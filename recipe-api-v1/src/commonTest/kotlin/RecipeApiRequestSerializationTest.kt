package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RecipeApiRequestSerializationTest {
    @Test
    fun recipeCreateRequestSerializationTest() {
        val createRequest = RecipeApiRecipeCreateRequest(
            recipe = RecipeApiRecipeCreateObject(
                title = "Title",
                description = "description",
                requirements = listOf(
                    "req1", "req2"
                ),
                duration = RecipeApiDuration(
                    duration = "3",
                    timeunit = RecipeApiTimeUnit.MIN
                ),
                ownerId = "ownerId",
                visibility = RecipeApiRecipeVisibility.PUBLIC,
                steps = "Step`s description or how to reproduce"
            ),
            debug = RecipeApiDebug(
                mode = RecipeApiRequestDebugMode.PROD,
                stub = RecipeApiRequestDebugStubs.BAD_ID
            )
        )
        val jsonString = recipeApiV1RequestSerialize(createRequest)
        assertContains(jsonString, """"title":"Title"""")
        assertContains(jsonString, """"requestType":"create"""")
    }

    @Test
    fun recipeCreateRequestDeserializationTest() {
        val jsonString =
            "{\"requestType\":\"create\",\"requestId\":null,\"recipe\":{\"title\":\"Title\",\"description\":\"description\",\"requirements\":[\"req1\",\"req2\"],\"duration\":{\"duration\":\"3\",\"timeunit\":\"min\"},\"ownerId\":\"ownerId\",\"visibility\":\"public\",\"steps\":\"Step`s description or how to reproduce\"},\"debug\":{\"mode\":\"prod\",\"stub\":\"badId\"}}"
        val recipeCreateRequest = recipeApiV1RequestDeserialize<RecipeApiRecipeCreateRequest>(jsonString)
        assertEquals("Title", recipeCreateRequest.recipe?.title)
        assertEquals("Step`s description or how to reproduce", recipeCreateRequest.recipe?.steps)
    }

    @Test
    fun recipeReadRequestSerializationTest() {
        val readRequest = RecipeApiRecipeReadRequest(
            requestId = "my_request_id",
            recipe = RecipeApiBaseWithId(
                recipe = RecipeApiBaseWithIdRecipe("recipe_id")
            ),
            debug = RecipeApiDebug(
                mode = RecipeApiRequestDebugMode.PROD,
                stub = RecipeApiRequestDebugStubs.BAD_ID
            ),
        )

        val jsonString = recipeApiV1RequestSerialize(readRequest)
        assertContains(jsonString, """"id":"recipe_id"""")
        assertContains(jsonString, """"requestType":"read"""")
    }

    @Test
    fun recipeReadRequestDeserializationTest() {
        val jsonString =
            "{\"requestType\":\"read\",\"requestId\":\"my_request_id\",\"recipe\":{\"recipe\":{\"id\":\"recipe_id\"}},\"debug\":{\"mode\":\"prod\",\"stub\":\"badId\"}}"
        val recipeReadRequest = recipeApiV1RequestDeserialize<RecipeApiRecipeReadRequest>(jsonString)
        assertEquals("my_request_id", recipeReadRequest.requestId)
        assertEquals("recipe_id", recipeReadRequest.recipe?.recipe?.id)
    }

    @Test
    fun recipeUpdateRequestSerializationTest() {

        val updateRequest = RecipeApiRecipeUpdateRequest(
            requestId = "my_request_id",
            recipe = RecipeApiRecipeUpdateWithLockObject(
                id = "updated_recipe_id",
                title = "Title",
                description = "description",
                requirements = listOf(
                    "req1", "req2"
                ),
                duration = RecipeApiDuration(
                    duration = "3",
                    timeunit = RecipeApiTimeUnit.MIN
                ),
                ownerId = "ownerId",
                visibility = RecipeApiRecipeVisibility.PUBLIC,
                steps = "Step`s description or how to reproduce",
                lock = "11111-11111-11111-11111"
            ),
            debug = RecipeApiDebug(
                mode = RecipeApiRequestDebugMode.PROD,
                stub = RecipeApiRequestDebugStubs.BAD_ID
            ),
        )

        val jsonString = recipeApiV1RequestSerialize(updateRequest)
        assertContains(jsonString, """"id":"updated_recipe_id"""")
        assertContains(jsonString, """"requestType":"update"""")
        assertContains(jsonString, """"lock":"11111-11111-11111-11111"""")
    }

    @Test
    fun recipeUpdateRequestDeserializationTest() {
        val jsonString =
            "{\"requestType\":\"update\",\"requestId\":\"my_request_id\",\"debug\":{\"mode\":\"prod\",\"stub\":\"badId\"},\"Recipe\":{\"title\":\"Title\",\"description\":\"description\",\"requirements\":[\"req1\",\"req2\"],\"duration\":{\"duration\":\"3\",\"timeunit\":\"min\"},\"ownerId\":\"ownerId\",\"visibility\":\"public\",\"steps\":\"Step`s description or how to reproduce\",\"id\":\"updated_recipe_id\", \"lock\":\"11111-11111-11111-11111\"}}"
        val recipeUpdateRequest = recipeApiV1RequestDeserialize<RecipeApiRecipeUpdateRequest>(jsonString)
        assertEquals("my_request_id", recipeUpdateRequest.requestId)
        assertEquals("Title", recipeUpdateRequest.recipe?.title)
        assertEquals("updated_recipe_id", recipeUpdateRequest.recipe?.id)
        assertEquals("11111-11111-11111-11111", recipeUpdateRequest.recipe?.lock)
    }

    @Test
    fun recipeDeleteRequestSerializationTest() {
        val recipeDeleteRequest = RecipeApiRecipeDeleteRequest(
            requestId = "my_delete_request",
            recipe = RecipeApiBaseWithIdAndLock(
                recipe = RecipeApiBaseWithIdAndLockRecipe(
                    id = "recipe_id",
                    lock = "11111-11111-11111-11111"
                ),
            ),
            debug = RecipeApiDebug(
                mode = RecipeApiRequestDebugMode.PROD,
                stub = RecipeApiRequestDebugStubs.BAD_ID
            ),
        )

        val jsonString = recipeApiV1RequestSerialize(recipeDeleteRequest)
        assertContains(jsonString, """"requestType":"delete"""")
        assertContains(jsonString, """"id":"recipe_id"""")
        assertContains(jsonString, """"lock":"11111-11111-11111-11111"""")
    }

    @Test
    fun recipeDeleteRequestDeserializationTest() {
        val jsonString =
            "{\"requestType\":\"delete\",\"requestId\":\"my_delete_request\",\"recipe\":{\"recipe\":{\"id\":\"recipe_id\", \"lock\":\"11111-11111-11111-11111\"}},\"debug\":{\"mode\":\"prod\",\"stub\":\"badId\"}}"
        val recipeDeleteRequest = recipeApiV1RequestDeserialize<RecipeApiRecipeDeleteRequest>(jsonString)
        assertEquals("recipe_id", recipeDeleteRequest.recipe?.recipe?.id)
        assertEquals("my_delete_request", recipeDeleteRequest.requestId)
        assertEquals("11111-11111-11111-11111", recipeDeleteRequest.recipe?.recipe?.lock)
    }

    @Test
    fun recipeSearchRequestSerializationTest() {
        val recipeSearchRequest = RecipeApiRecipeSearchRequest(
            requestId = "my_search_request",
            debug = RecipeApiDebug(
                mode = RecipeApiRequestDebugMode.PROD,
                stub = RecipeApiRequestDebugStubs.BAD_ID
            ),
            recipeFilter = RecipeApiRecipeSearchFilter("search string")
        )

        val jsonString = recipeApiV1RequestSerialize(recipeSearchRequest)
        assertContains(jsonString, """"requestType":"search"""")
        assertContains(jsonString, """"requestId":"my_search_request"""")
        assertContains(jsonString, """"searchString":"search string"""")
    }

    @Test
    fun recipeSearchRequestDeserializationTest() {
        val jsonString =
            "{\"requestType\":\"search\",\"requestId\":\"my_search_request\",\"debug\":{\"mode\":\"prod\",\"stub\":\"badId\"},\"recipeFilter\":{\"searchString\":\"search string\"}}"
        val recipeSearchRequest = recipeApiV1RequestDeserialize<RecipeApiRecipeSearchRequest>(jsonString)
        assertEquals("my_search_request", recipeSearchRequest.requestId)
        assertEquals("search string", recipeSearchRequest.recipeFilter?.searchString)
    }
}
