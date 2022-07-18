package com.salnikoff.recipe.common.models

@JvmInline
value class RecipeLock(private val id: String) {
    fun asString() = id

    companion object {
        val NONE = RecipeLock("")
    }
}
