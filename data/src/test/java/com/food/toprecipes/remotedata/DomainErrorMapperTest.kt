package com.food.toprecipes.remotedata

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.spoonacularapi.StringProvider
import io.mockk.every
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class DomainErrorMapperTest {

    private lateinit var stringProvider: StringProvider
    private lateinit var mapper: DomainErrorMapper

    @Before
    fun setup() {
        stringProvider = mockk()
        every { stringProvider.getString(any()) } returns "mapped message"
        mapper = DomainErrorMapper(stringProvider)
    }

    @Test
    fun `should return ServiceUnavailable when HttpException has code 503`() {
        val response = Response.error<Any>(
            503,
            "".toResponseBody("application/json".toMediaTypeOrNull())
        )
        val exception = HttpException(response)

        val result = mapper.mapError(exception)

        assertEquals(DomainError.ServiceUnavailable::class, result::class)
        assertEquals("503", (result as DomainError.ServiceUnavailable).errorCode)
        assertEquals("mapped message", result.errorMessage)
    }

    @Test
    fun `should return ConflictWithTargetResourceError when HttpException has code 409`() {
        val response = Response.error<Any>(
            409,
            "".toResponseBody("application/json".toMediaTypeOrNull())
        )
        val exception = HttpException(response)

        val result = mapper.mapError(exception)

        assertEquals(DomainError.ConflictWithTargetResourceError::class, result::class)
        assertEquals("409", (result as DomainError.ConflictWithTargetResourceError).errorCode)
        assertEquals("mapped message", result.errorMessage)
    }

    @Test
    fun `should return UnknownIssue when HttpException has other code`() {
        val response = Response.error<Any>(
            500,
            "".toResponseBody("application/json".toMediaTypeOrNull())
        )
        val exception = HttpException(response)

        val result = mapper.mapError(exception)

        assertEquals(DomainError.UnknownIssue::class, result::class)
        assertEquals("500", (result as DomainError.UnknownIssue).errorCode)
        assertEquals("mapped message", result.errorMessage)
    }

    @Test
    fun `should return TimeoutError when throwable is IOException`() {
        val result = mapper.mapError(IOException("network error"))

        assertEquals(DomainError.TimeoutError::class, result::class)
        assertEquals("mapped message", (result as DomainError.TimeoutError).errorMessage)
    }

    @Test
    fun `should return UnknownIssue when throwable is generic`() {
        val result = mapper.mapError(RuntimeException("unknown"))

        assertEquals(DomainError.UnknownIssue::class, result::class)
        assertEquals("mapped message", (result as DomainError.UnknownIssue).errorMessage)
    }
}
