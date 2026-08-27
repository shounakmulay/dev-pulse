package dev.shounakmulay.devpulse.core.sync

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.koin.core.annotation.Single
import kotlin.time.toJavaDuration

@Single
actual class DevPulseBackgroundSyncScheduler(
    private val workManager: WorkManager
) : BackgroundSyncScheduler {
    actual override suspend fun initialise(workRequests: List<DevPulseWorkRequest>) {
        workRequests.forEach {
            enqueue(it)
        }
    }

    actual override suspend fun enqueue(workRequest: DevPulseWorkRequest) {
        when (workRequest) {
            is DevPulseWorkRequest.OneTimeWorkRequest -> enqueueOneTimeWorkRequest(workRequest)
            is DevPulseWorkRequest.PeriodicWorkRequest -> enqueuePeriodicWorkRequest(workRequest)
        }
    }

    private fun enqueueOneTimeWorkRequest(workRequest: DevPulseWorkRequest.OneTimeWorkRequest) {
        val identifier = workRequest.identifier.name
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<AndroidDevPulseWorker>()
            .addTag(identifier)
            .setConstraints(getConstraints(workRequest))
            .setInputData(
                Data
                    .Builder()
                    .putString(DevPulseWorkerType.WORK_DATA_KEY, identifier)
                    .putAll(workRequest.inputData ?: emptyMap())
                    .build()
            )
            .apply {
                val outOfQuotaPolicy = getOutOfQuotaPolicy(workRequest)

                outOfQuotaPolicy?.let {
                    setExpedited(it)
                }
            }
            .build()

        if (workRequest.uniqueIdentifier != null) {
            workManager.enqueueUniqueWork(
                uniqueWorkName = workRequest.uniqueIdentifier.identifier,
                existingWorkPolicy = when (workRequest.uniqueIdentifier.existingWorkPolicy) {
                    DevPulseUniqueWorkerExitingWorkPolicy.REPLACE -> ExistingWorkPolicy.REPLACE
                    DevPulseUniqueWorkerExitingWorkPolicy.KEEP -> ExistingWorkPolicy.KEEP
                    DevPulseUniqueWorkerExitingWorkPolicy.APPEND -> ExistingWorkPolicy.APPEND
                    DevPulseUniqueWorkerExitingWorkPolicy.APPEND_OR_REPLACE -> ExistingWorkPolicy.APPEND_OR_REPLACE
                },
                request = oneTimeWorkRequest,
            )
        } else {
            workManager.enqueue(oneTimeWorkRequest)
        }
    }

    private fun enqueuePeriodicWorkRequest(workRequest: DevPulseWorkRequest.PeriodicWorkRequest) {
        val identifier = workRequest.identifier.name
        val periodicWorkRequest = PeriodicWorkRequestBuilder<AndroidDevPulseWorker>(
            repeatInterval = workRequest.interval.toJavaDuration()
        )
            .addTag(identifier)
            .setConstraints(getConstraints(workRequest))
            .apply {
                val outOfQuotaPolicy = getOutOfQuotaPolicy(workRequest)
                outOfQuotaPolicy?.let {
                    setExpedited(it)
                }
            }
            .setInputData(
                Data
                    .Builder()
                    .putString(DevPulseWorkerType.WORK_DATA_KEY, identifier)
                    .putAll(workRequest.inputData ?: emptyMap())
                    .build()
            )
            .build()


        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = identifier,
            existingPeriodicWorkPolicy = when (workRequest.existingPeriodicWorkPolicy) {
                DevPulsePeriodicWorkerExitingWorkPolicy.REPLACE -> ExistingPeriodicWorkPolicy.REPLACE
                DevPulsePeriodicWorkerExitingWorkPolicy.KEEP -> ExistingPeriodicWorkPolicy.KEEP
                DevPulsePeriodicWorkerExitingWorkPolicy.UPDATE -> ExistingPeriodicWorkPolicy.UPDATE
                DevPulsePeriodicWorkerExitingWorkPolicy.CANCEL_AND_REENQUEUE -> ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
            },
            request = periodicWorkRequest,
        )
    }

    private fun getOutOfQuotaPolicy(workRequest: DevPulseWorkRequest): OutOfQuotaPolicy? {
        val expeditedType = when (workRequest.expeditedType) {
            DevPulseWorkRequestExpeditedType.NONE -> null
            DevPulseWorkRequestExpeditedType.EXPEDITED_OR_NORMAL -> OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
            DevPulseWorkRequestExpeditedType.EXPEDITED -> OutOfQuotaPolicy.DROP_WORK_REQUEST
        }
        return expeditedType
    }

    private fun getConstraints(workRequest: DevPulseWorkRequest): Constraints {
        val dpConstraints = workRequest.constraints
        return Constraints
            .Builder()
            .setRequiresCharging(dpConstraints.requiresCharging)
            .setRequiredNetworkType(if (dpConstraints.requiresNetwork) NetworkType.CONNECTED else NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(dpConstraints.requiresBatteryNotLow)
            .setRequiresStorageNotLow(dpConstraints.requiresStorageNotLow)
            .setRequiresDeviceIdle(dpConstraints.requiresDeviceIdle)
            .build()
    }
}