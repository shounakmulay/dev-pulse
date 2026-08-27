package dev.shounakmulay.devpulse.core.sync

import dev.shounakmulay.devpulse.core.common.coroutines.ApplicationScope
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named
import platform.BackgroundTasks.BGProcessingTaskRequest
import platform.BackgroundTasks.BGTaskScheduler

@Single
actual class DevPulseBackgroundSyncScheduler(
    private val applicationScope: ApplicationScope
) : BackgroundSyncScheduler, KoinComponent {
    actual override suspend fun initialise(workRequests: List<DevPulseWorkRequest>) {
        DevPulseWorkerType.entries.forEach {
            BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(it.name, null) { task ->
                val worker = getKoin().get<DevPulseWorker>(named(requireNotNull(task?.identifier)))
                // TODO: get data from user defaults
                applicationScope.launch {
                    val result = worker.doWork(emptyMap())

                    task.setTaskCompletedWithSuccess(result.isSuccess)
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun enqueue(workRequest: DevPulseWorkRequest) {
        // TODO: Set data to user defaults
        BGTaskScheduler.sharedScheduler.submitTaskRequest(
            taskRequest = BGProcessingTaskRequest(workRequest.identifier.name),
            null
        )
    }
}