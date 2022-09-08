package com.salnikoff.recipe.repo.gremlin.mappers

import com.salnikoff.recipe.common.models.Recipe
import com.salnikoff.recipe.common.models.RecipeRequirement
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_DESCRIPTION
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_DURATION
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_LOCK
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_OWNER_ID
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_REQUIREMENT
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_STEPS
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_TITLE
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst.FIELD_VISIBILITY
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.VertexProperty
import kotlin.time.DurationUnit

fun GraphTraversal<Vertex, Vertex>.addRecipe(recipe: Recipe): GraphTraversal<Vertex, Vertex>? = this
    .property(VertexProperty.Cardinality.single, FIELD_TITLE, recipe.title)
    .property(VertexProperty.Cardinality.single, FIELD_DESCRIPTION, recipe.description)
    .property(VertexProperty.Cardinality.single, FIELD_DURATION, recipe.duration.toLong(DurationUnit.SECONDS))
    .property(VertexProperty.Cardinality.single, FIELD_OWNER_ID, recipe.ownerId.asString())
    .property(VertexProperty.Cardinality.single, FIELD_VISIBILITY, recipe.visibility.name)
    .property(VertexProperty.Cardinality.single, FIELD_STEPS, recipe.steps)
    .property(VertexProperty.Cardinality.single, FIELD_LOCK, recipe.lock.asString())
//    .apply {
//        if (recipe.requirements.isNotEmpty()) {
//            addRequirements(recipe.requirements)
////            recipe.requirements.map { addRequirement(it) }
//        }
//        if (recipe.permissionsClient.isNotEmpty()) {
//            recipe.permissionsClient.map { addPermission(it) }
//        }
//    }

fun GraphTraversal<Vertex, Vertex>.addRequirements(rq: List<RecipeRequirement>) : GraphTraversal<Vertex, Vertex>? = this
    .addV(FIELD_REQUIREMENT).property(VertexProperty.Cardinality.single, FIELD_REQUIREMENT, "req1")
