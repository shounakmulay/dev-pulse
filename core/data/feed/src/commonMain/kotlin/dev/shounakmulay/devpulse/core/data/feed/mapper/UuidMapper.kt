package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import org.koin.core.annotation.Factory

@Factory
class UuidMapper {

    fun fromUuid(uuid: UUID): LocalUUID {
        return LocalUUID(uuid.value)
    }

    fun toUuid(localUuid: LocalUUID): UUID {
        return UUID(localUuid.value)
    }
}