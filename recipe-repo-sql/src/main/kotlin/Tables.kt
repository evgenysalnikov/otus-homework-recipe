package com.salnikoff.recipe.repo.sql

import com.salnikoff.recipe.common.models.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.util.*
import kotlin.time.Duration.Companion.seconds

object RecipeTable : StringIdTable("Recipe") {
    val title = varchar("title", 128)
    val description = text("description") //todo add lenght constraint
    val duration = long("duration")
    val ownerId = reference("owner_id", UsersTable.id).index()
    val visibility = enumeration("visibility", RecipeVisibility::class)
    val steps = text("steps")
    val lock = varchar("lock", 50)

    // Mapper functions from sql-like table to MkplAd
    fun from(res: InsertStatement<Number>) = Recipe(
        id = RecipeId(res[id].toString()),
        title = res[title],
        description = res[description],
        duration = res[duration].seconds,
        ownerId = UserId(res[ownerId].toString()),
        visibility = res[visibility],
        steps = res[steps],
        lock = RecipeLock(res[lock])
    )

    fun from(res: ResultRow) = Recipe(
        id = RecipeId(res[id].toString()),
        title = res[title],
        description = res[description],
        requirements = listOf(),
        duration = res[duration].seconds,
        ownerId = UserId(res[ownerId].toString()),
        visibility = res[visibility],
        steps = res[steps],
        lock = RecipeLock(res[lock])
    )
}

object RequirementTable : StringIdTable("Requirement") {
    val name = varchar("name", 50).uniqueIndex("requirements_uniq_name")
    init {
        uniqueIndex("requirement_name", name)
    }
}

object UsersTable : StringIdTable("Users")

object RecipeRequirementTable : Table("RecipeRequirement") {
    val recipe = reference("recipe", RecipeTable)
    val requirement = reference("requirement", RequirementTable)

    override val primaryKey = PrimaryKey(recipe, requirement)
}


open class StringIdTable(name: String = "", columnName: String = "id", columnLength: Int = 50) : IdTable<String>(name) {
    override val id: Column<EntityID<String>> =
        varchar(columnName, columnLength).uniqueIndex().default(generateUuidAsStringFixedSize())
            .entityId()
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}

fun generateUuidAsStringFixedSize() = UUID.randomUUID().toString().replace("-", "").substring(0, 9)