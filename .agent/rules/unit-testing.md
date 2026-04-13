---
description: Unit testing guidelines for DevPulse KMP
---

# Unit Testing Guidelines

## Frameworks & Libraries

- **kotlin.test** for assertions (`assertEquals`, `assertTrue`, `assertNull`, `assertFailsWith`)
- **MockK** for mocking (`mockk()`, `coEvery`, `coVerify`)
- **Turbine** (`app.cash.turbine`) for testing Flows
- **kotlinx-coroutines-test** (`runTest`, `TestCoroutineDispatcher`) for coroutine testing

## Test Location & Naming

- Test files live in the module's `commonTest` or `androidUnitTest` source set, mirroring the production package structure
- Test class names are suffixed with `Test`: `GetUserProfileUseCaseTest`
- Test method names use the Given-When-Then format:

```kotlin
@Test
fun `Given valid user id When invoke called Then returns user profile`() = runTest { ... }
```

## Structure

Every test class:

```kotlin
class MyUseCaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val repository = mockk<UserRepository>()
    private lateinit var useCase: GetUserProfileUseCase

    @BeforeTest
    fun setUp() {
        useCase = GetUserProfileUseCase(repository, coroutineRule.testDispatcher)
    }
}
```

## What to Test

- **Use cases**: All conditional branches, connectivity logic, `Result` wrapper outcomes, dispatcher switching
- **Repositories**: All public methods, input scenarios, error conditions, flow transformations
- **ViewModels**: State updates, effect emissions, event routing — use Turbine to collect flows
- **Mappers & utilities**: Direct unit tests with real instances — no mocking needed

Do **not** test: simple getters/setters, pass-through delegation, framework code with no custom logic.

## Mocking Strategy

- Mock only external boundaries: repositories, data sources, platform services
- Use **real instances** for: mappers, data classes, domain models, pure utilities
- Avoid `spy()` — test the real object or mock it outright
- Use `mockk(relaxed = true)` sparingly and only for deeply nested dependencies

## Flow Testing with Turbine

**All Flow assertions must use Turbine.** Never use `first()`, `toList()`, `collect {}`, or manual `launch`/`cancel` patterns to assert on Flow emissions — Turbine is the only permitted approach.

```kotlin
// ✅ Correct — Turbine
@Test
fun `Given live data When flow collected Then emits updates`() = runTest {
    coEvery { repository.observe() } returns flowOf(fakeItem)

    useCase.invoke().test {
        assertEquals(fakeItem, awaitItem())
        awaitComplete()
    }
}

// ❌ Never do this
val result = useCase.invoke().first()
val items = useCase.invoke().toList()
```

Common Turbine assertions:
- `awaitItem()` — assert the next emission
- `awaitComplete()` — assert the flow completed normally
- `awaitError()` — assert the flow terminated with an error
- `cancelAndIgnoreRemainingEvents()` — cancel a hot flow after asserting what you care about
- `expectNoEvents()` — assert nothing was emitted

## ViewModel Testing

```kotlin
@Test
fun `Given refresh event When onEvent called Then state shows loading then data`() = runTest {
    coEvery { useCase() } returns Result.Success(fakeData)
    val viewModel = MyViewModel(useCase)

    viewModel.state.test {
        assertEquals(true, awaitItem().isLoading)
        assertEquals(fakeData, awaitItem().items)
        cancelAndIgnoreRemainingEvents()
    }
}
```

## Test Data

- Create realistic test objects inline or as a `TestData` companion object in the test file
- For reusable test data shared across test classes, create a dedicated `TestData.kt` in the same package
- Use constants for common values (`TEST_USER_ID = "test-123"`)

## Always Run Tests

- Run `./gradlew testDebugUnitTest` (or the equivalent task) before committing
- Tests must pass before requesting review
