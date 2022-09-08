package com.salnikoff.recipe.repo.test

import com.salnikoff.recipe.common.models.*
import kotlin.time.Duration.Companion.minutes

abstract class BaseInitRecipes(val op: String) : IInitObjects<Recipe> {

    fun createInitTestModel(
        suf: String,
        ownerId: UserId = UserId("owner-123"),
    ) = Recipe(
        id = RecipeId("recipe-repo-$op-$suf"),
        title = "$suf stub",
        description = "$suf stub description",
        requirements = listOf(
            RecipeRequirement("$suf stub requirement"),
            RecipeRequirement("$suf stub requirement second")),
        duration = 3.0.minutes,
        ownerId = ownerId,
        visibility = RecipeVisibility.VISIBLE_PUBLIC,
        steps = "$suf stub steps",
        lock = RecipeLock("12345-12345-12345-12345")
    )

}