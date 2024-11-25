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
│   │   │   │           │   ├── firebase/
│   │   │   │           │   │   ├── repository/
│   │   │   │           │   │   │   ├── FirebaseAuthRepositoryImpl.kt
│   │   │   │           │   │   │   └── FirebaseUserRepositoryImpl.kt
│   │   │   │           │   │   └── FirebaseModule.kt
│   │   │   │           │   └── repository/
│   │   │   │           │       ├── mock/
│   │   │   │           │       │   └── MockInboxRepositoryImpl.kt
│   │   │   │           │       └── InboxRepositoryImpl.kt
│   │   │   │           ├── di/
│   │   │   │           │   ├── RepositoryModule.kt
│   │   │   │           │   ├── UseCaseModule.kt
│   │   │   │           │   └── ViewModelModule.kt
│   │   │   │           ├── domain/
│   │   │   │           │   ├── model/
│   │   │   │           │   │   ├── auth/
│   │   │   │           │   │   │   ├── EmailVerification.kt
│   │   │   │           │   │   │   ├── Expert.kt
│   │   │   │           │   │   │   └── User.kt
│   │   │   │           │   │   ├── chat/
│   │   │   │           │   │   │   ├── Answer.kt
│   │   │   │           │   │   │   ├── Conversation.kt
│   │   │   │           │   │   │   ├── Message.kt
│   │   │   │           │   │   │   └── Question.kt
│   │   │   │           │   │   ├── extension/
│   │   │   │           │   │   │   ├── FirestoreDto.kt
│   │   │   │           │   │   │   └── VerificationConstants.kt
│   │   │   │           │   │   ├── Dispute.kt
│   │   │   │           │   │   ├── Notification.kt
│   │   │   │           │   │   ├── Payment.kt
│   │   │   │           │   │   ├── Point.kt
│   │   │   │           │   │   └── TrendingQuestion.kt
│   │   │   │           │   ├── repository/
│   │   │   │           │   │   ├── AuthRepository.kt
│   │   │   │           │   │   ├── InboxRepository.kt
│   │   │   │           │   │   └── UserRepository.kt
│   │   │   │           │   └── usecase/
│   │   │   │           │       ├── auth/
│   │   │   │           │       │   ├── SignInUseCase.kt
│   │   │   │           │       │   ├── SignUpUseCase.kt
│   │   │   │           │       │   └── VerifyEmailUseCase.kt
│   │   │   │           │       ├── payment/
│   │   │   │           │       ├── question/
│   │   │   │           │       └── user/
│   │   │   │           │           ├── CreateUserUseCase.kt
│   │   │   │           │           └── GetUserUseCase.kt
│   │   │   │           ├── presentation/
│   │   │   │           │   ├── navigation/
│   │   │   │           │   │   ├── navigation.kt
│   │   │   │           │   │   └── NavigationBar.kt
│   │   │   │           │   ├── screens/
│   │   │   │           │   │   ├── answer/
│   │   │   │           │   │   ├── auth/
│   │   │   │           │   │   │   ├── EmailVerificationScreen.kt
│   │   │   │           │   │   │   ├── ExpertOnboardingScreen.kt
│   │   │   │           │   │   │   ├── ExpertSubmitScreen.kt
│   │   │   │           │   │   │   ├── ExpertVerificationScreen.kt
│   │   │   │           │   │   │   ├── OnBoarding.kt
│   │   │   │           │   │   │   ├── SignInScreen.kt
│   │   │   │           │   │   │   ├── SignUpFormScreen.kt
│   │   │   │           │   │   │   └── SignUpScreen.kt
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
│   │   │   │           │   │   ├── AuthViewModel.kt
│   │   │   │           │   │   ├── HomeViewModel.kt
│   │   │   │           │   │   ├── InboxViewModel.kt
│   │   │   │           │   │   ├── QuestionCreateViewModel.kt
│   │   │   │           │   │   └── UiState.kt
│   │   │   │           │   └── MainActivity.kt
│   │   │   │           ├── util/
│   │   │   │           │   └── DataFormatter.kt
│   │   │   │           └── VerifAI.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── apple.xml
│   │   │   │   │   ├── back_arrow.xml
│   │   │   │   │   ├── check_circle.xml
│   │   │   │   │   ├── google.xml
│   │   │   │   │   ├── group.xml
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
│   │   │   │       └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── java/
│   │           └── mp/
│   │               └── verif_ai/
│   │                   ├── data/
│   │                   │   └── respository/
│   │                   └── ExampleUnitTest.kt
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── proguard-rules.pro
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml
├── build.gradle.kts
├── folder_structure_export.py
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
└── settings.gradle.kts
```
