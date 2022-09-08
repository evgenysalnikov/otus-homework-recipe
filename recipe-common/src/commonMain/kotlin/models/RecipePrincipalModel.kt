package com.salnikoff.recipe.common.models

data class RecipePrincipalModel(
    val id: UserId = UserId.NONE,
    val fname: String = "",
    val mname: String = "",
    val lname: String = "",
    val groups: Set<RecipeUserGroups> = emptySet()
) {
    companion object {
        val NONE = RecipePrincipalModel()
        val SALNIKOV = RecipePrincipalModel(
            UserId("esalnikov"),
            "Evgeny",
            "Vladimirovitch",
            "Salnikov",
            setOf(RecipeUserGroups.USER)
        )
    }
}
