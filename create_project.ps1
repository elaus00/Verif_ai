# create_project.ps1

# Set UTF8 encoding for file creation
$OutputEncoding = [System.Text.Encoding]::UTF8

# Root directory creation
New-Item -ItemType Directory -Force -Path @(
    "app/src/main/java/mp/verif_ai",
    "app/src/main/res/layout",
    "app/src/main/java/mp/verif_ai/presentation/base",
    "app/src/main/java/mp/verif_ai/presentation/common/components",
    "app/src/main/java/mp/verif_ai/presentation/common/extensions",
    "app/src/main/java/mp/verif_ai/presentation/common/utils",
    "app/src/main/java/mp/verif_ai/presentation/auth/login",
    "app/src/main/java/mp/verif_ai/presentation/auth/register",
    "app/src/main/java/mp/verif_ai/presentation/question",
    "app/src/main/java/mp/verif_ai/presentation/answer",
    "app/src/main/java/mp/verif_ai/presentation/chat",
    "app/src/main/java/mp/verif_ai/presentation/payment",
    "app/src/main/java/mp/verif_ai/presentation/notification",
    "app/src/main/java/mp/verif_ai/domain/base",
    "app/src/main/java/mp/verif_ai/domain/entity",
    "app/src/main/java/mp/verif_ai/domain/repository",
    "app/src/main/java/mp/verif_ai/domain/usecase/auth",
    "app/src/main/java/mp/verif_ai/domain/usecase/question",
    "app/src/main/java/mp/verif_ai/domain/usecase/payment",
    "app/src/main/java/mp/verif_ai/data/repository",
    "app/src/main/java/mp/verif_ai/data/remote/api",
    "app/src/main/java/mp/verif_ai/data/remote/model",
    "app/src/main/java/mp/verif_ai/data/remote/source",
    "app/src/main/java/mp/verif_ai/data/mapper",
    "app/src/main/java/mp/verif_ai/di/module",
    "app/src/main/java/mp/verif_ai/di/component"
)

# Create base files
@"
package mp.verif_ai.presentation.base

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity()
"@ | Out-File -FilePath "app/src/main/java/mp/verif_ai/presentation/base/BaseActivity.kt" -Encoding UTF8

@"
package mp.verif_ai.presentation.base

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment()
"@ | Out-File -FilePath "app/src/main/java/mp/verif_ai/presentation/base/BaseFragment.kt" -Encoding UTF8

@"
package mp.verif_ai.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()
    
    protected fun launch(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                _loading.value = true
                block()
            } catch (e: Exception) {
                _error.emit(e.message ?: "Unknown error")
            } finally {
                _loading.value = false
            }
        }
    }
}
"@ | Out-File -FilePath "app/src/main/java/mp/verif_ai/presentation/base/BaseViewModel.kt" -Encoding UTF8

@"
package mp.verif_ai.domain.base

abstract class BaseUseCase<in P, R> {
    suspend operator fun invoke(parameters: P): Result<R> = runCatching {
        execute(parameters)
    }

    protected abstract suspend fun execute(parameters: P): R
}
"@ | Out-File -FilePath "app/src/main/java/mp/verif_ai/domain/base/BaseUseCase.kt" -Encoding UTF8

@"
package mp.verif_ai.domain.base

interface BaseRepository
"@ | Out-File -FilePath "app/src/main/java/mp/verif_ai/domain/base/BaseRepository.kt" -Encoding UTF8

@"
package mp.verif_ai.di.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule
"@ | Out-File -FilePath "app/src/main/java/mp/verif_ai/di/module/NetworkModule.kt" -Encoding UTF8

@"
package mp.verif_ai.di.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule
"@ | Out-File -FilePath "app/src/main/java/mp/verif_ai/di/module/RepositoryModule.kt" -Encoding UTF8

@"
package mp.verif_ai.di.module

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule
"@ | Out-File -FilePath "app/src/main/java/mp/verif_ai/di/module/ViewModelModule.kt" -Encoding UTF8

# Update build.gradle file dependencies if needed
$buildGradleContent = Get-Content "app/build.gradle" -Raw
$updatedBuildGradle = $buildGradleContent -replace 'applicationId ".*"', 'applicationId "mp.verif_ai"'
$updatedBuildGradle | Out-File -FilePath "app/build.gradle" -Encoding UTF8

Write-Host "Project structure has been created successfully!" -ForegroundColor Green
Write-Host "Please make sure your Android Studio project is already created with package name 'mp.verif_ai'" -ForegroundColor Yellow