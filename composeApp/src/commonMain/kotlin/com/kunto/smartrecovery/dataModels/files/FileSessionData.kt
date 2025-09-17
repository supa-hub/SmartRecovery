package com.kunto.smartrecovery.dataModels.files

/**
 * A data class Used to represent the data in one of the .csv files. The data is given row wise.
 */
data class FileData(val fileName: String, val data: List<List<String>>)

/**
 * A data class used to represent all the sessions data, which might be stored in multiple .csv files.
 */
data class FileSessionData(
    val sessionName: String,
    val sessionCreationDate: String,
    val fileData: List<FileData>
)
