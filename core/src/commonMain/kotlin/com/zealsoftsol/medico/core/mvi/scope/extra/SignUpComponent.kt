package com.zealsoftsol.medico.core.mvi.scope.extra

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.utils.Validator
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.LocationData
import com.zealsoftsol.medico.data.PincodeValidation
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserValidation3

interface AddressComponent : Scopable {
    val locationData: DataSource<LocationData?>
    val registration: DataSource<UserRegistration2>
    val pincodeValidation: DataSource<PincodeValidation?>

    fun onDataValid(isValid: Boolean)

    fun checkData() {
        val isValid = registration.value.run {
            pincode.length == 6 && addressLine1.isNotEmpty() && landmark.isNotEmpty() && location.isNotEmpty()
                    && city.isNotEmpty() && district.isNotEmpty() && state.isNotEmpty()
        }
        onDataValid(isValid)
    }

    /**
     * Updates [locationData] with 1s debounce
     */
    fun changePincode(pincode: String) {
        if (pincode.length <= 6) {
            trimInput(pincode, registration.value.pincode) {
                EventCollector.sendEvent(Event.Action.Registration.UpdatePincode(it))
            }
        }
    }

    fun changeAddressLine(address: String) {
        trimInput(address, registration.value.addressLine1) {
            registration.value = registration.value.copy(addressLine1 = it)
            checkData()
        }
    }

    fun changeLandmark(landmark: String) {
        if (landmark.length > 15) return
        trimInput(landmark, registration.value.landmark) {
            registration.value = registration.value.copy(landmark = it)
            checkData()
        }
    }

    fun changeLocation(location: String) {
        trimInput(location, registration.value.location) {
            registration.value = registration.value.copy(location = it)
            checkData()
        }
    }

    fun changeCity(city: String) {
        trimInput(city, registration.value.city) {
            registration.value = registration.value.copy(city = it)
            checkData()
        }
    }
}

interface TraderDetailsComponent : Scopable {
    val registration: DataSource<UserRegistration3>
    val validation: DataSource<UserValidation3?>

    fun onDataValid(isValid: Boolean)

    fun checkData() {
        val isValid = registration.value.run {
            tradeName.isNotEmpty() &&
                    (Validator.TraderDetails.isGstinValid(gstin) || Validator.TraderDetails.isPanValid(
                        panNumber
                    ))
                    && drugLicenseNo1.isNotEmpty() && drugLicenseNo2.isNotEmpty()
        }
        onDataValid(isValid)
    }

    fun changeTradeName(tradeName: String) {
        trimInput(tradeName, registration.value.tradeName) {
            registration.value = registration.value.copy(tradeName = it)
            checkData()
        }
    }

    fun changeGstin(gstin: String) {
        if (gstin.length <= 15) {
            trimInput(gstin, registration.value.gstin) {
                registration.value = registration.value.copy(gstin = it)
                checkData()
            }
        }
    }

    fun changePan(panNumber: String) {
        if (panNumber.length <= 10) {
            trimInput(panNumber, registration.value.panNumber) {
                registration.value = registration.value.copy(panNumber = it)
                checkData()
            }
        }
    }

    fun changeDrugLicense1(drugLicenseNo: String) {
        if (drugLicenseNo.length <= 30) {
            trimInput(drugLicenseNo, registration.value.drugLicenseNo1) {
                registration.value = registration.value.copy(drugLicenseNo1 = it)
                checkData()
            }
        }
    }

    fun changeDrugLicense2(drugLicenseNo: String) {
        if (drugLicenseNo.length <= 30) {
            trimInput(drugLicenseNo, registration.value.drugLicenseNo2) {
                registration.value = registration.value.copy(drugLicenseNo2 = it)
                checkData()
            }
        }
    }
}