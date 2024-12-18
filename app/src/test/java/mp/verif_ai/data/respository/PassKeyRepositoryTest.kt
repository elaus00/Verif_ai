package mp.verif_ai.data.respository

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import io.mockk.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import mp.verif_ai.data.repository.auth.PassKeyRepositoryImpl
import mp.verif_ai.domain.model.auth.passkey.PassKeyRegistrationResult
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.PublicKeyCredential

class PassKeyRepositoryTest {

    private lateinit var passKeyRepository: PassKeyRepositoryImpl
    private lateinit var credentialManager: CredentialManager
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var passKeysCollection: CollectionReference

    @Before
    fun setup() {
        credentialManager = mockk(relaxed = true)
        firestore = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        passKeysCollection = mockk(relaxed = true)

        every { firestore.collection("passkeys") } returns passKeysCollection

        passKeyRepository = PassKeyRepositoryImpl(
            firestore = firestore,
            auth = auth
        )
    }

    @Test
    fun `verifyPassKey returns true for valid credential`() = runTest {
        // Given
        val credentialId = "testCredentialId"
        val userId = "testUserId"
        val firebaseUser = mockk<FirebaseUser>()
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<DocumentSnapshot>>()
        val documentSnapshot = mockk<DocumentSnapshot>()

        every { firebaseUser.uid } returns userId
        every { auth.currentUser } returns firebaseUser
        every { passKeysCollection.document(credentialId) } returns docRef
        every { docRef.get() } returns task
        coEvery { task.await() } returns documentSnapshot
        every { documentSnapshot.exists() } returns true
        every { documentSnapshot.getString("userId") } returns userId

        // When
        val result = passKeyRepository.verifyPassKey(credentialId)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() ?: false)
    }


//    @Test
//    fun `signInWithPassKey handles invalid credential`() = runTest {
//        // Given
//        val context = mockk<Context>()
//        val getCredentialException = mockk<GetCredentialException>()
//
//        coEvery {
//            credentialManager.getCredential(
//                context = any(),
//                request = any<GetCredentialRequest>()
//            )
//        } throws getCredentialException
//
//        // When
//        val result = passKeyRepository.signInWithPassKey()
//
//        // Then
//        assertTrue(result.isSuccess)
//        val signInResult = result.getOrNull()
//        assertTrue(signInResult is PassKeySignInResult.Error)
//        assertTrue(signInResult.exception is PassKeyNoCredentialException)
//    }

    @Test
    fun `registerPassKey returns success for valid registration`() = runTest {
        // Given
        val activity = mockk<ComponentActivity>()
        val userId = "testUserId"
        val displayName = "Test User"
        val createResponse = mockk<CreatePublicKeyCredentialResponse>()
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<Void>>()
        val responseBundle = Bundle().apply {
            putString("type", "public-key")
            // 필요한 다른 데이터도 여기에 추가
        }

        every { createResponse.type } returns PublicKeyCredential.TYPE_PUBLIC_KEY_CREDENTIAL
        every { createResponse.data } returns responseBundle  // CreatePublicKeyCredentialResponse.data는 ByteArray 타입입니다
        coEvery {
            credentialManager.createCredential(
                activity,
                request = any<CreatePublicKeyCredentialRequest>()
            )
        } returns createResponse
        every { passKeysCollection.document(userId) } returns docRef
        every { docRef.set(any<Map<String, Any>>()) } returns task
        coEvery { task.await() } returns mockk()

        // When
        val result = passKeyRepository.registerPassKey(userId, displayName, activity)

        // Then
        assertTrue(result is PassKeyRegistrationResult.Success)
        assertEquals(PublicKeyCredential.TYPE_PUBLIC_KEY_CREDENTIAL, result.credentialId)
    }

    @Test
    fun `registerPassKey returns error when credential creation fails`() = runTest {
        // Given
        val activity = mockk<ComponentActivity>()
        val userId = "testUserId"
        val bundle = Bundle().apply {
            putString("androidx.credentials.provider.extra.CREATE_CREDENTIAL_EXCEPTION_TYPE", "CreateCredentialException")
            putCharSequence("androidx.credentials.provider.extra.CREATE_CREDENTIAL_EXCEPTION_MESSAGE", "Failed to create credential")
        }
        val exception = GetCredentialException.fromBundle(bundle)

        coEvery {
            credentialManager.createCredential(
                activity,
                any()
            )
        } throws exception

        // When
        val result = passKeyRepository.registerPassKey(userId, null, activity)

        // Then
        assertTrue(result is PassKeyRegistrationResult.Error)
        assertTrue(result.exception is IllegalStateException)
    }

    @Test
    fun `removePassKey returns success for valid deletion`() = runTest {
        // Given
        val credentialId = "testCredentialId"
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<Void>>()

        // Setup verifyPassKey to return true
        setupVerifyPassKeyMock(credentialId, true)

        every { passKeysCollection.document(credentialId) } returns docRef
        every { docRef.delete() } returns task
        coEvery { task.await() } returns mockk()

        // When
        val result = passKeyRepository.removePassKey(credentialId)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `removePassKey returns error when passkey does not exist`() = runTest {
        // Given
        val credentialId = "testCredentialId"
        setupVerifyPassKeyMock(credentialId, false)

        // When
        val result = passKeyRepository.removePassKey(credentialId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `updatePassKeyLastUsed returns success for valid update`() = runTest {
        // Given
        val credentialId = "testCredentialId"
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<Void>>()

        setupVerifyPassKeyMock(credentialId, true)
        every { passKeysCollection.document(credentialId) } returns docRef
        every { docRef.update(any<String>(), any<Long>()) } returns task
        coEvery { task.await() } returns mockk()

        // When
        val result = passKeyRepository.updatePassKeyLastUsed(credentialId)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `updatePassKeyLastUsed returns error for non-existent passkey`() = runTest {
        // Given
        val credentialId = "testCredentialId"
        setupVerifyPassKeyMock(credentialId, false)

        // When
        val result = passKeyRepository.updatePassKeyLastUsed(credentialId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `verifyPassKey handles firebase error`() = runTest {
        // Given
        val credentialId = "testCredentialId"
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<DocumentSnapshot>>()

        every { passKeysCollection.document(credentialId) } returns docRef
        every { docRef.get() } returns task
        coEvery { task.await() } throws FirebaseFirestoreException(
            "Network error",
            FirebaseFirestoreException.Code.UNAVAILABLE
        )

        // When
        val result = passKeyRepository.verifyPassKey(credentialId)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is FirebaseFirestoreException)
    }

    // Helper method for setting up verifyPassKey mocking
    private fun setupVerifyPassKeyMock(credentialId: String, exists: Boolean) {
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<DocumentSnapshot>>()
        val documentSnapshot = mockk<DocumentSnapshot>()
        val firebaseUser = mockk<FirebaseUser>()

        every { passKeysCollection.document(credentialId) } returns docRef
        every { docRef.get() } returns task
        coEvery { task.await() } returns documentSnapshot
        every { documentSnapshot.exists() } returns exists
        if (exists) {
            every { auth.currentUser } returns firebaseUser
            every { firebaseUser.uid } returns "testUserId"
            every { documentSnapshot.getString("userId") } returns "testUserId"
        }
    }
}