# FinancialApp

An Android personal finance prototype that brings savings goals, recurring subscriptions, investment tracking and currency conversion into one app. Built in Kotlin as an SDP team project, with a Jetpack Compose interface and local Room databases.

## Features

- **Savings goals:** create and edit goals, record contributions, organise categories, search and filter the goal list, and track completion.
- **Subscriptions:** manage recurring bills, search and sort subscriptions, view total costs, mark payments and schedule due-date notifications.
- **Investments:** record holdings and purchase prices, retrieve daily stock prices from Alpha Vantage, and calculate portfolio value and unrealised profit/loss.
- **Currency conversion:** fetch supported currencies and exchange-rate data through the Frankfurter API.
- **AI assistant:** a Gemini-powered chat interface for app guidance and general financial questions.
- **Account and personalisation:** Firebase sign-up, sign-in, password reset and phone MFA flows, plus theme, background and profile-image preferences.
- **Wallet and reporting:** manage local card-reference entries and explore report screens and CSV export. Dashboard/report values and exported transactions currently include sample data.

## Technology

| Area | Implementation |
| --- | --- |
| Language and UI | Kotlin 2.0.21, Jetpack Compose, Material 3, Navigation Compose |
| State and asynchronous work | Android ViewModels, Kotlin coroutines and Flow |
| Local storage | Room with KSP; DataStore preferences |
| Authentication | Firebase Authentication; Firestore for user-profile data |
| Networking | Retrofit with Gson; Frankfurter and Alpha Vantage APIs |
| AI | Google Generative AI client for Gemini |
| Background reminders | WorkManager and Android notifications |
| Build | Android Gradle Plugin 8.12.2; Gradle 8.13 wrapper |

## Code layout

The Android Studio project is in [`FinancialApp/`](FinancialApp), one level below the repository root. Application source is under `FinancialApp/app/src/main/java/com/example/financialapp/`.

- `ui/goal/`, `ui/category/` and `data/`: savings screens, ViewModels, Room entities, DAOs and repositories.
- `subscriptions/`, `Investment/` and `wallet/`: feature-specific screens and persistence.
- `Conversion/` and `Ai/`: exchange-rate networking and the chat interface.
- `Login/`: account, password reset and MFA flows.
- `notifications/` and `work/`: notification helpers and background work.
- `dashboard/`, `report/` and `util/`: overview screens, reports and CSV export.

## Local setup

### Requirements

- Android Studio with support for Android Gradle Plugin 8.12 and Android SDK Platform 36.
- JDK 17 for Gradle. The app's Java/Kotlin bytecode target is 11; this is separate from the JDK used to run the build. See the [AGP compatibility requirements](https://developer.android.com/build/releases/agp-8-12-0-release-notes#compatibility).
- An emulator or Android device running API 24 or later. Compile/target SDK is 36; check reminder behaviour on your chosen Android version.
- Your own Firebase project and, for the corresponding features, Gemini and Alpha Vantage access.

### Configure and run

1. Clone the repository:

   ```sh
   git clone https://github.com/allenl13/FinancialApp.git
   cd FinancialApp/FinancialApp
   ```

2. Open this inner `FinancialApp` directory in Android Studio. Set the Gradle JDK to 17 and install the required SDK components. The checkout includes a machine-specific `local.properties`; update `sdk.dir` to your local Android SDK path.

3. Provide your own service configuration as environment variables available to the Android Studio or Gradle process:

   | Variable | Purpose |
   | --- | --- |
   | `FIREBASE_API_KEY` | Firebase Android app API key |
   | `FIREBASE_APP_ID` | Registered Firebase Android app ID |
   | `FIREBASE_PROJECT_ID` | Firebase project ID |
   | `FIREBASE_SENDER_ID` | Firebase project number / sender ID |
   | `FIREBASE_STORAGE_BUCKET` | Firebase storage bucket name |
   | `GEMINI_API_KEY` | Gemini chat access |
   | `ALPHA_VANTAGE_KEY` | Stock-price API access |

   Register the Android package `com.example.financialapp` in your Firebase project and configure the authentication methods used by the app. The Gradle build generates `app/google-services.json` from the Firebase configuration. For Gemini and Alpha Vantage, values in `local.properties` take precedence over environment variables; keep credentials out of tracked files.

   The current build script contains fallback service configuration. Supply your own configuration rather than relying on those defaults; their availability and permissions are not guaranteed.

4. Sync Gradle, select the `app` run configuration and launch it on your device or emulator. For a command-line debug build from the inner project directory:

   ```sh
   bash ./gradlew :app:assembleDebug
   ```

   The wrapper is invoked with `bash` because it is not marked executable in this checkout.

5. Create an account in your configured Firebase project. Allow notifications when testing reminders. Use test data while exploring the prototype.

## Testing

From the inner `FinancialApp` directory:

```sh
# Local JVM tests
bash ./gradlew :app:testDebugUnitTest

# Instrumented tests; requires a connected device or running emulator
bash ./gradlew :app:connectedDebugAndroidTest
```

The current suite includes one subscription-search example using a fixed list, the generated arithmetic unit test and the generated Android package-name check. Coverage is limited: authentication, persistence, notifications and external APIs do not have end-to-end tests.

For a manual review, check goal creation and contribution updates, subscription search and payment state, reminder permissions, currency conversion, portfolio calculations and sign-in/MFA using your own service configuration. Also check how each network feature responds when offline or rate-limited.

## Current limitations

- This is a team prototype. Some dashboard, report and CSV-export content is hard-coded sample data; it is not a live bank-transaction feed.
- Currency and stock data depend on third-party availability and request limits. Investment prices use daily time-series data rather than a live trading feed.
- Authentication, MFA and AI chat depend on correctly configured external services.
- Local wallet entries store card-reference details such as the last four digits; the project has no payment-processing integration.
- Notification timing and API compatibility need device testing, and automated test coverage is limited.

## Team project

FinancialApp was developed collaboratively as an SDP team project. The features described here reflect the shared work of the team.
