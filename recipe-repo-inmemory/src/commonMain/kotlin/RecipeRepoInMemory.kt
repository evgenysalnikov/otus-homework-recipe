package com.salnikoff.recipe.repo.inmemory

import com.benasher44.uuid.uuid4
import com.salnikoff.recipe.common.helpers.errorConcurrency
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.repo.*
import com.salnikoff.recipe.repo.inmemory.models.RecipeEntity
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class RecipeRepoInMemory(
    initObjects: List<Recipe> = emptyList(),
    ttl: Duration = 2.minutes,
    val randomUuid: () -> String = { uuid4().toString() }
) : IRecipeRepository {

    private val cache = Cache.Builder()
        .expireAfterWrite(ttl)
        .build<String, RecipeEntity>()

    private val mutex = Mutex()

    init {
        initObjects.forEach {
            save(it)
        }
    }

    private fun save(recipe: Recipe) {
        val entity = RecipeEntity(recipe)
        if (entity.id == null) {
            return
        }
        cache.put(entity.id, entity)
    }

    override suspend fun createRecipe(rq: DbRecipeRequest): DbRecipeResponse {
        val key = randomUuid()
        val recipe = rq.recipe.copy(id = RecipeId(key), lock = RecipeLock(randomUuid()))
        val entity = RecipeEntity(recipe)

        mutex.withLock {
            cache.put(key, entity)
        }
        return DbRecipeResponse(
            result = recipe,
            isSuccess = true,
        )
    }

    override suspend fun readRecipe(rq: DbRecipeIdRequest): DbRecipeResponse {
        val key = rq.id.takeIf { it != RecipeId.NONE }?.asString() ?: return resultErrorEmptyId
        return cache.get(key)
            ?.let {
                DbRecipeResponse(
                    result = it.toInternal(),
                    isSuccess = true,
                )
            } ?: resultErrorNotFound
    }

    override suspend fun updateRecipe(rq: DbRecipeRequest): DbRecipeResponse {
        val key = rq.recipe.id.takeIf { it != RecipeId.NONE }?.asString() ?: return resultErrorEmptyId
        val oldLock = rq.recipe.lock.takeIf { it != RecipeLock.NONE }?.asString()
        val newRecipe = rq.recipe.copy(lock = RecipeLock(randomUuid()))
        val entity = RecipeEntity(newRecipe)
        mutex.withLock {
            val local = cache.get(key)
            when {
                local == null -> return resultErrorNotFound
                local.lock == null || local.lock == oldLock -> cache.put(key, entity)
                else -> return resultErrorConcurrent
            }
        }
        return DbRecipeResponse(
            result = newRecipe,
            isSuccess = true,
        )
    }

    override suspend fun deleteRecipe(rq: DbRecipeIdRequest): DbRecipeResponse {
        val key = rq.id.takeIf { it != RecipeId.NONE }?.asString() ?: return resultErrorEmptyId
        mutex.withLock {
            val local = cache.get(key) ?: return resultErrorNotFound
            println(local.lock)
            println(rq.lock.asString())
            if (local.lock == rq.lock.asString()) {
                cache.invalidate(key)
                return DbRecipeResponse(
                    result = local.toInternal(),
                    isSuccess = true,
                    errors = emptyList()
                )
            } else {
                return resultErrorConcurrent
            }
        }
    }

    override suspend fun searchRecipe(rq: DbRecipeFilterRequest): DbRecipesResponse {
        val result = cache.asMap().asSequence()
            .filter { entry ->
                rq.ownerId.takeIf { it != UserId.NONE }?.let {
                    it.asString() == entry.value.ownerId
                } ?: true
            }
            .filter { entry ->
                rq.titleFilter.takeIf { it.isNotBlank() }?.let {
                    entry.value.title?.contains(it) ?: false
                } ?: true
            }
            .filter { entry ->
                rq.descriptionFilter.takeIf { it.isNotBlank() }?.let {
                    entry.value.description?.contains(it) ?: false
                } ?: true
            }
            .map { it.value.toInternal() }
            .toList()
        return DbRecipesResponse(
            result = result,
            isSuccess = true
        )
    }

    companion object {
        val resultErrorEmptyId = DbRecipeResponse(
            result = null,
            isSuccess = false,
            errors = listOf(
                RecipeError(field = "id", message = "Id must be not null or empty")
            )
        )

        val resultErrorNotFound = DbRecipeResponse(
            result = null,
            isSuccess = false,
            errors = listOf(
                RecipeError(field = "id", message = "Not Found")
            )
        )

        val resultErrorConcurrent = DbRecipeResponse(
            result = null,
            isSuccess = false,
            errors = listOf(
                errorConcurrency(
                    violationCode = "changed",
                    description = "Object has changed during request handling"
                )
            )
        )
    }
}