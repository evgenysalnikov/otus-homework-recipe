package recipe.biz.repo

import com.salnikoff.recipe.biz.RecipeProcessor
import com.salnikoff.recipe.biz.helpers.principalUser
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.repo.inmemory.RecipeRepoInMemory
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class BizRepoDeleteTest {
    private val command = RecipeCommand.DELETE
    private val uuidOld = "10000000-0000-0000-0000-000000000001"
    private val uuidNew = "10000000-0000-0000-0000-000000000002"
    private val uuidBad = "10000000-0000-0000-0000-000000000003"
    private val initRecipe = Recipe(
        id = RecipeId("123"),
        title = "abc",
        description = "abc",
        steps = "steps",
        duration = 10.minutes,
        visibility = RecipeVisibility.VISIBLE_PUBLIC,
        lock = RecipeLock(uuidOld),
        ownerId = principalUser().id
    )
    private val repo by lazy {
        RecipeRepoInMemory(initObjects = listOf(initRecipe), randomUuid = { uuidNew })
    }
    private val settings by lazy {
        RecipeSettings(
            repoTest = repo
        )
    }
    private val processor by lazy { RecipeProcessor(settings) }

    @Test
    fun repoDeleteSuccessTest() = runTest {
        val recipeToUpdate = Recipe(
            id = RecipeId("123"),
            lock = RecipeLock(uuidOld)
        )
        val ctx = RecipeContext(
            command = command,
            principal = principalUser(),
            state = CorState.NONE,
            workMode = RecipeWorkMode.TEST,
            recipeRequest = recipeToUpdate,
        )
        processor.exec(ctx)
        assertEquals(CorState.FINISHING, ctx.state)
        assertTrue { ctx.errors.isEmpty() }
        assertEquals(initRecipe.id, ctx.recipeResponse.id)
        assertEquals(initRecipe.title, ctx.recipeResponse.title)
        assertEquals(initRecipe.description, ctx.recipeResponse.description)
        assertEquals(initRecipe.duration, ctx.recipeResponse.duration)
        assertEquals(initRecipe.visibility, ctx.recipeResponse.visibility)
        assertEquals(uuidOld, ctx.recipeResponse.lock.asString())
    }

    @Test
    fun repoDeleteConcurrentTest() = runTest {
        val recipeToUpdate = Recipe(
            id = RecipeId("123"),
            lock = RecipeLock(uuidBad),
        )
        val ctx = RecipeContext(
            command = command,
            principal = principalUser(),
            state = CorState.NONE,
            workMode = RecipeWorkMode.TEST,
            recipeRequest = recipeToUpdate,
        )
        processor.exec(ctx)
        assertEquals(CorState.FAILING, ctx.state)
        assertEquals(1, ctx.errors.size)
        assertEquals("lock", ctx.errors.first().field)
        assertEquals(initRecipe.id, ctx.recipeResponse.id)
        assertEquals(initRecipe.title, ctx.recipeResponse.title)
        assertEquals(initRecipe.description, ctx.recipeResponse.description)
        assertEquals(initRecipe.duration, ctx.recipeResponse.duration)
        assertEquals(initRecipe.visibility, ctx.recipeResponse.visibility)
        assertEquals(uuidOld, ctx.recipeResponse.lock.asString())
    }

    @Test
    fun repoDeleteNotFoundTest() = repoNotFoundTest(processor, command)
}
