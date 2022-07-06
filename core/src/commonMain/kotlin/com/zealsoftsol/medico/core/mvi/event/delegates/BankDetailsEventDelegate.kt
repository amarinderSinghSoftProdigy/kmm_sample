package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.nested.BankDetailsScope
import com.zealsoftsol.medico.core.mvi.scope.regular.BannersScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.BankDetails
import com.zealsoftsol.medico.data.UpiDetails

internal class BankDetailsEventDelegate (
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val bankRepo: NetworkScope.BankDetailsStore
) : EventDelegate<Event.Action.BankData>(navigator) {

    override suspend fun handleEvent(event: Event.Action.BankData) = when(event){
        is Event.Action.BankData.SubmitAccountData -> submitBankAccountDetails(event.accountData)
        is Event.Action.BankData.SubmitUpiData -> submitUpiDetails(event.name, event.upiAddress)
        Event.Action.BankData.GetAccountData -> getBankAccountDetails()
        Event.Action.BankData.GetUpiData -> getUpiDetails()
    }

    private suspend fun getUpiDetails() {
        navigator.withScope<BankDetailsScope.UpiDetails> {
            val result = withProgress {
                bankRepo.getUpiDetails()
            }

            result.onSuccess { body ->
                it.canEditData.value = !body.isAccountCreated
                it.name.value = body.name
                it.upiAddress.value = body.vpa
            }.onError(navigator)
        }
    }

    private suspend fun getBankAccountDetails() {
        navigator.withScope<BankDetailsScope.AccountDetails> {
            val result = withProgress {
                bankRepo.getBankDetails()
            }

            result.onSuccess { body ->
                it.canEditData.value = !body.isAccountCreated
                it.bankDetails.value = body
            }.onError(navigator)
        }
    }

    private suspend fun submitUpiDetails(name: String, upiAddress: String) {
        navigator.withScope<BankDetailsScope.UpiDetails> {
            val result = withProgress {
                bankRepo.addUpiDetails(name, upiAddress)
            }

            result.onSuccess { _ ->
                it.canEditData.value = false
                it.showToast.value = true
            }.onError(navigator)
        }
    }

    private suspend fun submitBankAccountDetails(accountData: BankDetails) {
        navigator.withScope<BankDetailsScope.AccountDetails> {
            val result = withProgress {
                bankRepo.addBankDetails(accountData)
            }

            result.onSuccess { _ ->
                it.canEditData.value = false
                it.showToast.value = true
            }.onError(navigator)
        }
    }

}