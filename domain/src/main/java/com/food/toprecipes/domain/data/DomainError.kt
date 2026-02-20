package com.food.toprecipes.domain.data

sealed class DomainError() {

    data class TimeoutError(
        val errorCode: String? = null,
        val errorMessage: String
    ) : DomainError()

    data class UnknownIssue(
        val errorCode: String? = null,
        val errorMessage: String,
    ) : DomainError()

    data class ServiceUnavailable(
        val errorCode: String? = null,
        val errorMessage: String,
    ) : DomainError()

    data class ConflictWithTargetResourceError(
        val errorCode: String? = null,
        val errorMessage: String,
    ) : DomainError()
}


