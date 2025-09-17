package com.kunto.smartrecovery.oldSessions

import androidx.lifecycle.ViewModel
import com.juul.kable.PlatformAdvertisement
import com.kunto.smartrecovery.backend.filehandling.FileHandler
import com.kunto.smartrecovery.dataModels.files.FileSessionData


class AllSessionsViewModel : ViewModel()
{
    val fileHandler = FileHandler()
    val chosenSessionNames = mutableListOf<String>()

    fun allSessionNames(): List<String> = fileHandler.allFileNamesWithString("r_combined_force")

    fun chooseSession(name: String)
    {
        chosenSessionNames += name
    }

    fun removeSession(name: String)
    {
        chosenSessionNames -= name
    }

    fun clearChosen()
    {
        chosenSessionNames.clear()
    }

    fun deleteChosen()
    {
        chosenSessionNames.forEach { fileHandler.deleteFile(it) }
    }
}