package com.sameh.postnotificationwithworkmanager.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.sameh.postnotificationwithworkmanager.data.DemoApi
import javax.inject.Inject

class NotificationWorkerFactory @Inject constructor(private val api: DemoApi) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = NotificationWorker(appContext, workerParameters, api)
}