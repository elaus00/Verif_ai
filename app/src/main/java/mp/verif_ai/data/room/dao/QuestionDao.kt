package mp.verif_ai.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import mp.verif_ai.domain.room.QuestionEntity

@Dao
interface QuestionDao {
    @Insert
    suspend fun insertQuestion(question: QuestionEntity)

    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<QuestionEntity>
}