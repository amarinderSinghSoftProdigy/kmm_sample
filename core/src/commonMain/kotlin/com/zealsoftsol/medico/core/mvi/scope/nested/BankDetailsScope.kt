package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.core.utils.Validator.isIfscCodeValid
import com.zealsoftsol.medico.core.utils.Validator.isValidBankAccountNumber
import com.zealsoftsol.medico.core.utils.Validator.isValidPhone
import com.zealsoftsol.medico.core.utils.Validator.isValidUpi
import com.zealsoftsol.medico.data.BankDetails

open class BankDetailsScope(private val titleId: String) : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.OnlyBackHeader(titleId)

    class AccountDetails : BankDetailsScope("bank_details") {

        val bankDetails = DataSource(BankDetails("", "", "", ""))
        val reEnterAccountNumber = DataSource("")
        val canSubmitDetails = DataSource(false)
        val accountNumberErrorText = DataSource(true)
        val ifscErrorText = DataSource(true)
        val mobileErrorText = DataSource(true)
        val reenterAccountNumberErrorText = DataSource(true)
        val canEditData = DataSource(true)
        val showToast= DataSource(false)

        init {
            EventCollector.sendEvent(Event.Action.BankData.GetAccountData)
        }

        fun hideToast(){
            showToast.value = false
        }

        fun updateName(name: String) {
            bankDetails.value = bankDetails.value.copy(name = name)
            canSubmitData()
        }

        fun updateAccountNumber(accNumber: String) {
            bankDetails.value = bankDetails.value.copy(accountNumber = accNumber)
            accountNumberErrorText.value = isValidBankAccountNumber(accNumber)
            canSubmitData()
        }

        fun updateIfscCode(ifscCode: String) {
            bankDetails.value = bankDetails.value.copy(ifscCode = ifscCode)
            ifscErrorText.value = isIfscCodeValid(ifscCode)
            canSubmitData()
        }

        fun updateMobile(mobNumber: String) {
            bankDetails.value = bankDetails.value.copy(mobileNumber = mobNumber)
            mobileErrorText.value = isValidPhone(mobNumber)
            canSubmitData()
        }

        fun updateReEnterAccountNumber(accNumber: String) {
            reEnterAccountNumber.value = accNumber
            reenterAccountNumberErrorText.value = accNumber == bankDetails.value.accountNumber
            canSubmitData()
        }

        private fun canSubmitData() {
            val details = bankDetails.value
            canSubmitDetails.value =
                details.name.isNotEmpty() && isValidBankAccountNumber(details.accountNumber)
                        && details.accountNumber == reEnterAccountNumber.value && isIfscCodeValid(
                    details.ifscCode
                )
                        && isValidPhone(details.mobileNumber)
        }

        fun submitAccountDetails() =
            EventCollector.sendEvent(Event.Action.BankData.SubmitAccountData(bankDetails.value))
    }

    class UpiDetails : BankDetailsScope("upi_account") {

        val name = DataSource("")
        val upiAddress = DataSource("")
        val canSubmitDetails = DataSource(false)
        val upiErrorText = DataSource(true)
        val canEditData = DataSource(true)
        val showToast= DataSource(false)

        init {
            EventCollector.sendEvent(Event.Action.BankData.GetUpiData)
        }

        fun hideToast(){
            showToast.value = false
        }

        fun updateName(name: String) {
            this.name.value = name
            canSubmitData()
        }

        fun updateUpiAddress(upiAddress: String) {
            this.upiAddress.value = upiAddress
            upiErrorText.value = isValidUpi(upiAddress)
            canSubmitData()
        }

        private fun canSubmitData() {
            canSubmitDetails.value = name.value.isNotEmpty() && isValidUpi(upiAddress.value)
        }

        fun submitUpiDetails() = EventCollector.sendEvent(
            Event.Action.BankData.SubmitUpiData(
                name.value,
                upiAddress.value
            )
        )
    }
}