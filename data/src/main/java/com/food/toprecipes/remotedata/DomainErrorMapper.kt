package com.food.toprecipes.remotedata

import com.food.toprecipes.domain.data.DomainError
import com.food.toprecipes.spoonacularapi.StringProvider
import java.io.IOException
import javax.inject.Inject

class DomainErrorMapper @Inject constructor(
    private var stringProvider: StringProvider
) {
    fun mapError(throwable: Throwable): DomainError {
        return when (throwable) {
            is retrofit2.HttpException -> {
                val code = throwable.code()
                val message = throwable.message()
                // Map specific HTTP codes to the sealed class
                when (code) {
                    503 -> DomainError.ServiceUnavailable(code.toString(), message)
                    409 -> DomainError.ConflictWithTargetResourceError(code.toString(), message)
                    else -> DomainError.UnknownIssue(code.toString(), message)
                }
            }

            is IOException -> DomainError.TimeoutError(errorMessage = "")
            else -> DomainError.UnknownIssue(errorMessage = throwable.localizedMessage ?: "")
        }
    }

}