package recipe.biz.repo

import com.salnikoff.recipe.biz.RecipeProcessor
import com.salnikoff.recipe.biz.helpers.principalUser
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.repo.inmemory.RecipeRepoInMemory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class BizRepoReadTest {
    private val command = RecipeCommand.READ
    private val initRecipe = Recipe(
        id = RecipeId("123"),
        title = "abc",
        description = "abc",
        duration = 5.minutes,
        visibility = RecipeVisibility.VISIBLE_PUBLIC,
    )
    private val repo by lazy { RecipeRepoInMemory(initObjects = listOf(initRecipe)) }
    private val settings by lazy {
        RecipeSettings(
            repoTest = repo
        )
    }
    private val processor by lazy { RecipeProcessor(settings) }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun repoReadSuccessTest() = runTest {
        val ctx = RecipeContext(
            command = command,
            state = CorState.NONE,
            workMode = RecipeWorkMode.TEST,
            principal = principalUser(),
            recipeRequest = Recipe(
                id = RecipeId("123"),
            ),
        )
        processor.exec(ctx)
        assertEquals(CorState.FINISHING, ctx.state)
        assertEquals(initRecipe.id, ctx.recipeResponse.id)
        assertEquals(initRecipe.title, ctx.recipeResponse.title)
        assertEquals(initRecipe.description, ctx.recipeResponse.description)
        assertEquals(initRecipe.duration, ctx.recipeResponse.duration)
        assertEquals(initRecipe.visibility, ctx.recipeResponse.visibility)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun repoReadNotFoundTest() = repoNotFoundTest(processor, command)
}
