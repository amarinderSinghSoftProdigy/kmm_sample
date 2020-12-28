package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.utils.AadhaarVerification
import com.zealsoftsol.medico.data.AadhaarData

interface CommonScope {
    interface PhoneVerificationEntryPoint : CommonScope
    interface UploadDocument : CommonScope, WithErrors
    interface AadhaarDataHolder : CommonScope, WithErrors {
        val aadhaarData: DataSource<AadhaarData>
        val isVerified: DataSource<Boolean>

        fun changeCard(card: String) {
            if (card.length <= 12) {
                aadhaarData.value = aadhaarData.value.copy(cardNumber = card)
                verify()
            }
        }

        fun changeShareCode(shareCode: String) {
            if (shareCode.length <= 4) {
                aadhaarData.value = aadhaarData.value.copy(shareCode = shareCode)
                verify()
            }
        }

        private fun verify() {
            isVerified.value = aadhaarData.value.run {
                cardNumber.length == 12 && AadhaarVerification.isValid(cardNumber) && shareCode.length == 4
            }
        }
    }
}