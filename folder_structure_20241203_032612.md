# Folder Structure - c:\Users\elaus\AndroidStudioProjects\Verif_ai
```
├── app/
│   ├── release/
│   ├── src/
│   │   ├── androidTest/
│   │   │   └── java/
│   │   │       └── mp/
│   │   │           └── verif_ai/
│   │   │               ├── data/
│   │   │               │   └── repository/
│   │   │               └── ExampleInstrumentedTest.kt
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── mp/
│   │   │   │       └── verif_ai/
│   │   │   │           ├── data/
│   │   │   │           │   └── repository/
│   │   │   │           │       ├── auth/
│   │   │   │           │       │   ├── AuthRepositoryImpl.kt
│   │   │   │           │       │   └── PassKeyRepositoryImpl.kt
│   │   │   │           │       ├── chat/
│   │   │   │           │       │   ├── PromptRepositoryImpl.kt
│   │   │   │           │       │   └── PromptSettingsRepositoryImpl.kt
│   │   │   │           │       ├── mock/
│   │   │   │           │       │   └── MockInboxRepositoryImpl.kt
│   │   │   │           │       └── InboxRepositoryImpl.kt
│   │   │   │           ├── di/
│   │   │   │           │   ├── OpenAIModule.kt
│   │   │   │           │   ├── PassKeyModule.kt
│   │   │   │           │   └── RepositoryModule.kt
│   │   │   │           ├── domain/
│   │   │   │           │   ├── model/
│   │   │   │           │   │   ├── auth/
│   │   │   │           │   │   │   ├── AuthCredential.kt
│   │   │   │           │   │   │   ├── AuthResult.kt
│   │   │   │           │   │   │   ├── AuthState.kt
│   │   │   │           │   │   │   ├── EmailVerification.kt
│   │   │   │           │   │   │   ├── Expert.kt
│   │   │   │           │   │   │   ├── ExpertFields.kt
│   │   │   │           │   │   │   ├── SignUpState.kt
│   │   │   │           │   │   │   └── User.kt
│   │   │   │           │   │   ├── extension/
│   │   │   │           │   │   │   ├── FirestoreDto.kt
│   │   │   │           │   │   │   └── VerificationConstants.kt
│   │   │   │           │   │   ├── notification/
│   │   │   │           │   │   │   └── Notification.kt
│   │   │   │           │   │   ├── passkey/
│   │   │   │           │   │   │   ├── PassKeyInfo.kt
│   │   │   │           │   │   │   ├── PassKeyRegistrationResult.kt
│   │   │   │           │   │   │   ├── PassKeySignInResult.kt
│   │   │   │           │   │   │   └── PassKeyStatus.kt
│   │   │   │           │   │   ├── payment/
│   │   │   │           │   │   │   ├── Payment.kt
│   │   │   │           │   │   │   └── Point.kt
│   │   │   │           │   │   ├── prompt/
│   │   │   │           │   │   │   ├── Conversation.kt
│   │   │   │           │   │   │   ├── PromptCategory.kt
│   │   │   │           │   │   │   ├── PromptException.kt
│   │   │   │           │   │   │   ├── PromptResponse.kt
│   │   │   │           │   │   │   ├── PromptTemplate.kt
│   │   │   │           │   │   │   └── UserPrompt.kt
│   │   │   │           │   │   └── question/
│   │   │   │           │   │       ├── Question.kt
│   │   │   │           │   │       └── TrendingQuestion.kt
│   │   │   │           │   ├── repository/
│   │   │   │           │   │   ├── AuthRepository.kt
│   │   │   │           │   │   ├── InboxRepository.kt
│   │   │   │           │   │   ├── PassKeyRepository.kt
│   │   │   │           │   │   ├── PromptRepository.kt
│   │   │   │           │   │   ├── PromptSettingsRepository.kt
│   │   │   │           │   │   └── UserRepository.kt
│   │   │   │           │   ├── usecase/
│   │   │   │           │   │   ├── auth/
│   │   │   │           │   │   ├── payment/
│   │   │   │           │   │   ├── prompt/
│   │   │   │           │   │   │   ├── GetPromptTemplatesUseCase.kt
│   │   │   │           │   │   │   └── SendPromptUseCase.kt
│   │   │   │           │   │   └── question/
│   │   │   │           │   └── util/
│   │   │   │           │       ├── config/
│   │   │   │           │       │   └── OpenAIConfig.kt
│   │   │   │           │       ├── passkey/
│   │   │   │           │       │   ├── PassKeyConfig.kt
│   │   │   │           │       │   └── PassKeyException.kt
│   │   │   │           │       └── DataFormatter.kt
│   │   │   │           ├── presentation/
│   │   │   │           │   ├── navigation/
│   │   │   │           │   │   ├── navigation.kt
│   │   │   │           │   │   └── NavigationBar.kt
│   │   │   │           │   ├── screens/
│   │   │   │           │   │   ├── answer/
│   │   │   │           │   │   ├── auth/
│   │   │   │           │   │   │   ├── expertsignup/
│   │   │   │           │   │   │   │   ├── ExpertOnboardingScreen.kt
│   │   │   │           │   │   │   │   ├── ExpertSubmitScreen.kt
│   │   │   │           │   │   │   │   └── ExpertVerificationScreen.kt
│   │   │   │           │   │   │   ├── signup/
│   │   │   │           │   │   │   │   ├── EmailVerificationScreen.kt
│   │   │   │           │   │   │   │   ├── PassWordSignUp.kt
│   │   │   │           │   │   │   │   ├── PhoneSignUp.kt
│   │   │   │           │   │   │   │   ├── SignUpFormScreen.kt
│   │   │   │           │   │   │   │   └── SignUpScreen.kt
│   │   │   │           │   │   │   ├── AuthComponents.kt
│   │   │   │           │   │   │   ├── OnBoarding.kt
│   │   │   │           │   │   │   ├── SignInOptions.kt
│   │   │   │           │   │   │   └── SignInScreen.kt
│   │   │   │           │   │   ├── home/
│   │   │   │           │   │   │   ├── question/
│   │   │   │           │   │   │   │   ├── Question.kt
│   │   │   │           │   │   │   │   ├── QuestionCreateScreen.kt
│   │   │   │           │   │   │   │   └── QuestionDetailScreen.kt
│   │   │   │           │   │   │   ├── HomeScreen.kt
│   │   │   │           │   │   │   └── HomeScreenComponents.kt
│   │   │   │           │   │   ├── inbox/
│   │   │   │           │   │   │   ├── InboxQuestionDetailScreen.kt
│   │   │   │           │   │   │   └── InboxScreen.kt
│   │   │   │           │   │   ├── prompt/
│   │   │   │           │   │   │   ├── components/
│   │   │   │           │   │   │   │   └── MessageComponent.kt
│   │   │   │           │   │   │   ├── PromptDetailScreen.kt
│   │   │   │           │   │   │   ├── PromptHistoryScreen.kt
│   │   │   │           │   │   │   ├── PromptScreen.kt
│   │   │   │           │   │   │   ├── PromptSettingScreen.kt
│   │   │   │           │   │   │   └── PromptTemplateScreen.kt
│   │   │   │           │   │   ├── settings/
│   │   │   │           │   │   │   ├── notification/
│   │   │   │           │   │   │   │   └── NotificationSettingsScreen.kt
│   │   │   │           │   │   │   ├── payment/
│   │   │   │           │   │   │   │   ├── PaymentMethodsScreen.kt
│   │   │   │           │   │   │   │   └── SubscriptionScreen.kt
│   │   │   │           │   │   │   ├── profile/
│   │   │   │           │   │   │   │   ├── ProfileEditScreen.kt
│   │   │   │           │   │   │   │   └── ProfileViewScreen.kt
│   │   │   │           │   │   │   └── SettingsScreen.kt
│   │   │   │           │   │   ├── theme/
│   │   │   │           │   │   │   ├── Button.kt
│   │   │   │           │   │   │   ├── Color.kt
│   │   │   │           │   │   │   ├── Input.kt
│   │   │   │           │   │   │   ├── Theme.kt
│   │   │   │           │   │   │   └── Type.kt
│   │   │   │           │   │   └── Screen.kt
│   │   │   │           │   ├── viewmodel/
│   │   │   │           │   │   ├── prompt/
│   │   │   │           │   │   │   ├── PromptDetailViewModel.kt
│   │   │   │           │   │   │   ├── PromptHistoryViewModel.kt
│   │   │   │           │   │   │   ├── PromptSettingsViewModel.kt
│   │   │   │           │   │   │   ├── PromptTemplateViewModel.kt
│   │   │   │           │   │   │   └── PromptViewModel.kt
│   │   │   │           │   │   ├── state/
│   │   │   │           │   │   │   ├── ChatUiState.kt
│   │   │   │           │   │   │   └── SignInUiState.kt
│   │   │   │           │   │   ├── AuthViewModel.kt
│   │   │   │           │   │   ├── HomeViewModel.kt
│   │   │   │           │   │   ├── InboxViewModel.kt
│   │   │   │           │   │   ├── PassKeyViewModel.kt
│   │   │   │           │   │   └── QuestionCreateViewModel.kt
│   │   │   │           │   └── MainActivity.kt
│   │   │   │           └── VerifAI.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── apple.xml
│   │   │   │   │   ├── back_arrow.xml
│   │   │   │   │   ├── check_circle.xml
│   │   │   │   │   ├── google.xml
│   │   │   │   │   ├── group.xml
│   │   │   │   │   ├── ic_google_logo.xml
│   │   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   │   ├── ic_launcher_foreground.xml
│   │   │   │   │   ├── image_14.xml
│   │   │   │   │   ├── image_15.xml
│   │   │   │   │   ├── notification_icon.xml
│   │   │   │   │   ├── upload.xml
│   │   │   │   │   ├── vector.xml
│   │   │   │   │   ├── vector2.xml
│   │   │   │   │   ├── vector3.xml
│   │   │   │   │   └── vector__1_.xml
│   │   │   │   ├── font/
│   │   │   │   │   ├── archivo_regular.ttf
│   │   │   │   │   ├── archivo_thin.ttf
│   │   │   │   │   ├── inter_extralight.ttf
│   │   │   │   │   └── inter_light.ttf
│   │   │   │   ├── layout/
│   │   │   │   ├── mipmap-anydpi-v26/
│   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   └── ic_launcher_round.xml
│   │   │   │   ├── mipmap-hdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-mdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xhdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xxhdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xxxhdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── font_certs.xml
│   │   │   │   │   ├── preloaded_fonts.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml
│   │   │   │       ├── data_extraction_rules.xml
│   │   │   │       └── provider.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── java/
│   │           └── mp/
│   │               └── verif_ai/
│   │                   ├── data/
│   │                   │   └── respository/
│   │                   │       └── PassKeyRepositoryTest.kt
│   │                   └── ExampleUnitTest.kt
│   ├── build.gradle.kts
│   ├── desktop.ini
│   ├── google-services.json
│   └── proguard-rules.pro
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml
├── build.gradle.kts
├── folder_structure_20241124_213456.md
├── folder_structure_20241203_032555.md
├── folder_structure_export.py
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
└── settings.gradle.kts
```
