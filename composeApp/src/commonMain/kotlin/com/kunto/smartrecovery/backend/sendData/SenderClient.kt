package com.kunto.smartrecovery.backend.sendData

import com.kunto.smartrecovery.json.BackendResponses
import com.kunto.smartrecovery.json.ErrorResponse
import com.kunto.smartrecovery.json.SessionPayload
import com.kunto.smartrecovery.json.SuccessfulResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


sealed class Responses(val msg: String)
{
    class Success(msg: String) : Responses(msg)
    class Error(msg: String) : Responses(msg)
}

/**
 * The SenderClient object is used to send HTTP requests with the chosen data
 * to the server and return the incoming responses.
 */
object SenderClient {
    private val client = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
    }


    /**
     * Send the chosen session data
     */
    suspend fun sendData(payload: SessionPayload): Responses
    {
        try {
            val response: HttpResponse = client.post("https://smartrecovery-backend-cbhybaedcwfwfsb6.francecentral-01.azurewebsites.net/api/newSessionData") {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }

            when (response.status.value) {
                in 400..499 -> return Responses.Error("Could not send the data due to client error code: ${response.status.value}")
                in 500..599 -> return Responses.Error("Could not send the data due to server error code: ${response.status.value}")
            }


            val receivedData: String = response.body()
            val decoded: BackendResponses = try {
                 Json.decodeFromString<SuccessfulResponse>(receivedData)
            }
            catch (e: Exception) {
                Json.decodeFromString<ErrorResponse>(receivedData)
            }
            return when (decoded) {
                is SuccessfulResponse -> Responses.Success("The data was successfully sent")
                else -> Responses.Error("The data could not be sent due to an unspecified error")
            }
        }
        catch (e: Exception) {
            return Responses.Error("Could not send the data due to client error: ${e.message}")
        }
    }
}