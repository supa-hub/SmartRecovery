package com.kunto.smartrecovery.json

import kotlinx.serialization.Serializable


sealed interface BackendResponses

@Serializable
data class SuccessfulResponse(val res: String) : BackendResponses

@Serializable
data class ErrorResponse(val err: String) : BackendResponses
