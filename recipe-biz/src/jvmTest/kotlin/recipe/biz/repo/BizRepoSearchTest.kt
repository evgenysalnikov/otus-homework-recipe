package recipe.biz.repo

import com.salnikoff.recipe.biz.RecipeProcessor
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.repo.inmemory.RecipeRepoInMemory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class BizRepoSearchTest {
    private val command = RecipeCommand.SEARCH
    private val initRecipe = Recipe(
        id = RecipeId("123"),
        title = "abc",
        description = "abc",
        duration = 7.minutes,
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
    fun repoSearchSuccessTest() = runTest {
        val ctx = RecipeContext(
            command = command,
            state = CorState.NONE,
            workMode = RecipeWorkMode.TEST,
            recipeFilterRequest = RecipeFilter(
                searchString = "ab",
            ),
        )
        processor.exec(ctx)
        assertEquals(CorState.FINISHING, ctx.state)
        assertEquals(1, ctx.recipesResponse.size)
    }
}
