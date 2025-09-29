package com.kunto.smartrecovery.oldSessions

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.juul.kable.PlatformAdvertisement
import com.kunto.smartrecovery.backend.filehandling.FileHandler
import com.kunto.smartrecovery.dataModels.files.FileSessionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class AllSessionsViewModel : ViewModel()
{
    val fileHandler = FileHandler()
    val chosenSessionNames = mutableListOf<String>()

    private val _allFiles = MutableStateFlow(allSessionNames())
    val allFiles = _allFiles.asStateFlow()

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
        chosenSessionNames.forEach {
            fileHandler.deleteAllFilesWith(it.replace("r_combined_force_", "").takeWhile { it != '.' })
        }
        chosenSessionNames.clear()
        _allFiles.update { allSessionNames() }
    }
}