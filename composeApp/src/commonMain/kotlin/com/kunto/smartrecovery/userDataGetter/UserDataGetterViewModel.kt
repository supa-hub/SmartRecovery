package com.kunto.smartrecovery.userDataGetter

import androidx.lifecycle.ViewModel
import com.kunto.smartrecovery.backend.filehandling.FileHandler
import com.kunto.smartrecovery.json.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class UserDataGetterViewModel : ViewModel()
{
    private val fileHandler = FileHandler()
    private val _profile = MutableStateFlow<UserProfile>(fileHandler.getUserProfile() ?: UserProfile())
    val profile = _profile.asStateFlow()


    fun updateProfile(
        userName: String? = null,
        weight: Int? = null,
        height: Int? = null,
        typeOfInjury: String? = null,
        locationOfInjury: String? = null,
        dayOfInjury: String? = null,
        dayOfCasting: String? = null,
        amountOfForcePercentage: Int? = null,
        maximumForceOnInjury: Double? = null
    ) {
        _profile.update {
            it.copy(
                userName = userName ?: it.userName,
                weight = weight ?: it.weight,
                height = height ?: it.height,
                typeOfInjury = typeOfInjury ?: it.typeOfInjury,
                locationOfInjury = locationOfInjury ?: it.locationOfInjury,
                dayOfInjury = dayOfInjury ?: it.dayOfInjury,
                dayOfCasting = dayOfCasting ?: it.dayOfCasting,
                amountOfForcePercentage = amountOfForcePercentage ?: it.amountOfForcePercentage,
                maximumForceOnInjury = maximumForceOnInjury ?: it.maximumForceOnInjury
            )
        }
    }


    fun saveProfile(): Unit = fileHandler.saveUserProfile(profile.value)
}