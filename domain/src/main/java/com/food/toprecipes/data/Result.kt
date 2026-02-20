package com.food.toprecipes.data

sealed class DomainResult<out L, out R> {
    data class Error<L>(
        val value: L,
    ) : DomainResult<L, Nothing>()

    data class Success<R>(
        val value: R,
    ) : DomainResult<Nothing, R>()
}

fun <R> R.success() = DomainResult.Success(this)

fun <L> L.error() = DomainResult.Error(this)


inline fun <R> attempt(f: () -> R): DomainResult<Throwable, R> = try {
    f().success()
} catch (error: Throwable) {
    error.error()
}

fun <L, R, R1> DomainResult<L, R>.map(f: (R) -> R1): DomainResult<L, R1> =
    when (this) {
        is DomainResult.Success -> f(value).success()
        is DomainResult.Error -> this
    }

fun <L, R, L1> DomainResult<L, R>.mapError(f: (L) -> L1): DomainResult<L1, R> =
    when (this) {
        is DomainResult.Success -> value.success()
        is DomainResult.Error -> f(value).error()
    }
