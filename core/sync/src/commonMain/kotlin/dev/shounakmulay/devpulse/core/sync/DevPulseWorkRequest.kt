package dev.shounakmulay.devpulse.core.sync

import kotlin.time.Duration

sealed class DevPulseWorkRequest(
    open val identifier: DevPulseWorkerType,
    open val inputData: Map<String, Any?>? = null,
    open val constraints: DevPulseWorkRequestConstraints,
    open val expeditedType: DevPulseWorkRequestExpeditedType
) {

    data class OneTimeWorkRequest(
        override val identifier: DevPulseWorkerType,
        override val inputData: Map<String, Any?>? = null,
        override val constraints: DevPulseWorkRequestConstraints = DevPulseWorkRequestConstraints(),
        val uniqueIdentifier: DevPulseUniqueWorkRequestMetadata? = null,
        override val expeditedType: DevPulseWorkRequestExpeditedType = DevPulseWorkRequestExpeditedType.NONE
    ) : DevPulseWorkRequest(
        identifier = identifier,
        inputData = inputData,
        constraints = constraints,
        expeditedType = expeditedType
    )

    data class PeriodicWorkRequest(
        override val identifier: DevPulseWorkerType,
        override val inputData: Map<String, Any?>? = null,
        override val constraints: DevPulseWorkRequestConstraints = DevPulseWorkRequestConstraints(),
        val existingPeriodicWorkPolicy: DevPulsePeriodicWorkerExitingWorkPolicy,
        val interval: Duration,
        override val expeditedType: DevPulseWorkRequestExpeditedType = DevPulseWorkRequestExpeditedType.NONE
    ) : DevPulseWorkRequest(
        identifier = identifier,
        inputData = inputData,
        constraints = constraints,
        expeditedType = expeditedType
    )
}
