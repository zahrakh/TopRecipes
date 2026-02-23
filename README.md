# TopRecipes

Android app that shows recipes from the Spoonacular API. You can search, view details, and mark favorites (stored locally).
I added repository for Spoonacular API but app layer and structure has potential to add other Api in future or its easy to replace the Api 
the Architecture is MVVM but you can see that I also used Unidirectional flow that we use in the MVI.
---

### Requirements

- Android Studio (latest stable)
- JDK 11+
- Android device or emulator (API 24+)

### API key (required)

The app calls the Spoonacular API and needs an API key. to keep the API key safe I added the key to the local file to prevent pushing on the git
you have to create your own key and replace it as below:
1. Create a file named **`local.properties`** in the **project root** (same folder as `build.gradle.kts`, next to `app/`, `domain/`, `data/`, `presentation/`).
2. Add this line (replace `YOUR_KEY` with your key):

   ```properties
   API_KEY=YOUR_KEY
   ```

3. Save the file. The project reads `API_KEY` from here and puts it into the app. **Do not commit `local.properties`** — it’s in `.gitignore` so your key stays local.

### Build and run

1. Open the project in Android Studio.
2. Wait for Gradle sync to finish.
3. Connect a device or start an emulator.
4. Click **Run** (green play) or use **Shift+F10** (Windows/Linux) / **Control+R** (Mac).

To run tests: **Run → Run 'All Tests'** or run a single test class from the Project view (right‑click test file → Run).

---

## Architecture and design

The app is split into layers so each part has a clear job and can be tested on its own.

- **Domain** – Models (e.g. `Recipe`, `RecipeDetail`), repository interfaces, and use cases. No Android or network code. This is the “what the app does” layer.
- **Data** – Implements the repositories: calls Spoonacular API, maps DTOs to domain models, and uses Room for caching and favorites. Also maps API/network errors to domain errors.
- **Presentation** – ViewModels and Compose UI. ViewModels call use cases and expose state/effects; they don’t know about Retrofit or Room.

Data and presentation both depend only on domain, so we can swap or mock the data layer without touching UI or business rules.

**Design choices**

- **Single activity, Compose** – One Activity, navigation and screens in Compose.
- **State + effects** – ViewModels expose a state (e.g. list, loading, error) and one‑off effects (e.g. show error, open URL). UI reacts to state and handles effects once.
- **Cache‑then‑network for details** – Recipe details are loaded from cache first; if missing, we fetch from API, then save to cache. Favorites are stored in Room.
- **Errors** – Network/API errors are mapped in the data layer to a small set of domain error types so the UI can show the right message without knowing HTTP codes.

---

## Assumptions

- Spoonacular API is used as the only remote source;but its easy to update with any other api.
- Favorites are device‑only (Room), no sync.
- Recipe list is paginated with a fixed page size; infinite scroll could be added later.
- We assume a simple “first load from cache, then network” for details; no background refresh strategy.

---

## What could be improved

- **Offline** – Better handling when there’s no network (e.g. show cached list, clear “no connection” messaging) for all list not only fav screen.
- **Retries** – Retry failed requests (with backoff) instead of failing once.
- **Testing** – More UI/Compose tests and edge cases (empty states, rotation). 
- **DI** – Some modules are a bit large; could be split for readability.
- **Analytics / logging** – No analytics or structured logging yet; could be added for debugging and product metrics.
- **UI** – Navigation button and separate UI for theme and fave menu.

---

## Automated tests (Domain and Data layers)

### Domain layer

Tests live under **`domain/src/test/`**. They use JUnit 4, MockK, and `runTest` (coroutines test).

| Test | What it checks |
|------|----------------|
| `GetRecipesUseCaseTest` | Use case returns success/error from repository and passes parameters correctly. |
| `GetRecipeDetailsUseCaseTest` | Same for recipe details. |
| `GetFavoriteRecipesUseCaseTest` | Use case returns the flow from repository. |
| `ToggleFavoriteUseCaseTest` | Use case calls repository `setFavorite`. |

### Data layer

Tests live under **`data/src/test/`**. Same stack (JUnit 4, MockK, coroutines test).

| Test | What it checks |
|------|----------------|
| `SpoonacularRepositoryImplTest` | Repository uses remote for list, cache‑then‑remote for details, delegates favorites to local source. |
| `SpoonacularRemoteDataSourceImpTest` | Remote data source calls API and maps errors with `DomainErrorMapper`. |
| `RecipeLocalDataSourceImplTest` | Local data source delegates to DAO and maps entities to domain. |
| `DomainErrorMapperTest` | HTTP 503/409/other and `IOException` map to the right `DomainError` types. |
| `RecipeMapperTest` | DTOs (list + details) map to domain models correctly; nulls handled. |
| `RecipeEntityMapperTest` | Room entity ↔ domain model mapping. |
| `ConvertersTest` | Room type converters (e.g. list of ingredients). |

To run only Domain and Data tests from the command line:

```bash
./gradlew :domain:testDebugUnitTest :data:testDebugUnitTest
```

In Android Studio you can run all tests or right‑click `domain/src/test` or `data/src/test` and choose **Run Tests**.

---

## Tech stack (short)

Kotlin, Jetpack Compose, Hilt, Retrofit, Room, Coroutines. Versions are in `gradle/libs.versions.toml`.
