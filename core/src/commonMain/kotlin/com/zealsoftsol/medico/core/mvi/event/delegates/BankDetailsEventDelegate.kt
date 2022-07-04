package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.AddBankDetails

internal class BankDetailsEventDelegate (
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val bannersRepo: NetworkScope.BankDetailsStore
) : EventDelegate<Event.Action.BankDetails>(navigator) {

    override suspend fun handleEvent(event: Event.Action.BankDetails) = when(event){
        is Event.Action.BankDetails.SubmitAccountDetails -> submitBankAccountDetails(event.accountData)
        is Event.Action.BankDetails.SubmitUpiDetails -> submitUpiDetails(event.name, event.upiAddress)
    }

    private fun submitUpiDetails(name: String, upiAddress: String) {
        TODO("Not yet implemented")
    }

    private fun submitBankAccountDetails(accountData: AddBankDetails) {
        TODO("Not yet implemented")
    }

}