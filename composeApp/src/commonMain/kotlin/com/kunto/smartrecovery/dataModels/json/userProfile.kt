package com.kunto.smartrecovery.json

import kotlinx.serialization.Serializable


@Serializable
data class UserProfile(
    var userName: String = "",
    var weight: Int = 0,
    var height: Int = 0,
    var typeOfInjury: String = "",
    var locationOfInjury: String = "",
    var dayOfInjury: String = "",  // ISO 8601 date string
    var dayOfCasting: String = "",   // ISO 8601 date string
    var amountOfForcePercentage: Int = 0,  // the percentage of the weight that can be applied on the injury
    var maximumForceOnInjury: Double = 0.0  // the calculated maximum force in Newtons
)

