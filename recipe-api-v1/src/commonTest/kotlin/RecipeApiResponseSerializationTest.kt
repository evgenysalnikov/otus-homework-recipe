package com.salnikoff.recipe.api.v1

import com.salnikoff.recipe.api.v1.models.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RecipeApiResponseSerializationTest {

    @Test
    fun recipeCreateResponseSerializationTest() {
        val recipeCreateResponse = RecipeApiRecipeCreateResponse(
            requestId = "request_id",
            result = RecipeApiResponseResult.SUCCESS,
            errors = listOf(
                RecipeApiError(
                    code = "10",
                    group = "group",
                    field = "id",
                    message = "error message"
                )
            ),
            recipe = RecipeApiRecipeResponseObject(
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
                lock = "12345-12345-12345-12345"
            )
        )

        val jsonString = recipeApiV1ResponseSerialize(recipeCreateResponse)
        assertContains(jsonString, """"title":"Title"""")
        assertContains(jsonString, """"responseType":"create"""")
        assertContains(jsonString, """"lock":"12345-12345-12345-12345"""")
    }

    @Test
    fun recipeCreateResponseDeserializationTest() {
        val jsonString =
            "{\"responseType\":\"create\",\"requestId\":\"request_id\",\"result\":\"success\",\"errors\":[{\"code\":\"10\",\"group\":\"group\",\"field\":\"id\",\"message\":\"error message\"}],\"recipe\":{\"title\":\"Title\",\"description\":\"description\",\"requirements\":[\"req1\",\"req2\"],\"duration\":{\"duration\":\"3\",\"timeunit\":\"min\"},\"ownerId\":\"ownerId\",\"visibility\":\"public\",\"steps\":\"Step`s description or how to reproduce\",\"id\":null,\"permissions\":null, \"lock\":\"12345-12345-12345-12345\"}}"
        val recipeCreateResponse = apiV1ResponseDeserialize<RecipeApiRecipeCreateResponse>(jsonString)
        assertEquals("Title", recipeCreateResponse.recipe?.title)
        assertEquals(RecipeApiResponseResult.SUCCESS, recipeCreateResponse.result)
        assertEquals("12345-12345-12345-12345", recipeCreateResponse.recipe?.lock)
    }

    @Test
    fun recipeReadResponseSerializationTest() {
        val recipeReadResponse = RecipeApiRecipeReadResponse(
            requestId = "read_request_id",
            result = RecipeApiResponseResult.SUCCESS,
            errors = listOf(
                RecipeApiError(
                    code = "10",
                    group = "group",
                    field = "id",
                    message = "error message"
                )
            ),
            recipe = RecipeApiRecipeResponseObject(
                id = "recipe_id",
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
                lock = "12345-12345-12345-12345"
            )
        )

        val jsonString = recipeApiV1ResponseSerialize(recipeReadResponse)
        assertContains(jsonString, """"title":"Title"""")
        assertContains(jsonString, """"responseType":"read"""")
        assertContains(jsonString, """"lock":"12345-12345-12345-12345"""")
    }

    @Test
    fun recipeReadResponseDeserializationTest() {
        val jsonString =
            "{\"responseType\":\"read\",\"requestId\":\"read_request_id\",\"result\":\"success\",\"errors\":[{\"code\":\"10\",\"group\":\"group\",\"field\":\"id\",\"message\":\"error message\"}],\"recipe\":{\"title\":\"Title\",\"description\":\"description\",\"requirements\":[\"req1\",\"req2\"],\"duration\":{\"duration\":\"3\",\"timeunit\":\"min\"},\"ownerId\":\"ownerId\",\"visibility\":\"public\",\"steps\":\"Step`s description or how to reproduce\",\"id\":\"recipe_id\",\"permissions\":null, \"lock\":\"12345-12345-12345-12345\"}}"
        val readResponse = apiV1ResponseDeserialize<RecipeApiRecipeReadResponse>(jsonString)
        assertEquals("recipe_id", readResponse.recipe?.id)
        assertEquals("Title", readResponse.recipe?.title)
        assertEquals("12345-12345-12345-12345", readResponse.recipe?.lock)
    }

    @Test
    fun recipeUpdateResponseSerializationTest() {
        val recipeUpdateResponse = RecipeApiRecipeUpdateResponse(
            requestId = "update_response_id",
            result = RecipeApiResponseResult.SUCCESS,
            errors = listOf(
                RecipeApiError(
                    code = "10",
                    group = "group",
                    field = "id",
                    message = "error message"
                )
            ),
            recipe = RecipeApiRecipeResponseObject(
                id = "recipe_id",
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
                lock = "12345-12345-12345-12345"
            )
        )

        val jsonString = recipeApiV1ResponseSerialize(recipeUpdateResponse)
        assertContains(jsonString, """"title":"Title"""")
        assertContains(jsonString, """"responseType":"update"""")
        assertContains(jsonString, """"id":"recipe_id"""")
        assertContains(jsonString, """"lock":"12345-12345-12345-12345"""")
    }

    @Test
    fun recipeUpdateResponseDeserializationTest() {
        val jsonString =
            "{\"responseType\":\"update\",\"requestId\":\"update_response_id\",\"result\":\"success\",\"errors\":[{\"code\":\"10\",\"group\":\"group\",\"field\":\"id\",\"message\":\"error message\"}],\"recipe\":{\"title\":\"Title\",\"description\":\"description\",\"requirements\":[\"req1\",\"req2\"],\"duration\":{\"duration\":\"3\",\"timeunit\":\"min\"},\"ownerId\":\"ownerId\",\"visibility\":\"public\",\"steps\":\"Step`s description or how to reproduce\",\"id\":\"recipe_id\",\"permissions\":null, \"lock\":\"12345-12345-12345-12345\"}}"
        val recipeUpdateResponse = apiV1ResponseDeserialize<RecipeApiRecipeUpdateResponse>(jsonString)
        assertEquals("recipe_id", recipeUpdateResponse.recipe?.id)
        assertEquals("Title", recipeUpdateResponse.recipe?.title)
        assertEquals("12345-12345-12345-12345", recipeUpdateResponse.recipe?.lock)
    }

    @Test
    fun recipeDeleteResponseSerializationTest() {
        val recipeDeleteResponse = RecipeApiRecipeDeleteResponse(
            requestId = "delete_response_id",
            result = RecipeApiResponseResult.SUCCESS,
            errors = listOf(
                RecipeApiError(
                    code = "10",
                    group = "group",
                    field = "id",
                    message = "error message"
                )
            ),
            recipe = RecipeApiRecipeResponseObject(
                id = "recipe_id",
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
                lock = "12345-12345-12345-12345"
            )
        )

        val jsonString = recipeApiV1ResponseSerialize(recipeDeleteResponse)
        assertContains(jsonString, """"title":"Title"""")
        assertContains(jsonString, """"responseType":"delete"""")
        assertContains(jsonString, """"id":"recipe_id"""")
        assertContains(jsonString, """"lock":"12345-12345-12345-12345"""")
    }

    @Test
    fun recipeDeleteResponseDeserializationTest() {
        val jsonString =
            "{\"responseType\":\"delete\",\"requestId\":\"delete_response_id\",\"result\":\"success\",\"errors\":[{\"code\":\"10\",\"group\":\"group\",\"field\":\"id\",\"message\":\"error message\"}],\"recipe\":{\"title\":\"Title\",\"description\":\"description\",\"requirements\":[\"req1\",\"req2\"],\"duration\":{\"duration\":\"3\",\"timeunit\":\"min\"},\"ownerId\":\"ownerId\",\"visibility\":\"public\",\"steps\":\"Step`s description or how to reproduce\",\"id\":\"recipe_id\",\"permissions\":null, \"lock\":\"12345-12345-12345-12345\"}}"
        val recipeDeleteResponse = apiV1ResponseDeserialize<RecipeApiRecipeDeleteResponse>(jsonString)
        assertEquals("recipe_id", recipeDeleteResponse.recipe?.id)
        assertEquals("Title", recipeDeleteResponse.recipe?.title)
        assertEquals("12345-12345-12345-12345", recipeDeleteResponse.recipe?.lock)
    }

    @Test
    fun recipeSearchResponseSerializationTest() {
        val recipeSearchResponse = RecipeApiRecipeSearchResponse(
            requestId = "delete_response_id",
            result = RecipeApiResponseResult.SUCCESS,
            errors = listOf(
                RecipeApiError(
                    code = "10",
                    group = "group",
                    field = "id",
                    message = "error message"
                )
            ),
            recipes = listOf(
                RecipeApiRecipeResponseObject(
                    id = "recipe_id",
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
                    lock = "12345-12345-12345-12345"
                )
            )
        )

        val jsonString = recipeApiV1ResponseSerialize(recipeSearchResponse)
        assertContains(jsonString, """"title":"Title"""")
        assertContains(jsonString, """"responseType":"search"""")
        assertContains(jsonString, """"id":"recipe_id"""")
        assertContains(jsonString, """"lock":"12345-12345-12345-12345"""")
    }

    @Test
    fun recipeSearchResponseDeserializationTest() {
        val jsonString =
            "{\"responseType\":\"search\",\"requestId\":\"delete_response_id\",\"result\":\"success\",\"errors\":[{\"code\":\"10\",\"group\":\"group\",\"field\":\"id\",\"message\":\"error message\"}],\"recipes\":[{\"title\":\"Title\",\"description\":\"description\",\"requirements\":[\"req1\",\"req2\"],\"duration\":{\"duration\":\"3\",\"timeunit\":\"min\"},\"ownerId\":\"ownerId\",\"visibility\":\"public\",\"steps\":\"Step`s description or how to reproduce\",\"id\":\"recipe_id\",\"permissions\":null, \"lock\":\"12345-12345-12345-12345\"}]}"
        val recipeSearchResponse = apiV1ResponseDeserialize<RecipeApiRecipeSearchResponse>(jsonString)
        assertEquals("recipe_id", recipeSearchResponse.recipes?.first()?.id)
        assertEquals("Title", recipeSearchResponse.recipes?.first()?.title)
        assertEquals("12345-12345-12345-12345", recipeSearchResponse.recipes?.first()?.lock)
    }
}
