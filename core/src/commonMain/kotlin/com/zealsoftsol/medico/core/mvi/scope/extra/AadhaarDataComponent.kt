package com.zealsoftsol.medico.core.mvi.scope.extra

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.utils.Validator
import com.zealsoftsol.medico.core.utils.trimInput
import com.zealsoftsol.medico.data.AadhaarData

interface AadhaarDataComponent {
    val aadhaarData: DataSource<AadhaarData>
    val isVerified: DataSource<Boolean>

    fun changeCard(card: String) {
        if (card.length <= Validator.Aadhaar.MAX_LENGTH) {
            trimInput(card, aadhaarData.value.cardNumber) {
                aadhaarData.value = aadhaarData.value.copy(cardNumber = it)
                verify()
            }
        }
    }

    fun changeShareCode(shareCode: String) {
        if (shareCode.length <= 4) {
            trimInput(shareCode, aadhaarData.value.shareCode) {
                aadhaarData.value = aadhaarData.value.copy(shareCode = it)
                verify()
            }
        }
    }

    private fun verify() {
        isVerified.value = aadhaarData.value.run {
            cardNumber.length == 12 && Validator.Aadhaar.isValid(cardNumber) && shareCode.length == 4
        }
    }
}