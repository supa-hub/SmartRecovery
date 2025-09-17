package com.kunto.smartrecovery

import kotlinx.serialization.Serializable


enum class Route
{
    Main,
    CurrentSession,
    UserProfileGetter,
    AllSavedSessions,
    NewSessionName,
    ChooseBLEDevicesPopUp,
    RequestPermissions
}

@Serializable
object Main
@Serializable
object CurrentSession
@Serializable
object UserProfileGetter
@Serializable
object AllSavedSessions
@Serializable
object NewSessionName
@Serializable
data class ChooseBLEDevicesPopUp(val sessionName: String = "")
@Serializable
data class RequestPermissions(val destination: Int)