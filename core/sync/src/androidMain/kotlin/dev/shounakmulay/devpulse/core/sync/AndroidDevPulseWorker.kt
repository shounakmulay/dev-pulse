package dev.shounakmulay.devpulse.core.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import org.koin.android.annotation.KoinWorker
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named

@KoinWorker
class AndroidDevPulseWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(
    appContext = context,
    params = workerParams
), KoinComponent {
    override suspend fun doWork(): Result {
        val workerTypeName = requireNotNull(inputData.getString(DevPulseWorkerType.WORK_DATA_KEY)) {
            "Worker type not specified"
        }
        val workerType = DevPulseWorkerType.fromString(
            value = workerTypeName
        )

        val worker = when (workerType) {
            DevPulseWorkerType.FEED_SYNC -> getKoin().get<DevPulseWorker>(named(workerType.name))
        }

        require(worker.identifier == workerType) {
            "Worker registered with type ${worker.identifier} but called with type $workerType"
        }

        val result = worker.doWork(inputData.keyValueMap)

        return result.fold(
            onSuccess = { output ->
                if (output != null) {
                    val outputData = Data.Builder()
                        .putAll(output)
                        .build()
                    Result.success(outputData)
                } else {
                    Result.success()
                }
            },
            onFailure = {
                Result.failure()
            }
        )
    }
}