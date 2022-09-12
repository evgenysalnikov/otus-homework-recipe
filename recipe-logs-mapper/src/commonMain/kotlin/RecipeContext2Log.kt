package com.salnikoff.recipe.logs.mapper

import com.benasher44.uuid.uuid4
import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun RecipeContext.toLog(logId: String) = CommonLogModel(
    messageId = uuid4().toString(),
    messageTime = Clock.System.now().toString(),
    logId = logId,
    source = "recipe",
    recipe = toRecipeLog(),
    errors = errors.map { it.toLog() },
)

fun RecipeContext.toRecipeLog() : RecipeLogModel? {
    val recipeNone = Recipe()
    return RecipeLogModel(
        requestId = requestId.takeIf { it != RecipeRequestId.NONE }?.asString(),
        requestRecipe = recipeRequest.takeIf { it != recipeNone }?.toLog(),
        responseRecipe = recipeResponse.takeIf { it != recipeNone }?.toLog(),
        responseRecipes = recipesResponse.takeIf { it.isNotEmpty() }?.filter { it != recipeNone }?.map { it.toLog() },
        requestFilter = recipeFilterRequest.takeIf { it != RecipeFilter() }?.toLog(),
    ).takeIf { it != RecipeLogModel() }
}

private fun RecipeFilter.toLog() = RecipeFilterLog(
    searchString = searchString.takeIf { it.isNotBlank() },
    ownerId = owner.takeIf { it != UserId.NONE }?.asString(),
    searchTypes = searchTypes.takeIf { it.isNotEmpty() }?.map { it.name }?.toSet(),
)

private fun RecipeError.toLog() = ErrorLogModel(
    message = message.takeIf { it.isNotBlank() },
    field = field.takeIf { it.isNotBlank() },
    code = code.takeIf { it.isNotBlank() },
    level = level.name,
)

private fun Recipe.toLog() = RecipeLog(
    id = id.takeIf { it != RecipeId.NONE }?.asString(),
    title = title.takeIf { it.isNotBlank() },
    description = description.takeIf { it.isNotBlank() },
    requirements = requirements.toLog(),
    duration = duration.takeIf { it != Duration.ZERO }?.toLog(),
    ownerId = ownerId.takeIf { it != UserId.NONE }?.asString(),
    visibility = visibility.takeIf { it != RecipeVisibility.NONE }?.name,
    steps = steps.takeIf { it.isNotBlank() },
    permissions = permissionsClient.takeIf { it.isNotEmpty() }?.map { it.name }?.toSet(),
)

private fun List<RecipeRequirement>.toLog() : List<String> {
    val result = mutableListOf<String>()
    this.map {
        if (it != RecipeRequirement.NONE) {
            result.add(it.asString())
        }
    }
    return result.toList()
}

private fun Duration.toLog() : RecipeDurationLog = RecipeDurationLog(
    duration = toLong(DurationUnit.SECONDS).toString(),
    timeunit = DurationUnit.SECONDS.toString()
)
