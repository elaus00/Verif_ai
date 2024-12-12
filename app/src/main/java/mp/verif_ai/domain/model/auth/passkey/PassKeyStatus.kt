package mp.verif_ai.domain.model.auth.passkey

enum class PassKeyStatus {
    AVAILABLE,        // PassKey 사용 가능
    NOT_SUPPORTED,    // 기기가 PassKey를 지원하지 않음
    NOT_ALLOWED,      // 사용자가 PassKey 사용을 허용하지 않음
    NO_CREDENTIAL,    // 등록된 PassKey가 없음
    CANCELLED,
    ERROR            // 기타 오류
}