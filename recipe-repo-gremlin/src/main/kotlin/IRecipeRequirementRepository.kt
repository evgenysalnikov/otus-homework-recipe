package com.salnikoff.recipe.repo.gremlin

import com.salnikoff.recipe.common.models.RecipeRequirement
import com.salnikoff.recipe.common.repo.*
import org.apache.tinkerpop.gremlin.structure.Vertex

internal interface IRecipeRequirementRepository {

    suspend fun createRecipeRequirement(req: RecipeRequirement): Vertex?

    suspend fun readRecipeRequirement(key: String): Vertex?

    suspend fun updateRecipeRequirement(req: RecipeRequirement): Vertex?

    suspend fun deleteRecipeRequirement(key: String): Boolean

    suspend fun searchRecipeRequirement(name: String): Vertex?

    

}
