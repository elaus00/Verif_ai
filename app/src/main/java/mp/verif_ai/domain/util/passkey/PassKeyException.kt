package mp.verif_ai.domain.util.passkey

import android.os.Bundle
import androidx.credentials.exceptions.GetCredentialException

abstract class PassKeyException(
    open val type: String,
    open val errorMessage: String? = null
) : Exception(errorMessage) {
    companion object {
        private const val EXTRA_PASSKEY_EXCEPTION_TYPE = "extra.PASSKEY_EXCEPTION_TYPE"
        private const val EXTRA_PASSKEY_EXCEPTION_MESSAGE = "extra.PASSKEY_EXCEPTION_MESSAGE"

        fun asBundle(ex: PassKeyException): Bundle {
            return Bundle().apply {
                putString(EXTRA_PASSKEY_EXCEPTION_TYPE, ex.type)
                ex.errorMessage?.let {
                    putString(EXTRA_PASSKEY_EXCEPTION_MESSAGE, it)
                }
            }
        }

        fun fromBundle(bundle: Bundle): PassKeyException {
            val type = bundle.getString(EXTRA_PASSKEY_EXCEPTION_TYPE)
                ?: throw IllegalArgumentException("Bundle was missing exception type.")
            val message = bundle.getString(EXTRA_PASSKEY_EXCEPTION_MESSAGE)

            return when (type) {
                PassKeyNotSupportedException.TYPE -> PassKeyNotSupportedException(message)
                PassKeyNoCredentialException.TYPE -> PassKeyNoCredentialException(message)
                PassKeyCancellationException.TYPE -> PassKeyCancellationException(message)
                else -> PassKeyUnknownException(message)
            }
        }
    }
}

class PassKeyNotSupportedException(
    override val errorMessage: String? = null
) : PassKeyException(TYPE, errorMessage) {
    companion object {
        const val TYPE = "ERROR_NOT_SUPPORTED"
    }
}

class PassKeyNoCredentialException(
    override val errorMessage: String? = null
) : PassKeyException(TYPE, errorMessage) {
    companion object {
        const val TYPE = "ERROR_NO_CREDENTIAL"
    }
}

class PassKeyCancellationException(
    override val errorMessage: String? = null
) : PassKeyException(TYPE, errorMessage) {
    companion object {
        const val TYPE = "ERROR_CANCELLED"
    }
}

class PassKeyUnknownException(
    override val errorMessage: String? = null
) : PassKeyException(TYPE, errorMessage) {
    companion object {
        const val TYPE = "ERROR_UNKNOWN"
    }
}