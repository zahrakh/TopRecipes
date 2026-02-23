package com.food.toprecipes.presentation.mapper

import androidx.annotation.StringRes
import com.food.toprecipes.data.DomainError
import com.food.toprecipes.presentation.R

/**
 * Single place to map [DomainError] to the string resource IDs.
 */
object DomainErrorToMessageMapper {

    @StringRes
    fun toMessageResId(error: DomainError): Int = when (error) {
        is DomainError.TimeoutError -> R.string.error_connection_timeout
        is DomainError.ServiceUnavailable -> R.string.error_service_unavailable
        is DomainError.ConflictWithTargetResourceError -> R.string.error_generic_load_failed
        is DomainError.UnknownIssue -> R.string.error_generic_load_failed
    }
}
