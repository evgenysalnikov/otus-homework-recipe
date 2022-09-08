package recipe.biz.repo

import com.salnikoff.recipe.biz.RecipeProcessor
import com.salnikoff.recipe.biz.helpers.principalUser
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.repo.inmemory.RecipeRepoInMemory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes

class BizRepoCreateTest {
    private val command = RecipeCommand.CREATE
    private val uuid = "10000000-0000-0000-0000-000000000001"
    private val repo = RecipeRepoInMemory(
        randomUuid = { uuid }
    )
    private val settings = RecipeSettings(
        repoTest = repo
    )
    private val processor = RecipeProcessor(settings)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun repoCreateSuccessTest() = runTest {
        val ctx = RecipeContext(
            command = command,
            state = CorState.NONE,
            workMode = RecipeWorkMode.TEST,
            principal = principalUser(),
            recipeRequest = Recipe(
                id = RecipeId("123"),
                title = "abc",
                description = "abc",
                duration = 3.minutes,
                visibility = RecipeVisibility.VISIBLE_PUBLIC,
            ),
        )
        processor.exec(ctx)
        assertEquals(CorState.FINISHING, ctx.state)
        assertNotEquals(RecipeId.NONE, ctx.recipeResponse.id)
        assertEquals("abc", ctx.recipeResponse.title)
        assertEquals("abc", ctx.recipeResponse.description)
        assertEquals(RecipeVisibility.VISIBLE_PUBLIC, ctx.recipeResponse.visibility)
        assertEquals(uuid, ctx.recipeResponse.lock.asString())
    }
}
