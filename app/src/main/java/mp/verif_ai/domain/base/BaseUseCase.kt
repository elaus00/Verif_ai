package mp.verif_ai.domain.base

abstract class BaseUseCase<in P, R> {
    suspend operator fun invoke(parameters: P): Result<R> = runCatching {
        execute(parameters)
    }

    protected abstract suspend fun execute(parameters: P): R
}
