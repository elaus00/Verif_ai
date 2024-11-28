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

    private val passKeysCollection = firestore.collection("passkeys")
    private val usersCollection = firestore.collection("users")
    val TAG: String = "PasskeyRepositoryImpl"

    fun generateChallenge(): ByteArray {
        return ByteArray(32).apply {
            SecureRandom().nextBytes(this)
        }
    }

    override suspend fun checkPassKeyStatus(context: ComponentActivity): PassKeyStatus = runCatching {
        val credentialManager = CredentialManager.create(context)
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
        Log.d(TAG, "checkPassKeyStatus failed: ${e.message}", e)
        when (e) {
            is PassKeyNotSupportedException -> {
                Log.d(TAG, "PassKey not supported on this device")
                PassKeyStatus.NOT_SUPPORTED
            }
            is PassKeyNoCredentialException -> {
                Log.d(TAG, "No PassKey credential found")
                PassKeyStatus.NO_CREDENTIAL
            }
            is PassKeyCancellationException -> {
                Log.d(TAG, "PassKey operation cancelled by user")
                PassKeyStatus.CANCELLED
            }
            else -> {
                Log.e(TAG, "Unexpected error in checkPassKeyStatus", e)
                PassKeyStatus.ERROR
            }
        }
    }

    @SuppressLint("PublicKeyCredential")
    override suspend fun registerPassKey(
        userId: String,
        displayName: String?,
        context: ComponentActivity
    ): PassKeyRegistrationResult = runCatching {
        val challenge = generateChallenge()
        val credentialManager = CredentialManager.create(context)

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
                Log.e(TAG, "Failed to create credential", e)
                throw IllegalStateException("Failed to create credential", e)
            }
        }

        when (response.type) {
            PublicKeyCredential.TYPE_PUBLIC_KEY_CREDENTIAL -> {
                val createPublicKeyResponse = response as CreatePublicKeyCredentialResponse
                Log.d(TAG, "Successfully created PublicKeyCredential")

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
                Log.e(TAG, "Unexpected credential type received: ${response.type}")
                throw IllegalStateException("Unexpected credential type received: ${response.type}")
            }
        }
    }.getOrElse { e ->
        Log.e(TAG, "PassKey registration failed", e)
        PassKeyRegistrationResult.Error(e as Exception)
    }

    override suspend fun signInWithPassKey(result: GetCredentialResponse): Result<PassKeySignInResult> = runCatching {
        val credential = result.credential

        when (credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                Log.d(TAG, "Successfully authenticated with PublicKeyCredential")
                PassKeySignInResult.Success(
                    credentialId = responseJson,
                    userId = auth.currentUser?.uid ?: throw IllegalStateException("User not found")
                )
            }
            is PasswordCredential -> {
                Log.d(TAG, "Authenticating with PasswordCredential")
                val result = auth.signInWithEmailAndPassword(credential.id, credential.password).await()
                val user = result.user ?: throw IllegalStateException("User not found")
                PassKeySignInResult.Success(
                    credentialId = credential.id,
                    userId = user.uid
                )
            }
            is GoogleIdTokenCredential -> {
                try {
                    Log.d(TAG, "Authenticating with GoogleIdTokenCredential")
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
                    Log.e(TAG, "Invalid Google ID token", e)
                    throw IllegalStateException("Invalid Google ID token", e)
                }
            }
            else -> {
                Log.e(TAG, "Unsupported credential type: ${credential.javaClass.simpleName}")
                throw IllegalStateException("Unsupported credential type: ${credential.javaClass.simpleName}")
            }
        }
    }

    override suspend fun getRegisteredPassKeys(userId: String): Result<List<PassKeyInfo>> = runCatching {
        Log.d(TAG, "Fetching registered PassKeys for user: $userId")
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
            }.also { passKeys ->
                Log.d(TAG, "Found ${passKeys.size} PassKeys for user: $userId")
            }
    }

    override suspend fun removePassKey(credentialId: String): Result<Unit> = runCatching {
        Log.d(TAG, "Attempting to remove PassKey: $credentialId")
        val exists = verifyPassKey(credentialId).getOrThrow()
        if (!exists) {
            Log.e(TAG, "PassKey not found: $credentialId")
            throw IllegalStateException("PassKey not found")
        }

        passKeysCollection
            .document(credentialId)
            .delete()
            .await()
        Log.d(TAG, "Successfully removed PassKey: $credentialId")
    }

    override suspend fun savePassKeyToFirebase(passKeyInfo: PassKeyInfo): Result<Unit> = runCatching {
        Log.d(TAG, "Saving PassKey to Firebase: ${passKeyInfo.credentialId}")
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
        Log.d(TAG, "Successfully saved PassKey to Firebase: ${passKeyInfo.credentialId}")
    }

    override suspend fun updatePassKeyLastUsed(credentialId: String): Result<Unit> = runCatching {
        Log.d(TAG, "Updating last used timestamp for PassKey: $credentialId")
        val exists = verifyPassKey(credentialId).getOrThrow()
        if (!exists) {
            Log.e(TAG, "PassKey not found: $credentialId")
            throw IllegalStateException("PassKey not found")
        }

        passKeysCollection
            .document(credentialId)
            .update("lastUsedAt", System.currentTimeMillis())
            .await()
        Log.d(TAG, "Successfully updated last used timestamp for PassKey: $credentialId")
    }

    override suspend fun verifyPassKey(credentialId: String): Result<Boolean> = runCatching {
        Log.d(TAG, "Verifying PassKey: $credentialId")
        val doc = passKeysCollection
            .document(credentialId)
            .get()
            .await()

        if (doc.exists()) {
            val userId = doc.getString("userId")
            val currentUserId = auth.currentUser?.uid
            val isValid = userId == currentUserId
            Log.d(TAG, "PassKey verification result: $isValid")
            isValid
        } else {
            Log.d(TAG, "PassKey not found: $credentialId")
            false
        }
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

    private fun FirebaseUser.toUser(): User {
        return User(
            id = uid,
            email = email.toString(),
            emailVerified = isEmailVerified,
            displayName = displayName
        )
    }
}