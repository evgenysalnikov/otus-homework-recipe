package com.salnikoff.recipe.repo.gremlin.mappers

import com.salnikoff.recipe.common.models.RecipeRequirement
import com.salnikoff.recipe.repo.gremlin.RecipeGremlinConst
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.VertexProperty

fun GraphTraversal<Vertex, Vertex>.addRequirement(requirement: RecipeRequirement): GraphTraversal<Vertex, Vertex>? = this
    .property(VertexProperty.Cardinality.single, RecipeGremlinConst.FIELD_REQUIREMENT, requirement.asString())
