package mp.verif_ai.data.repository.auth

import android.annotation.SuppressLint
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import mp.verif_ai.domain.model.passkey.PassKeyInfo
import mp.verif_ai.domain.model.passkey.PassKeyRegistrationResult
import mp.verif_ai.domain.model.passkey.PassKeySignInResult
import mp.verif_ai.domain.repository.PassKeyRepository
import javax.inject.Inject
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.coroutineScope
import mp.verif_ai.domain.util.passkey.PassKeyConfig
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import mp.verif_ai.domain.model.auth.User
import org.json.JSONObject
import java.security.SecureRandom

class PassKeyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PassKeyRepository {

    private val passKeysCollection = firestore.collection("passkeys")
    private val usersCollection = firestore.collection("users")
    val logTag: String = "PasskeyRepositoryImpl"

    fun generateChallenge(): ByteArray {
        return ByteArray(32).apply {
            SecureRandom().nextBytes(this)
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

        val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
            requestJson = PassKeyConfig.getCreateOptions(
                userId = userId,
                displayName = displayName ?: userId,
            ),
            isAutoSelectAllowed = true, // 단일 옵션 자동 선택 허용
            preferImmediatelyAvailableCredentials = true
        )

        val response = coroutineScope {
            try {
                credentialManager.createCredential(
                    context,
                    createPublicKeyCredentialRequest
                )
            } catch (e: GetCredentialException) {
                Log.e(logTag, "Failed to create credential", e)
                throw IllegalStateException("Failed to create credential", e)
            }
        }

        when (response.type) {
            PublicKeyCredential.TYPE_PUBLIC_KEY_CREDENTIAL -> {
                Log.d(logTag, "Successfully created PublicKeyCredential")

                val createResponse = response as CreatePublicKeyCredentialResponse
                val registrationData = JSONObject(createResponse.registrationResponseJson)

                // 로깅 추가
                Log.d(logTag, "Registration Response: $registrationData")

                val passKeyInfo = PassKeyInfo(
                    credentialId = registrationData.optString("id", userId),
                    publicKeyData = createResponse.registrationResponseJson,
                    userId = userId,
                    displayName = displayName,
                    clientDataHash = challenge.toString(),
                    deviceInfo = PassKeyInfo.DeviceInfo()
                )

                savePassKeyToFirebase(passKeyInfo).getOrThrow()

                PassKeyRegistrationResult.Success(response.type)
            }
            else -> {
                Log.e(logTag, "Unexpected credential type received: ${response.type}")
                throw IllegalStateException("Unexpected credential type received: ${response.type}")
            }
        }
    }.getOrElse { e ->
        Log.e(logTag, "PassKey registration failed", e)
        PassKeyRegistrationResult.Error(e as Exception)
    }

    override suspend fun signInWithPassKey(result: GetCredentialResponse): Result<PassKeySignInResult> = runCatching {
        val credential = result.credential

        when (credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                Log.d(logTag, "Successfully authenticated with PublicKeyCredential")
                PassKeySignInResult.Success(
                    credentialId = responseJson,
                    userId = auth.currentUser?.uid ?: throw IllegalStateException("User not found")
                )
            }
            is PasswordCredential -> {
                Log.d(logTag, "Authenticating with PasswordCredential")
                val result = auth.signInWithEmailAndPassword(credential.id, credential.password).await()
                val user = result.user ?: throw IllegalStateException("User not found")
                PassKeySignInResult.Success(
                    credentialId = credential.id,
                    userId = user.uid
                )
            }
            is GoogleIdTokenCredential -> {
                try {
                    Log.d(logTag, "Authenticating with GoogleIdTokenCredential")
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
                    Log.e(logTag, "Invalid Google ID token", e)
                    throw IllegalStateException("Invalid Google ID token", e)
                }
            }
            else -> {
                Log.e(logTag, "Unsupported credential type: ${credential.javaClass.simpleName}")
                throw IllegalStateException("Unsupported credential type: ${credential.javaClass.simpleName}")
            }
        }
    }

    override suspend fun getRegisteredPassKeys(userId: String): Result<List<PassKeyInfo>> = runCatching {
        Log.d(logTag, "Fetching registered PassKeys for user: $userId")
        passKeysCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                doc.data?.let { data ->
                    PassKeyInfo(
                        credentialId = data["credentialId"] as String,
                        publicKeyData = data["publicKeyData"] as String,
                        userId = data["userId"] as String,
                        displayName = data["displayName"] as? String,
                        clientDataHash = data["clientDataHash"] as? String,
                        createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                        lastUsedAt = (data["lastUsedAt"] as? Number)?.toLong(),
                        deviceInfo = if (data.containsKey("deviceModel")) {
                            PassKeyInfo.DeviceInfo(
                                model = data["deviceModel"] as String,
                                manufacturer = data["deviceManufacturer"] as String,
                                sdkVersion = (data["deviceSdkVersion"] as Number).toInt()
                            )
                        } else null
                    )
                }
            }.also { passKeys ->
                Log.d(logTag, "Found ${passKeys.size} PassKeys for user: $userId")
            }
    }

    override suspend fun removePassKey(credentialId: String): Result<Unit> = runCatching {
        Log.d(logTag, "Attempting to remove PassKey: $credentialId")
        val exists = verifyPassKey(credentialId).getOrThrow()
        if (!exists) {
            Log.e(logTag, "PassKey not found: $credentialId")
            throw IllegalStateException("PassKey not found")
        }

        passKeysCollection
            .document(credentialId)
            .delete()
            .await()
        Log.d(logTag, "Successfully removed PassKey: $credentialId")
    }

    override suspend fun savePassKeyToFirebase(passKeyInfo: PassKeyInfo): Result<Unit> = runCatching {
        Log.d(logTag, "Saving PassKey to Firebase: ${passKeyInfo.credentialId}")
        val passKeyData = passKeyInfo.toMap()  // PassKeyInfo의 toMap() 메서드 사용

        passKeysCollection
            .document(passKeyInfo.credentialId)
            .set(passKeyData)
            .await()
        Log.d(logTag, "Successfully saved PassKey to Firebase: ${passKeyInfo.credentialId}")
    }

    override suspend fun updatePassKeyLastUsed(credentialId: String): Result<Unit> = runCatching {
        Log.d(logTag, "Updating last used timestamp for PassKey: $credentialId")
        val exists = verifyPassKey(credentialId).getOrThrow()
        if (!exists) {
            Log.e(logTag, "PassKey not found: $credentialId")
            throw IllegalStateException("PassKey not found")
        }

        passKeysCollection
            .document(credentialId)
            .update("lastUsedAt", System.currentTimeMillis())
            .await()
        Log.d(logTag, "Successfully updated last used timestamp for PassKey: $credentialId")
    }

    override suspend fun verifyPassKey(credentialId: String): Result<Boolean> = runCatching {
        Log.d(logTag, "Verifying PassKey: $credentialId")
        val doc = passKeysCollection
            .document(credentialId)
            .get()
            .await()

        if (doc.exists()) {
            val userId = doc.getString("userId")
            val currentUserId = auth.currentUser?.uid
            val isValid = userId == currentUserId
            Log.d(logTag, "PassKey verification result: $isValid")
            isValid
        } else {
            Log.d(logTag, "PassKey not found: $credentialId")
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
                            publicKeyData = data["publicKeyData"] as String,
                            userId = data["userId"] as String,
                            displayName = data["displayName"] as? String,
                            clientDataHash = data["clientDataHash"] as? String,
                            createdAt = (data["createdAt"] as? Number)?.toLong()
                                ?: System.currentTimeMillis(),
                            lastUsedAt = (data["lastUsedAt"] as? Number)?.toLong(),
                            deviceInfo = if (data.containsKey("deviceModel")) {
                                PassKeyInfo.DeviceInfo(
                                    model = data["deviceModel"] as String,
                                    manufacturer = data["deviceManufacturer"] as String,
                                    sdkVersion = (data["deviceSdkVersion"] as Number).toInt()
                                )
                            } else null
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