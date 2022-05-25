package com.salnikoff.recipe.stubs

import com.salnikoff.recipe.common.models.*
import kotlin.time.Duration.Companion.minutes

object Pie {
    private fun stubReady() = Recipe(
        id = RecipeId(id = "pie_recipe_id"),
        title = "Pie",
        description = "pie description",
        requirements = listOf(
            RecipeRequirement("pie req1"),
            RecipeRequirement("pie req2")
        ),
        duration = 3.0.minutes,
        ownerId = UserId("pieOwnerId"),
        visibility = RecipeVisibility.VISIBLE_PUBLIC,
        steps = "My recipe description. How to do it and so on",
        permissionsClient = mutableSetOf(RecipePermissionClient.READ)
    )

    private fun stubInProgress() = Recipe(
        id = RecipeId(id = "some_pie_recipe_id"),
        title = "SomePieRecipeTitle",
        description = "description about how to make a perfect pie",
        requirements = listOf(
            RecipeRequirement("dough"),
            RecipeRequirement("apple")
        ),
        duration = 3.0.minutes,
        ownerId = UserId("kitchenOwnerId"),
        visibility = RecipeVisibility.VISIBLE_PUBLIC,
        steps = "SomePieRecipe recipe description. How to do it and so on",
        permissionsClient = mutableSetOf(RecipePermissionClient.MAKE_VISIBLE_GROUP)
    )

    fun getModel(model: (Recipe.() -> Unit)? = null) = model?.let { stubReady().apply(it) } ?: stubReady()

    fun getModels() = listOf(
        stubReady(),
        stubInProgress()
    )

    fun Recipe.update(updatableRecipe: Recipe) = apply {
        title = updatableRecipe.title
        description = updatableRecipe.description
        requirements = updatableRecipe.requirements
        duration = updatableRecipe.duration
        ownerId = updatableRecipe.ownerId
        visibility = updatableRecipe.visibility
        steps = updatableRecipe.steps
        permissionsClient.addAll(updatableRecipe.permissionsClient)
    }
}
