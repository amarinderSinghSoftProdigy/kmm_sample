package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.ProductInfoScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.getUserDataSource
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.ErrorCode

internal class ProductEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkProductScope: NetworkScope.Product,
) : EventDelegate<Event.Action.Product>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Product) = when (event) {
        is Event.Action.Product.Select -> selectProduct(event.productCode)
    }

    private suspend fun selectProduct(productCode: String) {
        val (response, isSuccess) = navigator.withProgress {
            networkProductScope.getProductData(productCode)
        }
        if (isSuccess && response != null) {
            navigator.setScope(
                ProductInfoScope.get(
                    user = userRepo.requireUser(),
                    userDataSource = userRepo.getUserDataSource(),
                    product = response.productData,
                    alternativeBrands = emptyList(),
                )
            )
//            "alternate brands".warnIt()
//            response.alternateBrands.forEach {
//                it.logIt()
//            }
        } else {
            navigator.setHostError(ErrorCode())
        }
    }
}