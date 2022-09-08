package com.salnikoff.recipe.common.models

import kotlin.time.Duration

data class Recipe(
    var id: RecipeId = RecipeId.NONE,
    var title: String = "",
    var description: String = "",
    var requirements: List<RecipeRequirement> = listOf(),
    var duration: Duration = Duration.ZERO,
    var ownerId: UserId = UserId.NONE,
    var visibility: RecipeVisibility = RecipeVisibility.NONE,
    var steps: String = "",
    val permissionsClient: MutableSet<RecipePermissionClient> = mutableSetOf(),
    var lock: RecipeLock = RecipeLock.NONE,
    var principalRelations: Set<RecipePrincipalRelations> = emptySet(),
) {
    fun deepCopy(): Recipe = Recipe(
        id = this@Recipe.id,
        title = this@Recipe.title,
        description = this@Recipe.description,
        requirements = this@Recipe.requirements.deepCopy(),
        duration = this@Recipe.duration,
        ownerId = this@Recipe.ownerId,
        visibility = this@Recipe.visibility,
        steps = this@Recipe.steps,
        permissionsClient = this@Recipe.permissionsClient,
        lock = this@Recipe.lock.deepCopy()
    )
}

fun List<RecipeRequirement>.deepCopy(): List<RecipeRequirement> = this.map { RecipeRequirement(it.asString()) }.toList()

fun RecipeLock?.deepCopy(): RecipeLock {
    if (this == null) {
        return RecipeLock.NONE
    } else {
        return RecipeLock(this.asString())
    }
}
