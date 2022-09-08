package com.salnikoff.recipe.repo.gremlin.mappers

import com.salnikoff.recipe.common.models.Recipe

fun Recipe.label(): String? = this::class.simpleName
