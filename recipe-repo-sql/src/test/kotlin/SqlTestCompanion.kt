package com.salnikoff.recipe.repo.sql.test

import com.salnikoff.recipe.common.models.Recipe
import com.salnikoff.recipe.common.models.RecipeLock
import com.salnikoff.recipe.repo.sql.*
import org.jetbrains.exposed.sql.Database
import org.testcontainers.containers.PostgreSQLContainer
import java.time.Duration
import java.util.*

class PostgresContainer : PostgreSQLContainer<PostgresContainer>("postgres:13.2")

object SqlTestCompanion {
    private const val USER = "recipe"
    private const val PASS = "recipe-pass"
    private const val SCHEMA = "recipe"

    private val container by lazy {
        PostgresContainer().apply {
            withUsername(USER)
            withPassword(PASS)
            withDatabaseName(SCHEMA)
            withStartupTimeout(Duration.ofSeconds(300L))
            start()
        }
    }

    private val url: String by lazy { container.jdbcUrl }

    fun repoUnderTestContainer(
        initObjects: Collection<Recipe> = emptyList(),
        newRecipeUpdateLock: RecipeLock = RecipeLock(UUID.randomUUID().toString())
    ): RecipeRepoSQL {
        return RecipeRepoSQL(url, USER, PASS, SCHEMA, initObjects, newRecipeUpdateLock)
    }

    fun dbUnderTestContainer(): Database {
        return SqlConnector(url, USER, PASS, SCHEMA).connect(
            UsersTable,
            RequirementTable,
            RecipeTable,
            RecipeRequirementTable
        )
    }

}