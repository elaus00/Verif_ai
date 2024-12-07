package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.domain.service.AIModel

/**
 * Repository interface for managing AI responses and expert reviews.
 * Handles interactions with AI models and human expert reviewers.
 */
interface ResponseRepository {
    /**
     * Retrieves streaming response from specified AI model.
     * @param model AI model to use for generating response
     * @param prompt Input prompt for the AI model
     * @return Flow of response text chunks
     */
    suspend fun getAiResponse(model: AIModel, prompt: String): Flow<String>

    /**
     * Requests an expert review for a specific conversation.
     * @param conversationId ID of the conversation to review
     * @param points Number of points to offer for the review
     * @return Result indicating success or failure
     */
    suspend fun requestExpertReview(conversationId: String, points: Int): Result<Unit>

    /**
     * Observes expert reviews for a specific conversation.
     * @param conversationId ID of the conversation to get reviews for
     * @return Flow of expert review lists
     */
    suspend fun getExpertReviews(conversationId: String): Flow<List<ExpertReview>>
}