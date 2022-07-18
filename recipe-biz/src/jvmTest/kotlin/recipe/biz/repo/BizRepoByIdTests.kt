package recipe.biz.repo

import com.salnikoff.recipe.biz.RecipeProcessor
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.repo.IRecipeRepository
import com.salnikoff.recipe.repo.inmemory.RecipeRepoInMemory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals

private val initRecipe = Recipe(
    id = RecipeId("123"),
    title = "abc",
    description = "abc",
    visibility = RecipeVisibility.VISIBLE_PUBLIC,
)
private val uuid = "10000000-0000-0000-0000-000000000001"
private val repo: IRecipeRepository
    get() = RecipeRepoInMemory(initObjects = listOf(initRecipe), randomUuid = { uuid })


@OptIn(ExperimentalCoroutinesApi::class)
fun repoNotFoundTest(processor: RecipeProcessor, command: RecipeCommand) = runTest {
    val ctx = RecipeContext(
        command = command,
        state = CorState.NONE,
        workMode = RecipeWorkMode.TEST,
        recipeRepo = repo,
        recipeRequest = Recipe(
            id = RecipeId("12345"),
            title = "xyz",
            description = "xyz",
            visibility = RecipeVisibility.VISIBLE_TO_GROUP,
            lock = RecipeLock(uuid),
        ),
    )
    processor.exec(ctx)
    assertEquals(CorState.FAILING, ctx.state)
    assertEquals(Recipe(), ctx.recipeResponse)
    assertEquals(1, ctx.errors.size)
    assertEquals("id", ctx.errors.first().field)
}
