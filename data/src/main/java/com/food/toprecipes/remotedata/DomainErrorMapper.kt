package com.food.toprecipes.remotedata

import android.util.Log
import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.R
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
                Log.d("DomainErrorMapper", throwable.message.toString())
                val message = throwable.message()
                when (code) {
                    503 -> DomainError.ServiceUnavailable(
                        code.toString(), stringProvider.getString(
                            R.string.error_occurred
                        )
                    )

                    409 -> DomainError.ConflictWithTargetResourceError(
                        code.toString(), stringProvider.getString(
                            R.string.error_occurred
                        )
                    )

                    else -> DomainError.UnknownIssue(
                        code.toString(), stringProvider.getString(
                            R.string.unknown_error
                        )
                    )
                }
            }

            is IOException -> DomainError.TimeoutError(
                errorMessage = stringProvider.getString(
                    R.string.check_internet_connection
                )
            )

            else -> DomainError.UnknownIssue(
                errorMessage = stringProvider.getString(
                    R.string.unknown_error
                )
            )
        }
    }

}