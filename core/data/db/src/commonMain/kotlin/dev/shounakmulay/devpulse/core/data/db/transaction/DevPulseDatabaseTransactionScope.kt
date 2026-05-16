package dev.shounakmulay.devpulse.core.data.db.transaction

interface DevPulseDatabaseTransactionScope<T> {
    suspend fun rollback(result: T)
}