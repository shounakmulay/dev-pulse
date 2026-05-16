package dev.shounakmulay.devpulse.core.data.db.transaction

import androidx.room3.TransactionScope

class RoomTransactionScopeWrapper<T> private constructor(
    val scope: TransactionScope<T>
) : DevPulseDatabaseTransactionScope<T> {

    companion object {
        fun <T> fromRoomScope(scope: TransactionScope<T>): DevPulseDatabaseTransactionScope<T> {
            return RoomTransactionScopeWrapper(scope)
        }
    }

    override suspend fun rollback(result: T) {
        scope.rollback(result)
    }

}