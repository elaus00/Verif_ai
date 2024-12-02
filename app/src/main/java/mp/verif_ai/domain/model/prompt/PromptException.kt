package mp.verif_ai.domain.model.prompt

/**
 * 프롬프트 처리 중 발생하는 예외를 나타내는 클래스
 */
class PromptException : Exception {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)
}