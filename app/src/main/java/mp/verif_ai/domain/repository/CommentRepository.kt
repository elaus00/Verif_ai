package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.question.Comment
import mp.verif_ai.domain.model.question.CommentParentType

interface CommentRepository {
    /**
     * 새로운 댓글을 생성합니다.
     */
    suspend fun createComment(comment: Comment): Result<String>

    /**
     * 특정 댓글을 조회합니다.
     */
    suspend fun getComment(commentId: String): Result<Comment>

    /**
     * 특정 부모(질문/답변)에 달린 댓글 목록을 실시간으로 관찰합니다.
     */
    suspend fun observeComments(parentId: String, parentType: CommentParentType): Flow<List<Comment>>

    /**
     * 댓글을 수정합니다.
     */
    suspend fun updateComment(comment: Comment): Result<Unit>

    /**
     * 댓글을 삭제합니다. (소프트 삭제)
     */
    suspend fun deleteComment(commentId: String): Result<Unit>

    /**
     * 댓글을 신고합니다.
     */
    suspend fun reportComment(commentId: String, reporterId: String): Result<Unit>
}
