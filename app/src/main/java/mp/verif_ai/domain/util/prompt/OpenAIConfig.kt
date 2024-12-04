package mp.verif_ai.domain.util.prompt

import android.R.attr.apiKey
import com.aallam.openai.api.http.Timeout
import kotlin.time.Duration.Companion.seconds

class OpenAIConfig(
    token: Int = apiKey,
    timeout: Timeout = Timeout(socket = 60.seconds),
)