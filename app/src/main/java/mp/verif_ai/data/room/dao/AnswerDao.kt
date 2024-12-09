package mp.verif_ai.data.room.dao

import androidx.room.*
import mp.verif_ai.domain.room.AnswerEntity

@Dao
interface AnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: AnswerEntity)

    @Query("SELECT * FROM answers WHERE id = :id")
    suspend fun getAnswerById(id: String): AnswerEntity?

    @Query("SELECT * FROM answers")
    suspend fun getAllAnswers(): List<AnswerEntity>

    @Delete
    suspend fun deleteAnswer(answer: AnswerEntity)
}
