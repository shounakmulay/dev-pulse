package dev.shounakmulay.devpulse.core.data.db.transaction

interface DevPulseDatabaseTransactionAccessor {
    suspend fun <T> readTransaction(block: suspend DevPulseDatabaseTransactionScope<T>.() -> T): T

    suspend fun <T> writeTransaction(block: suspend DevPulseDatabaseTransactionScope<T>.() -> T): T
}