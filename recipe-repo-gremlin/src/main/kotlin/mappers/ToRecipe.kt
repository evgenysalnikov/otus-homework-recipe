package com.salnikoff.recipe.repo.gremlin.mappers

import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_DESCRIPTION
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_DURATION
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_LOCK
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_OWNER_ID
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_PERMISSIONS_CLIENT
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_REQUIREMENT
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_STEPS
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_TITLE
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_VISIBILITY
import org.apache.tinkerpop.gremlin.structure.T
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


fun Map<Any?, Any?>.toRecipe(): Recipe = Recipe(
    id = (this[T.id] as? String)?.let { RecipeId(it) } ?: RecipeId.NONE,
    title = (this[FIELD_TITLE] as? String) ?: "",
    description = (this[FIELD_DESCRIPTION] as? String) ?: "",
//    requirements = listOf(RecipeRequirement(this[FIELD_REQUIREMENT] as String)) ?: listOf(),
    duration = (this[FIELD_DURATION] as? Long)?.seconds ?: Duration.ZERO,
    ownerId = (this[FIELD_OWNER_ID] as? String)?.let { UserId(it) } ?: UserId.NONE,
    visibility = RecipeVisibility.valueOf(this[FIELD_VISIBILITY] as? String ?: "") ?: RecipeVisibility.NONE,
    steps = (this[FIELD_STEPS] as? String) ?: "",
//    permissionsClient = mutableSetOf(RecipePermissionClient.valueOf(this[FIELD_PERMISSIONS_CLIENT] as String)) ?: mutableSetOf(),
    lock = (this[FIELD_LOCK] as? String)?.let { RecipeLock(it) } ?: RecipeLock.NONE,
)


fun Map<String, Any?>.toRecipe2(): Recipe = Recipe(
    id = (this["id"] as? String)?.let { RecipeId(it) } ?: RecipeId.NONE,
    title = (this[FIELD_TITLE] as? String) ?: "",
    description = (this[FIELD_DESCRIPTION] as? String) ?: "",
    requirements = (this[FIELD_REQUIREMENT] as? List<*>)?.map { RecipeRequirement(it.toString()) } ?: listOf(),
    duration = (this[FIELD_DURATION] as? Long)?.seconds ?: Duration.ZERO,
    ownerId = (this[FIELD_OWNER_ID] as? String)?.let { UserId(it) } ?: UserId.NONE,
    visibility = RecipeVisibility.valueOf(this[FIELD_VISIBILITY] as? String ?: "") ?: RecipeVisibility.NONE,
    steps = (this[FIELD_STEPS] as? String) ?: "",
    permissionsClient = (this[FIELD_PERMISSIONS_CLIENT] as? List<*>)?.map { RecipePermissionClient.valueOf(it.toString()) }?.toMutableSet() ?: mutableSetOf(),
    lock = (this[FIELD_LOCK] as? String)?.let { com.salnikoff.recipe.common.models.RecipeLock(it) } ?: RecipeLock.NONE,
)
