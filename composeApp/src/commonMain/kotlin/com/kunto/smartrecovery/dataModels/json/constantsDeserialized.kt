package com.kunto.smartrecovery.json

import kotlinx.serialization.Serializable

@Serializable
data class InjuryLocations(
    val locations: Map<String, Int>
)
