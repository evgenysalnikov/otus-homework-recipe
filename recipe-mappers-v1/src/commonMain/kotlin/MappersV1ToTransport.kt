package com.salnikoff.recipe.mappers.v1

import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.mappers.v1.exceptions.UnknownRecipeCommand
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun RecipeContext.toTransport() : IRecipeApiResponse = when (val cmd = command) {
    RecipeCommand.CREATE -> toTransportCreate()
    RecipeCommand.READ -> toTransportRead()
    RecipeCommand.UPDATE -> toTransportUpdate()
    RecipeCommand.DELETE -> toTransportDelete()
    RecipeCommand.SEARCH -> toTransportSearch()
    RecipeCommand.NONE -> throw UnknownRecipeCommand(cmd)
}
private fun MutableList<RecipeError>.toTransportErrors() : List<RecipeApiError>? = this
    .map { it.toTransportRecipe() }
    .toList()
    .takeIf { it.isNotEmpty() }

private fun RecipeError.toTransportRecipe(): RecipeApiError = RecipeApiError(
    code = code.takeIf { it.isNotBlank() },
    group = group.takeIf { it.isNotBlank() },
    field = field.takeIf { it.isNotBlank() },
    message = message.takeIf { it.isNotBlank() }
)

private fun List<RecipeRequirement>.toTransportRecipe() = this
    .map { it.asString() }
    .toList()
    .takeIf { it.isNotEmpty() }

private fun Duration.toTransportRecipe(): RecipeApiDuration = when {
    this.toInt(DurationUnit.MINUTES) < 1 -> RecipeApiDuration(
        duration = this.toString(DurationUnit.SECONDS, 2),
        timeunit = RecipeApiTimeUnit.SEC
    )
    this.toInt(DurationUnit.MINUTES) >= 1 && this.toInt(DurationUnit.HOURS) < 1 -> RecipeApiDuration(
        duration = this.toDouble(DurationUnit.MINUTES).toString(),
        timeunit = RecipeApiTimeUnit.MIN
    )
    this.toInt(DurationUnit.HOURS) > 1 && this.toInt(DurationUnit.DAYS) < 1 -> RecipeApiDuration(
        duration = this.toString(DurationUnit.HOURS, 2),
        timeunit = RecipeApiTimeUnit.HOUR
    )
    this.toInt(DurationUnit.DAYS) > 1 -> RecipeApiDuration(
        duration = this.toString(DurationUnit.DAYS, 2),
        timeunit = RecipeApiTimeUnit.DAY
    )
    else -> RecipeApiDuration(
        duration = this.toString(DurationUnit.SECONDS, 2),
        timeunit = RecipeApiTimeUnit.SEC
    )
}

private fun RecipeVisibility.toTransportRecipe(): RecipeApiRecipeVisibility? = when(this) {
    RecipeVisibility.VISIBLE_PUBLIC -> RecipeApiRecipeVisibility.PUBLIC
    RecipeVisibility.VISIBLE_TO_OWNER -> RecipeApiRecipeVisibility.OWNER_ONLY
    RecipeVisibility.VISIBLE_TO_GROUP -> RecipeApiRecipeVisibility.REGISTERED_ONLY
    RecipeVisibility.NONE -> null
}

private fun RecipePermissionClient.toTransportRecipe() : RecipeApiRecipePermissions = when(this) {
    RecipePermissionClient.READ -> RecipeApiRecipePermissions.READ
    RecipePermissionClient.UPDATE -> RecipeApiRecipePermissions.UPDATE
    RecipePermissionClient.MAKE_VISIBLE_OWNER -> RecipeApiRecipePermissions.MAKE_VISIBLE_OWN
    RecipePermissionClient.MAKE_VISIBLE_GROUP -> RecipeApiRecipePermissions.MAKE_VISIBLE_GROUP
    RecipePermissionClient.MAKE_VISIBLE_PUBLIC -> RecipeApiRecipePermissions.MAKE_VISIBLE_PUBLIC
    RecipePermissionClient.DELETE -> RecipeApiRecipePermissions.DELETE
}

private fun MutableSet<RecipePermissionClient>.toTransportRecipe() : Set<RecipeApiRecipePermissions>? = this
    .map { it.toTransportRecipe() }
    .toSet()
    .takeIf { it.isNotEmpty() }

private fun Recipe.toTransportRecipe() : RecipeApiRecipeResponseObject  = RecipeApiRecipeResponseObject(
    id = id.takeIf { it != RecipeId.NONE }?.asString(),
    title = title.takeIf { it.isNotBlank() },
    description = description.takeIf { it.isNotBlank() },
    requirements = requirements.toTransportRecipe(),
    duration = duration.toTransportRecipe(),
    ownerId = ownerId.asString(),
    visibility = visibility.toTransportRecipe(),
    steps = steps,
    permissions = permissionsClient.toTransportRecipe(),
    lock = lock.takeIf { it != RecipeLock.NONE }?.asString()
)

private fun CorState.toRecipeApiResponseResult(): RecipeApiResponseResult = when(this) {
    CorState.RUNNING -> RecipeApiResponseResult.SUCCESS
    CorState.FINISHING -> RecipeApiResponseResult.SUCCESS
    else -> RecipeApiResponseResult.ERROR
}


private fun RecipeContext.toTransportCreate() : RecipeApiRecipeCreateResponse = RecipeApiRecipeCreateResponse(
    requestId = this.requestId.asString().takeIf { it.isNotBlank() },
    result = state.toRecipeApiResponseResult(),
    errors = errors.toTransportErrors(),
    recipe = recipeResponse.toTransportRecipe()
)

private fun RecipeContext.toTransportRead(): RecipeApiRecipeReadResponse = RecipeApiRecipeReadResponse(
    requestId = this.requestId.asString().takeIf { it.isNotBlank() },
    result = state.toRecipeApiResponseResult(),
    errors = errors.toTransportErrors(),
    recipe = recipeResponse.toTransportRecipe()
)

private fun RecipeContext.toTransportUpdate(): RecipeApiRecipeUpdateResponse = RecipeApiRecipeUpdateResponse(
    requestId = this.requestId.asString().takeIf { it.isNotBlank() },
    result = state.toRecipeApiResponseResult(),
    errors = errors.toTransportErrors(),
    recipe = recipeResponse.toTransportRecipe()
)

private fun RecipeContext.toTransportDelete(): RecipeApiRecipeDeleteResponse = RecipeApiRecipeDeleteResponse(
    requestId = this.requestId.asString().takeIf { it.isNotBlank() },
    result = state.toRecipeApiResponseResult(),
    errors = errors.toTransportErrors(),
    recipe = recipeResponse.toTransportRecipe()
)

@JvmName("RecipeToTransportRecipe")
private fun List<Recipe>.toTransportRecipe(): List<RecipeApiRecipeResponseObject>? = this
    .map { it.toTransportRecipe() }
    .toList()
    .takeIf { it.isNotEmpty() }

private fun RecipeContext.toTransportSearch(): RecipeApiRecipeSearchResponse = RecipeApiRecipeSearchResponse(
    requestId = this.requestId.asString().takeIf { it.isNotBlank() },
    result = if (state == CorState.RUNNING || state == CorState.FINISHING) RecipeApiResponseResult.SUCCESS else RecipeApiResponseResult.ERROR,
    errors = errors.toTransportErrors(),
    recipes = recipesResponse.toTransportRecipe()
)
