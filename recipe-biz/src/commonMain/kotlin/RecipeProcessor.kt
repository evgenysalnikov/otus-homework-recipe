package com.salnikoff.recipe.biz

import com.salnikoff.recipe.biz.general.initRepo
import com.salnikoff.recipe.biz.general.initStatus
import com.salnikoff.recipe.biz.general.operation
import com.salnikoff.recipe.biz.general.prepareResult
import com.salnikoff.recipe.biz.permissions.accessValidation
import com.salnikoff.recipe.biz.permissions.chainPermissions
import com.salnikoff.recipe.biz.permissions.frontPermissions
import com.salnikoff.recipe.biz.permissions.searchTypes
import com.salnikoff.recipe.biz.repo.*
import com.salnikoff.recipe.biz.stubs.*
import com.salnikoff.recipe.biz.validation.*
import com.salnikoff.recipe.chain.handlers.chain
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.chain.rootChain
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeCommand
import com.salnikoff.recipe.common.models.RecipeId
import com.salnikoff.recipe.common.models.RecipeSettings

class RecipeProcessor(private val settings: RecipeSettings = RecipeSettings()) {
    suspend fun exec(ctx: RecipeContext) = BuzinessChain.exec(ctx.apply { settings = this@RecipeProcessor.settings })

    companion object {
        private val BuzinessChain = rootChain<RecipeContext> {
            initStatus("Init", "Status from NONE to RUNNING")
            initRepo("Init recipe repository", "Init recipe repository by current environment")

            operation("Create recipe", "Store recipe from request", RecipeCommand.CREATE) {
                stubs("Stubs create", "Stub logic for create recipe") {
                    stubCreateSuccess("Success stub", "Like everything ok and recipe was saved")
                    stubValidationBadTitle("Title validate error", "Bad recipe title error")
                    stubValidationBadDescription("Description validate error", "Bad recipe description error")

                    stubNoCase("Unknown stub", "Error: wrong stub")
                }
                chain {
                    title = "Request validation"
                    description = "Check request content for all conditions"

                    worker(title = "Copy fields", description = "Copy fields to recipeValidating") {
                        recipeValidating = recipeRequest.deepCopy()
                    }
                    worker(title = "Trim title", description = "Trim title string") {
                        recipeValidating.title = recipeValidating.title.trim()
                    }
                    worker(title = "Trim description", description = "Trim description string") {
                        recipeValidating.description = recipeValidating.description.trim()
                    }

                    validateTitleNotEmpty("Title not empty", "Title should contains letters")
                    validateTitleHasContent("Title has content", "Title should contains letters")
                    validateDescriptionNotEmpty("Description not empty", "Description should contains letters")
                    validateDescriptionHasContent("Description has content", "Description should contains letters")

                    finishRecipeValidation("Validation done", "Finish validation and copy result")
                }

                chainPermissions("User permissions", "User permissions")
                worker {
                    title = "Инициализация recipeRepoRead"
                    on { state == CorState.RUNNING }
                    handle {
                        recipeRepoRead = recipeValidated
                        recipeRepoRead.ownerId = principal.id
                    }
                }
                accessValidation("User rights", "User rights")

                chain {
                    title = "Логика сохранения"
                    description = "Сохраняем рецепт"
                    repoPrepareCreate("Подготовка объекта для сохранения")
                    repoCreate("Создание рецепта в БД")
                }

                frontPermissions("Вычисление пользовательских разрешений для фронтенда")
                prepareResult("Подготовка ответа")
            }
            operation("Read recipe", "Read stored recipe by id", RecipeCommand.READ) {
                stubs("Stubs read", "Stubs for read recipe logig") {
                    stubReadSuccess("Success stub", "Like everything ok and recipe was read")
                    stubValidationBadId("Id validate error", "Bad recipe id error")

                    stubNoCase("Unknown stub", "Error: wrong stub")
                }
                chain {
                    worker(title = "Copy fields", description = "Copy fields to recipeValidating") {
                        recipeValidating = recipeRequest.deepCopy()
                    }
                    worker("Clear id", "Check and clear id value") {
                        recipeValidating.id = RecipeId(recipeValidating.id.asString().trim())
                    }
                    validateIdNotEmpty("Id not empty", "Id needed")
                    validateIdProperFormat("Format id", "Wrong id format")

                    finishRecipeValidation("Validation done", "Finish validation and copy result")
                }

                chainPermissions("User permissions", "User permissions")

                chain {
                    title = "Логика сохранения"
                    repoRead("Чтение объявления из БД")
                    accessValidation("User rights", "User rights")
                    worker {
                        title = "Подготовка ответа для Read"
                        on { state == CorState.RUNNING }
                        handle { recipeRepoDone = recipeRepoRead }
                    }
                }
                frontPermissions("Вычисление пользовательских разрешений для фронтенда")
                prepareResult("Подготовка ответа")
            }
            operation("Update recipe", "Update existed recipe", RecipeCommand.UPDATE) {
                stubs("Stubs update", "Stubs for update recipe logic") {
                    stubUpdateSuccess("Success stub", "Update recipe stubs")
                    stubValidationBadId("Id validate error", "Bad recipe id error")
                    stubValidationBadTitle("Title validate error", "Bad recipe title error")
                    stubValidationBadDescription("Description validate error", "Bad recipe description error")

                    stubNoCase("Unknown stub", "Error: wrong stub")
                }
                chain {
                    worker(title = "Copy fields", description = "Copy fields to recipeValidating") {
                        recipeValidating = recipeRequest.deepCopy()
                    }
                    worker("Clear id", "Check and clear id value") {
                        recipeValidating.id = RecipeId(recipeValidating.id.asString().trim())
                    }
                    worker("Clear title", "Trim title value") {
                        recipeValidating.title = recipeValidating.title.trim()
                    }
                    worker("Clear description", "Trim description value") {
                        recipeValidating.description = recipeValidating.description.trim()
                    }
                    validateIdNotEmpty("Id not empty", "Id needed")
                    validateIdProperFormat("Format id", "Wrong id format")
                    validateTitleNotEmpty("Title not empty", "Title should contains letters")
                    validateTitleHasContent("Title has content", "Title should contains letters")
                    validateDescriptionNotEmpty("Description not empty", "Description should contains letters")
                    validateDescriptionHasContent("Description has content", "Description should contains letters")

                    finishRecipeValidation("Validation done", "Finish validation and copy result")
                }

                chainPermissions("User permissions", "User permissions")

                chain {
                    title = "Логика сохранения"
                    repoRead("Чтение объявления из БД")
                    accessValidation("User rights", "User rights")
                    repoCheckReadLock("Проверяем блокировку")
                    repoPrepareUpdate("Подготовка объекта для обновления")
                    repoUpdate("Обновление рецепта в БД")
                }
                frontPermissions("Вычисление пользовательских разрешений для фронтенда")
                prepareResult("Подготовка ответа")
            }
            operation("Delete recipe", "Delete existed recipe", RecipeCommand.DELETE) {
                stubs("Stubs delete", "Stubs for delete recipe logic") {
                    stubDeleteSuccess("Success stub", "Update recipe stubs")
                    stubValidationBadId("Id validate error", "Bad recipe id error")

                    stubNoCase("Unknown stub", "Error: wrong stub")
                }
                chain {
                    worker(title = "Copy fields", description = "Copy fields to recipeValidating") {
                        recipeValidating = recipeRequest.deepCopy()
                    }
                    worker("Clear id", "Check and clear id value") {
                        recipeValidating.id = RecipeId(recipeValidating.id.asString().trim())
                    }
                    validateIdNotEmpty("Id not empty", "Id needed")
                    validateIdProperFormat("Format id", "Wrong id format")

                    finishRecipeValidation("Validation done", "Finish validation and copy result")
                }

                chainPermissions("User permissions", "User permissions")

                chain {
                    title = "Логика сохранения"
                    repoRead("Чтение объявления из БД")
                    accessValidation("User rights", "User rights")
                    repoCheckReadLock("Проверяем блокировку")
                    repoPrepareDelete("Подготовка объекта для удаления")
                    repoDelete("Удаление рецепта из БД")
                }
                prepareResult("Подготовка ответа")
            }
            operation("Search recipe", "Search existed recipe", RecipeCommand.SEARCH) {
                stubs("Stubs search", "Stubs for search recipe logic") {
                    stubSearchSuccess("Success stub", "search recipe stubs")

                    stubNoCase("Unknown stub", "Error: wrong stub")
                }
                chain {
                    worker(title = "Copy fields", description = "Copy fields to recipeValidating") {
                        recipeFilterValidating = recipeFilterRequest.copy()
                    }

                    finishRecipeFilterValidation("Validation done", "Finish validation and copy result")
                }

                chainPermissions("User permissions", "User permissions")
                searchTypes("Подготовка поискового запроса")
                repoSearch("Поиск объявления в БД по фильтру")
                prepareResult("Подготовка ответа")
            }
        }.build()
    }
}
