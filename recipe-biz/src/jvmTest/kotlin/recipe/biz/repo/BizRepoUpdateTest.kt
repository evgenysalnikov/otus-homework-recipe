package recipe.biz.repo

import com.salnikoff.recipe.biz.RecipeProcessor
import com.salnikoff.recipe.biz.helpers.principalUser
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.repo.inmemory.RecipeRepoInMemory
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class BizRepoUpdateTest {
    private val command = RecipeCommand.UPDATE
    private val uuidOld = "10000000-0000-0000-0000-000000000001"
    private val uuidNew = "10000000-0000-0000-0000-000000000002"
    private val uuidBad = "10000000-0000-0000-0000-000000000003"
    private val initRecipe = Recipe(
        id = RecipeId("123"),
        title = "abc",
        description = "abc",
        duration = 5.minutes,
        visibility = RecipeVisibility.VISIBLE_PUBLIC,
        lock = RecipeLock(uuidOld),
        ownerId = principalUser().id,
    )
    private val repo by lazy { RecipeRepoInMemory(initObjects = listOf(initRecipe), randomUuid = { uuidNew }) }
    private val settings by lazy {
        RecipeSettings(
            repoTest = repo
        )
    }
    private val processor by lazy { RecipeProcessor(settings) }

    @Test
    fun repoUpdateSuccessTest() = runTest {
        val recipeToUpdate = Recipe(
            id = RecipeId("123"),
            title = "xyz",
            description = "xyz",
            duration = 5.minutes,
            visibility = RecipeVisibility.VISIBLE_TO_GROUP,
            lock = RecipeLock(uuidOld),
        )
        val ctx = RecipeContext(
            command = command,
            state = CorState.NONE,
            workMode = RecipeWorkMode.TEST,
            principal = principalUser(),
            recipeRequest = recipeToUpdate,
        )
        processor.exec(ctx)
        assertEquals(CorState.FINISHING, ctx.state)
        assertEquals(recipeToUpdate.id, ctx.recipeResponse.id)
        assertEquals(recipeToUpdate.title, ctx.recipeResponse.title)
        assertEquals(recipeToUpdate.description, ctx.recipeResponse.description)
        assertEquals(recipeToUpdate.duration, ctx.recipeResponse.duration)
        assertEquals(recipeToUpdate.visibility, ctx.recipeResponse.visibility)
        assertEquals(uuidNew, ctx.recipeResponse.lock.asString())
    }

    @Test
    fun repoUpdateConcurrentTest() = runTest {
        val recipeToUpdate = Recipe(
            id = RecipeId("123"),
            title = "xyz",
            description = "xyz",
            duration = 5.minutes,
            visibility = RecipeVisibility.VISIBLE_TO_GROUP,
            lock = RecipeLock(uuidBad)
        )
        val ctx = RecipeContext(
            command = command,
            state = CorState.NONE,
            workMode = RecipeWorkMode.TEST,
            principal = principalUser(),
            recipeRequest = recipeToUpdate,
        )
        processor.exec(ctx)
        assertEquals(CorState.FAILING, ctx.state)
        assertEquals(initRecipe.id, ctx.recipeResponse.id)
        assertEquals(initRecipe.title, ctx.recipeResponse.title)
        assertEquals(initRecipe.description, ctx.recipeResponse.description)
        assertEquals(initRecipe.duration, ctx.recipeResponse.duration)
        assertEquals(initRecipe.visibility, ctx.recipeResponse.visibility)
        assertEquals(uuidOld, ctx.recipeResponse.lock.asString())
    }

    @Test
    fun repoUpdateNotFoundTest() = repoNotFoundTest(processor, command)
}
