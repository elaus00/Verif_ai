package mp.verif_ai.data.util

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.ConversationStatus
import mp.verif_ai.domain.model.conversation.ConversationType
import mp.verif_ai.domain.model.conversation.ParticipantType
import mp.verif_ai.domain.model.conversation.ParticipationStatus
import javax.inject.Inject
import kotlin.String
import kotlin.collections.Map

class ConversationMapper @Inject constructor() {
    suspend fun createConversationListener(
        conversationId: String,
        collection: CollectionReference,
        onConversation: suspend (Conversation) -> Unit
    ): ListenerRegistration {
        return collection.document(conversationId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ConversationMapper", "Error in conversation listener", error)
                    return@addSnapshotListener
                }

                snapshot?.let { doc ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            mapDocumentToConversation(doc)?.let { conversation ->
                                onConversation(conversation)
                            }
                        } catch (e: Exception) {
                            Log.e("ConversationMapper", "Error mapping conversation", e)
                        }
                    }
                }
            }
    }

    suspend fun mapDocumentToConversation(doc: DocumentSnapshot): Conversation? {
        val data = doc.data ?: return null
        return Conversation(
            id = doc.id,
            title = data["title"] as? String ?: "",
            participantIds = (data["participantIds"] as? List<*>)?.mapNotNull { it as? String }
                ?: emptyList(),
            participantTypes = mapParticipantTypes(data.getOrDefault("participantTypes", emptyMap<String, Any>()) as Map<*, *>),
            participantStatuses = mapParticipantStatuses(data.getOrDefault("participantStatuses", emptyMap<String, Any>()) as Map<*, *>),
            messages = emptyList(),
            type = mapConversationType(data["type"] as? String),
            status = mapConversationStatus(data["status"] as? String),
            category = data["category"] as? String,
            tags = (data["tags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }

    fun mapParticipantTypes(data: Map<*, *>): Map<String, ParticipantType> {
        return data.mapNotNull { entry ->
            val id = entry.key as? String ?: return@mapNotNull null
            val type = try {
                ParticipantType.valueOf(entry.value as? String ?: return@mapNotNull null)
            } catch (e: IllegalArgumentException) {
                return@mapNotNull null
            }
            id to type
        }.toMap()
    }

    fun mapParticipantStatuses(data: Map<*, *>): Map<String, ParticipationStatus> {
        return data.mapNotNull { entry ->
            val id = entry.key as? String ?: return@mapNotNull null
            val status = try {
                ParticipationStatus.valueOf(entry.value as? String ?: return@mapNotNull null)
            } catch (e: IllegalArgumentException) {
                return@mapNotNull null
            }
            id to status
        }.toMap()
    }

    private fun mapConversationType(value: String?): ConversationType {
        return try {
            ConversationType.valueOf(value ?: ConversationType.AI_CHAT.name)
        } catch (e: IllegalArgumentException) {
            ConversationType.AI_CHAT
        }
    }

    private fun mapConversationStatus(value: String?): ConversationStatus {
        return try {
            ConversationStatus.valueOf(value ?: ConversationStatus.ACTIVE.name)
        } catch (e: IllegalArgumentException) {
            ConversationStatus.ACTIVE
        }
    }

    suspend fun getFirestoreConversations(
        collection: CollectionReference,
        userId: String,
        limit: Int
    ): List<Conversation> {
        return collection
            .whereArrayContains("participants", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                mapDocumentToConversation(doc)
            }
    }

    suspend fun getFirestoreConversation(
        collection: CollectionReference,
        conversationId: String
    ): Conversation {
        val doc = collection
            .document(conversationId)
            .get()
            .await()

        return mapDocumentToConversation(doc)
            ?: throw IllegalStateException("Conversation not found")
    }
}