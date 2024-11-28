package mp.verif_ai.data.auth

import android.annotation.SuppressLint
import android.content.Context
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialResponse
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialRequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import mp.verif_ai.domain.model.passkey.PassKeyInfo
import mp.verif_ai.domain.model.passkey.PassKeyRegistrationResult
import mp.verif_ai.domain.model.passkey.PassKeySignInResult
import mp.verif_ai.domain.model.passkey.PassKeyStatus
import mp.verif_ai.domain.repository.PassKeyRepository
import javax.inject.Inject
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.Credential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.fido.fido2.api.common.AuthenticatorAssertionResponse
import com.google.android.gms.fido.fido2.api.common.AuthenticatorAttestationResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import mp.verif_ai.domain.util.passkey.PassKeyCancellationException
import mp.verif_ai.domain.util.passkey.PassKeyConfig
import mp.verif_ai.domain.util.passkey.PassKeyConfig.getRequestOptions
import mp.verif_ai.domain.util.passkey.PassKeyNoCredentialException
import mp.verif_ai.domain.util.passkey.PassKeyNotSupportedException
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import mp.verif_ai.domain.model.auth.User
import java.security.SecureRandom

class PassKeyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PassKeyRepository {

    val context: ComponentActivity = ComponentActivity()
    val credentialManager = CredentialManager.create(context)
    private val passKeysCollection = firestore.collection("passkeys")
    private val usersCollection = firestore.collection("users")

    override suspend fun checkPassKeyStatus(): PassKeyStatus = runCatching {
        val challenge = generateChallenge()

        val requestOptionsJson = getRequestOptions(challenge.toString())

        val credentialRequest = GetCredentialRequest(
            listOf(
                GetPublicKeyCredentialOption(
                    requestJson = requestOptionsJson
                )
            )
        )

        credentialManager.getCredential(context, credentialRequest)
        PassKeyStatus.AVAILABLE

    }.getOrElse { e ->
        when (e) {
            is PassKeyNotSupportedException -> PassKeyStatus.NOT_SUPPORTED
            is PassKeyNoCredentialException -> PassKeyStatus.NO_CREDENTIAL
            is PassKeyCancellationException -> PassKeyStatus.CANCELLED
            else -> PassKeyStatus.ERROR
        }
    }

    @SuppressLint("PublicKeyCredential")
    override suspend fun registerPassKey(
        userId: String,
        displayName: String?
    ): PassKeyRegistrationResult = runCatching {
        val challenge = generateChallenge()

        val request = CreatePublicKeyCredentialRequest(
            requestJson = PassKeyConfig.getCreateOptions(
                userId = userId,
                displayName = displayName ?: userId,
                challenge = challenge.toString()
            ),
        )

        val response = coroutineScope {
            try {
                credentialManager.createCredential(
                    context,
                    request
                )
            } catch (e: GetCredentialException) {
                throw IllegalStateException("Failed to create credential", e)
            }
        }

        when (response.type) {
            PublicKeyCredential.TYPE_PUBLIC_KEY_CREDENTIAL -> {
                val createPublicKeyResponse = response as CreatePublicKeyCredentialResponse

                passKeysCollection.document(userId).set(
                    hashMapOf(
                        "username" to userId,
                        "displayName" to displayName,
                        "responseType" to response.type,
                        "responseData" to response.data.toString(),
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                ).await()

                PassKeyRegistrationResult.Success(response.type)
            }
            else -> {
                throw IllegalStateException("Unexpected credential type received: ${response.type}")
            }
        }
    }.getOrElse { e ->
        PassKeyRegistrationResult.Error(e as Exception)
    }

    override suspend fun signInWithPassKey(result: GetCredentialResponse): Result<PassKeySignInResult> = runCatching {
        val credential = result.credential

        when (credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                PassKeySignInResult.Success(
                    credentialId = responseJson,
                    userId = auth.currentUser?.uid ?: throw IllegalStateException("User not found")
                )
            }
            is PasswordCredential -> {
                val result = auth.signInWithEmailAndPassword(credential.id, credential.password).await()
                val user = result.user ?: throw IllegalStateException("User not found")
                PassKeySignInResult.Success(
                    credentialId = credential.id,
                    userId = user.uid
                )
            }
            is GoogleIdTokenCredential -> {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val result = auth.signInWithCredential(firebaseCredential).await()
                    val user = result.user ?: throw IllegalStateException("User not found")

                    val userData = hashMapOf(
                        "email" to user.email,
                        "displayName" to user.displayName,
                        "lastSignInAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )

                    usersCollection.document(user.uid)
                        .set(userData, SetOptions.merge())
                        .await()

                    PassKeySignInResult.Success(
                        credentialId = idToken,
                        userId = user.uid
                    )
                } catch (e: GoogleIdTokenParsingException) {
                    throw IllegalStateException("Invalid Google ID token", e)
                }
            }
            else -> throw IllegalStateException("Unsupported credential type: ${credential.javaClass.simpleName}")
        }
    }

    fun generateChallenge(): ByteArray {
        return ByteArray(32).apply {
            SecureRandom().nextBytes(this)
        }
    }

    override suspend fun getRegisteredPassKeys(userId: String): Result<List<PassKeyInfo>> = runCatching {
        passKeysCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                doc.data?.let { data ->
                    PassKeyInfo(
                        credentialId = data["credentialId"] as String,
                        userId = data["userId"] as String,
                        publicKey = data["publicKey"] as String,
                        name = data["name"] as? String,
                        createdAt = data["createdAt"] as Long,
                        lastUsedAt = data["lastUsedAt"] as Long
                    )
                }
            }
    }

    override suspend fun removePassKey(credentialId: String): Result<Unit> = runCatching {
        // 패스키 존재 여부 확인
        val exists = verifyPassKey(credentialId).getOrThrow()
        if (!exists) {
            throw IllegalStateException("PassKey not found")
        }

        passKeysCollection
            .document(credentialId)
            .delete()
            .await()
    }

    override fun observePassKeys(userId: String): Flow<List<PassKeyInfo>> {
        return passKeysCollection
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.data?.let { data ->
                        PassKeyInfo(
                            credentialId = data["credentialId"] as String,
                            userId = data["userId"] as String,
                            publicKey = data["publicKey"] as String,
                            name = data["name"] as? String,
                            createdAt = data["createdAt"] as Long,
                            lastUsedAt = data["lastUsedAt"] as Long
                        )
                    }
                }
            }
    }

    override suspend fun savePassKeyToFirebase(passKeyInfo: PassKeyInfo): Result<Unit> = runCatching {
        val passKeyData = mapOf(
            "credentialId" to passKeyInfo.credentialId,
            "userId" to passKeyInfo.userId,
            "publicKey" to passKeyInfo.publicKey,
            "name" to passKeyInfo.name,
            "createdAt" to passKeyInfo.createdAt,
            "lastUsedAt" to passKeyInfo.lastUsedAt
        )

        passKeysCollection
            .document(passKeyInfo.credentialId)
            .set(passKeyData)
            .await()
    }

    override suspend fun updatePassKeyLastUsed(credentialId: String): Result<Unit> = runCatching {
        // 패스키 존재 여부 확인
        val exists = verifyPassKey(credentialId).getOrThrow()
        if (!exists) {
            throw IllegalStateException("PassKey not found")
        }

        passKeysCollection
            .document(credentialId)
            .update("lastUsedAt", System.currentTimeMillis())
            .await()
    }

    override suspend fun verifyPassKey(credentialId: String): Result<Boolean> = runCatching {
        val doc = passKeysCollection
            .document(credentialId)
            .get()
            .await()

        // 문서 존재 여부와 현재 사용자와 패스키의 사용자 ID가 일치하는지 확인
        if (doc.exists()) {
            val userId = doc.getString("userId")
            val currentUserId = auth.currentUser?.uid
            userId == currentUserId
        } else {
            false
        }
    }

    private fun FirebaseUser.toUser(): User {
        return User(
            id = uid,
            email = email.toString(),
            emailVerified = isEmailVerified,
            displayName = displayName
        )
    }
}