package com.george200150.uni.pdmandro.todo

import android.content.Context
import androidx.work.*
import com.george200150.uni.pdmandro.todo.data.PlantRepoHelper
import com.george200150.uni.pdmandro.todo.data.local.DeleteHelper

class RepoWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        when {
            inputData.getString("operation") == "save" -> PlantRepoHelper.saveNewVersion()
            inputData.getString("operation") == "update" -> PlantRepoHelper.updateNewVersion()
            inputData.getString("operation") == "delete" -> deleteIfNeeded()
            else -> return Result.failure()
        }
        return Result.success()
    }

    private fun deleteIfNeeded() {
        val deleteValue = DeleteHelper.getDeleteAndClear()
        if (deleteValue != "") {
            deleteValue.split(",").forEach {
                PlantRepoHelper.setPlantToBeDeletedId(it)
                PlantRepoHelper.deletePlant()
            }
        }
    }
}