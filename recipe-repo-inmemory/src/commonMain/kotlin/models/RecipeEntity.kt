package com.salnikoff.recipe.repo.inmemory.models

import com.salnikoff.recipe.common.models.*
import kotlin.time.Duration

data class RecipeEntity(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val requirements: List<RecipeRequirementEntity>? = emptyList(),
    val duration: Duration? = Duration.ZERO,
    val ownerId: String? = null,
    val visibility: String? = null,
    val steps: String? = null,
    val permissionsClient: MutableSet<RecipePermissionClientEntity>? = mutableSetOf(),
    val lock: String? = null

) {
    constructor(model: Recipe) : this(
        id = model.id.asString().takeIf { it.isNotBlank() },
        title = model.title.takeIf { it.isNotBlank() },
        description = model.description.takeIf { it.isNotBlank() },
        requirements = model.requirements.toEntity(),
        duration = model.duration,
        ownerId = model.ownerId.asString().takeIf { it.isNotBlank() },
        visibility = model.visibility.toString().takeIf { it.isNotBlank() },
        steps = model.steps.toString().takeIf { it.isNotBlank() },
        permissionsClient = model.permissionsClient.toEntity(),
        lock = model.lock.takeIf { it != RecipeLock.NONE }?.asString()
    )

    fun toInternal(): Recipe = Recipe(
        id = id?.let { RecipeId(id) } ?: RecipeId.NONE,
        title = title ?: "",
        description = description ?: "",
        requirements = requirements.toInternal(),
        duration = duration ?: Duration.ZERO,
        ownerId = ownerId?.let { UserId(it) } ?: UserId.NONE,
        visibility = visibility?.let { RecipeVisibility.valueOf(visibility) } ?: RecipeVisibility.NONE,
        steps = steps ?: "",
        permissionsClient = permissionsClient?.toInternal() ?: mutableSetOf(),
        lock = lock?.let { RecipeLock(it) } ?: RecipeLock.NONE
    )
}

internal fun MutableSet<RecipePermissionClientEntity>?.toInternal(): MutableSet<RecipePermissionClient> = when (this) {
    is MutableSet<RecipePermissionClientEntity> -> this.map { RecipePermissionClient.valueOf(it.toString()) }
        .toMutableSet()
    null -> mutableSetOf()
    else -> mutableSetOf()
}

internal fun List<RecipeRequirementEntity>?.toInternal(): List<RecipeRequirement> = when (this) {
    is List<RecipeRequirementEntity> -> this.map {
        if (it != RecipeRequirementEntity.NONE) {
            RecipeRequirement(it.asString())
        } else {
            RecipeRequirement.NONE
        }
    }
    null -> emptyList()
    else -> emptyList()
}

internal fun List<RecipeRequirement>?.toEntity(): List<RecipeRequirementEntity> = when (this) {
    is List<RecipeRequirement> -> this.map {
        if (it != RecipeRequirement.NONE) {
            RecipeRequirementEntity(it.asString())
        } else {
            RecipeRequirementEntity.NONE
        }
    }
    null -> emptyList()
    else -> emptyList()
}

internal fun MutableSet<RecipePermissionClient>?.toEntity(): MutableSet<RecipePermissionClientEntity> = when (this) {
    is MutableSet<RecipePermissionClient> -> this.map { RecipePermissionClientEntity.valueOf(it.toString()) }
        .toMutableSet()
    null -> mutableSetOf()
    else -> mutableSetOf()
}
