# TopRecipes – Multi-Module Clean Architecture

Android app with Clean Architecture.

## Architecture

- **domain** – Business logic, no dependencies
- **data** – Repositories, API, DB; depends on domain
- **presentation** – UI, ViewModels; depends on domain
- **app** – Entry point; depends on data, presentation, domain

## Modules & layers

| Module         | Layer        | Role                                      |
|----------------|-------------|-------------------------------------------|
| **:domain**    | Domain      | BaseRepository, BaseUseCase, business rules |
| **:data**      | Data        | Hilt, Retrofit, Room, OkHttp, Coroutines   |
| **:presentation** | UI      | Compose, ViewModels, Navigation, Hilt       |
| **:app**       | App         | MainApplication, MainActivity              |

## Libraries

**Core:** Kotlin 2.0.21 · AGP 8.13.2 · Min SDK 24, Target 36  

**UI:** Jetpack Compose (BOM 2024.12.01) · Material 3 · Navigation Compose 2.8.4  

**DI:** Hilt 2.52  

**Network:** Retrofit 2.11.0 · OkHttp 4.12.0 · Gson 2.11.0  

**DB:** Room 2.6.1  

**Async:** Kotlin Coroutines 1.9.0  

**Architecture:** Lifecycle ViewModel 2.8.7  

Versions are in `gradle/libs.versions.toml`.
