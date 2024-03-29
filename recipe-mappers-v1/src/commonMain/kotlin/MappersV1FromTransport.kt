package com.salnikoff.recipe.mappers.v1

import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.stubs.RecipeStubs
import com.salnikoff.recipe.mappers.v1.exceptions.UnknownRequestClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun RecipeContext.fromTransport(request: IRecipeApiRequest) = when(request) {
    is RecipeApiRecipeCreateRequest -> fromTransport(request)
    is RecipeApiRecipeReadRequest -> fromTransport(request)
    is RecipeApiRecipeUpdateRequest -> fromTransport(request)
    is RecipeApiRecipeDeleteRequest -> fromTransport(request)
    is RecipeApiRecipeSearchRequest -> fromTransport(request)
    else -> throw UnknownRequestClass(request::class)
}

private fun IRecipeApiRequest?.requestId() = this?.requestId?.let { RecipeRequestId(it) } ?: RecipeRequestId.NONE
private fun List<String>.toRecipeRequirements(): List<RecipeRequirement> = this.map { RecipeRequirement(it) }
private fun RecipeApiDuration.toInternal(): Duration = when(this.timeunit) {
    RecipeApiTimeUnit.SEC -> this.duration?.toDoubleOrNull()?.seconds ?: 0.0.seconds
    RecipeApiTimeUnit.MIN -> this.duration?.toDoubleOrNull()?.minutes ?: 0.0.minutes
    RecipeApiTimeUnit.HOUR -> this.duration?.toDoubleOrNull()?.hours ?: 0.0.hours
    RecipeApiTimeUnit.DAY -> this.duration?.toDoubleOrNull()?.days ?: 0.0.days
    null -> 0.0.seconds
}
private fun String.toRecipeUserId(): UserId = UserId(this)
private fun String?.toRecipeId(): RecipeId = this?.let{RecipeId(it)} ?: RecipeId.NONE
private fun RecipeApiRecipeVisibility?.fromTransport(): RecipeVisibility = when(this) {
    RecipeApiRecipeVisibility.PUBLIC -> RecipeVisibility.VISIBLE_PUBLIC
    RecipeApiRecipeVisibility.OWNER_ONLY -> RecipeVisibility.VISIBLE_TO_OWNER
    RecipeApiRecipeVisibility.REGISTERED_ONLY -> RecipeVisibility.VISIBLE_TO_GROUP
    null -> RecipeVisibility.NONE
}

private fun RecipeApiRecipeCreateObject.toInternal(): Recipe = Recipe(
    title = this.title ?: "",
    description = this.description ?: "",
    requirements = this.requirements?.toRecipeRequirements() ?: listOf(),
    duration = this.duration?.toInternal() ?: Duration.ZERO,
    ownerId = this.ownerId?.toRecipeUserId() ?: UserId.NONE,
    visibility = this.visibility.fromTransport(),
    steps = this.steps ?: ""
)

private fun RecipeApiRecipeUpdateObject.toInternal(): Recipe = Recipe(
    id = this.id.toRecipeId(),
    title = this.title ?: "",
    description = this.description ?: "",
    requirements = this.requirements?.toRecipeRequirements() ?: listOf(),
    duration = this.duration?.toInternal() ?: Duration.ZERO,
    ownerId = this.ownerId?.toRecipeUserId() ?: UserId.NONE,
    visibility = this.visibility.fromTransport(),
    steps = this.steps?: ""
)

private fun String?.toRecipeLock() : RecipeLock {
    if (this == null || this.isEmpty()) {
        return RecipeLock.NONE
    } else return RecipeLock(this)
}

private fun RecipeApiRecipeUpdateWithLockObject.toInternal(): Recipe = Recipe(
    id = this.id.toRecipeId(),
    title = this.title ?: "",
    description = this.description ?: "",
    requirements = this.requirements?.toRecipeRequirements() ?: listOf(),
    duration = this.duration?.toInternal() ?: Duration.ZERO,
    ownerId = this.ownerId?.toRecipeUserId() ?: UserId.NONE,
    visibility = this.visibility.fromTransport(),
    steps = this.steps?: "",
    lock = this.lock.toRecipeLock()
)
private fun RecipeApiBaseWithId?.toRecipeWithId(): Recipe = Recipe(id = this?.recipe?.id.toRecipeId())
private fun RecipeApiBaseWithIdAndLock?.toRecipeWithIdAndLock(): Recipe = Recipe(id = this?.recipe?.id.toRecipeId(), lock = this?.recipe?.lock.toRecipeLock())
private fun RecipeApiDebug?.transportToWorkMode(): RecipeWorkMode = when(this?.mode) {
    RecipeApiRequestDebugMode.PROD -> RecipeWorkMode.PROD
    RecipeApiRequestDebugMode.TEST -> RecipeWorkMode.TEST
    RecipeApiRequestDebugMode.STUB -> RecipeWorkMode.STUB
    null -> RecipeWorkMode.PROD
}
private fun RecipeApiDebug?.transportToStubCase() : RecipeStubs = when(this?.stub) {
    RecipeApiRequestDebugStubs.SUCCESS -> RecipeStubs.SUCCESS
    RecipeApiRequestDebugStubs.NOT_FOUND -> RecipeStubs.NOT_FOUND
    RecipeApiRequestDebugStubs.BAD_ID -> RecipeStubs.BAD_ID
    RecipeApiRequestDebugStubs.BAD_TITLE -> RecipeStubs.BAD_TITLE
    RecipeApiRequestDebugStubs.BAD_DESCRIPTION -> RecipeStubs.BAD_DESCRIPTION
    RecipeApiRequestDebugStubs.BAD_VISIBILITY -> RecipeStubs.BAD_VISIBILITY
    RecipeApiRequestDebugStubs.CANNOT_DELETE -> RecipeStubs.CANNOT_DELETE
    RecipeApiRequestDebugStubs.BAD_SEARCH_STRING -> RecipeStubs.BAD_SEARCH_STRING
    null -> RecipeStubs.NONE
}
private fun RecipeContext.fromTransport(request: RecipeApiRecipeCreateRequest) {
    command = RecipeCommand.CREATE
    requestId = request.requestId()
    recipeRequest = request.recipe?.toInternal() ?: Recipe()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

private fun RecipeContext.fromTransport(request: RecipeApiRecipeReadRequest) {
    command = RecipeCommand.READ
    requestId = request.requestId()
    recipeRequest = request.recipe.toRecipeWithId()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

private fun RecipeContext.fromTransport(request: RecipeApiRecipeUpdateRequest) {
    command = RecipeCommand.UPDATE
    requestId = request.requestId()
    recipeRequest = request.recipe?.toInternal() ?: Recipe()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

private fun RecipeContext.fromTransport(request: RecipeApiRecipeDeleteRequest) {
    command = RecipeCommand.DELETE
    requestId = request.requestId()
    recipeRequest = request.recipe.toRecipeWithIdAndLock()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

private fun RecipeContext.fromTransport(request: RecipeApiRecipeSearchRequest) {
    command = RecipeCommand.SEARCH
    requestId = request.requestId()
    recipeFilterRequest = request.recipeFilter.toInternal()
    workMode = request.debug.transportToWorkMode()
    stubCase = request.debug.transportToStubCase()
}

private fun RecipeApiRecipeSearchFilter?.toInternal() : RecipeFilter = RecipeFilter(
    searchString = this?.searchString ?: "",
)
