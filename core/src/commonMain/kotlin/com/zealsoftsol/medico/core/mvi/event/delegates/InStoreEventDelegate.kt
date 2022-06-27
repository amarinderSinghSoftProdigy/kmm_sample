package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreAddUserScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreCartScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreOrderPlacedScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreProductsScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreSellerScope
import com.zealsoftsol.medico.core.mvi.scope.nested.InStoreUsersScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SearchScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.CartRepo
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.core.utils.LoadHelper
import com.zealsoftsol.medico.data.InStoreCart
import com.zealsoftsol.medico.data.InStoreCartRequest
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.InStoreSeller
import com.zealsoftsol.medico.data.InStoreUser
import com.zealsoftsol.medico.data.Value

internal class InStoreEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkInStoreScope: NetworkScope.InStore,
    private val loadHelper: LoadHelper,
    private val cartRepo: CartRepo,
) : EventDelegate<Event.Action.InStore>(navigator), CommonScope.CanGoBack {

    override suspend fun handleEvent(event: Event.Action.InStore) = when (event) {
        is Event.Action.InStore.SellerLoad -> loadSellerInStore(event.isFirstLoad)
        is Event.Action.InStore.SellerSearch -> searchSellerInStore(event.value)
        is Event.Action.InStore.SellerSelect -> selectSellerInStore(
            event.unitcode,
            event.sellerName,
            event.address,
            event.phoneNumber
        )
        is Event.Action.InStore.ProductLoad -> loadProductInStore(
            event.isFirstLoad,
            page = event.page,
            search = event.searchTerm,
            manufacturers = event.manufacturers
        )
        is Event.Action.InStore.ProductSearch -> searchProductInStore(event.value)
        is Event.Action.InStore.ProductSelect -> selectProductInStore(event.item)
        is Event.Action.InStore.UserLoad -> loadUserInStore(event.isFirstLoad)
        is Event.Action.InStore.UserSearch -> searchUserInStore(event.value)
        is Event.Action.InStore.AddUserUpdatePincode -> updatePincode(event.pincode)
        is Event.Action.InStore.AddUser -> addUser()
        is Event.Action.InStore.FinishAddUser -> finishAddUser()
        is Event.Action.InStore.LoadCart -> loadCart()
        is Event.Action.InStore.ClearCart -> clearCart()
        is Event.Action.InStore.ConfirmCartOrder -> confirmCartOrder()
        is Event.Action.InStore.AddCartItem -> event.run {
            addItem(
                productName,
                productCode,
                spid,
                quantity,
                freeQuantity,
                page,
                search
            )
        }
        is Event.Action.InStore.UpdateCartItem -> event.run {
            updateItem(
                productCode,
                spid,
                quantity,
                freeQuantity,
            )
        }
        is Event.Action.InStore.RemoveCartItem -> event.run {
            removeItem(entryId)
        }
        is Event.Action.InStore.DeleteOrder -> removeOrder(event.unitcode, event.id)
        is Event.Action.InStore.SubmitReward -> submitReward(event.storeId)
        is Event.Action.InStore.ShowAltProds -> showAlternativeProducts(
            event.productCode,
            event.sellerName
        )
        is Event.Action.InStore.ApplyManufacturersFilter -> updateSelectedManufacturersFilters(event.filters)
        is Event.Action.InStore.ShowManufacturers -> showFilterManufacturers(event.data)
    }

    /**
     * this will get the manufacturers selected by user to be applied as filter
     */
    private fun updateSelectedManufacturersFilters(filters: List<Value>) {
        navigator.withScope<InStoreProductsScope> {
            it.selectedFilters.value = filters
            it.loadItems(true)
        }
    }

    /**
     * show all the manufacturers to user that are available for filter
     * and send preselected filters if any
     */
    private fun showFilterManufacturers(data: List<Value>) {
        navigator.withScope<InStoreProductsScope> {
            val hostScope = scope.value
            hostScope.bottomSheet.value = BottomSheet.FilerManufacturers(
                data,
                it.selectedFilters.value,
                BottomSheet.FilerManufacturers.FilterScopes.IN_STORES_PRODUCTS
            )
        }
    }

    /**
     * show alternate products based on product code
     */
    private suspend fun showAlternativeProducts(productCode: String, sellerName: String?) {
        navigator.withScope<InStoreProductsScope> {
            withProgress { networkInStoreScope.getAlternateProducts(productCode) }
                .onSuccess { body ->
                    if (body.isNotEmpty())
                        this.scope.value.bottomSheet.value =
                            BottomSheet.AlternateProducts(body, sellerName)
                    else
                        it.showNoAlternateProdToast.value = true
                }.onError(navigator)
        }
    }

    private suspend fun submitReward(storeId: String) {
        navigator.withScope<InStoreOrderPlacedScope> {
            withProgress { cartRepo.submitReward(storeId) }
                .onSuccess { _ ->
                    it.isOfferSwiped.value = true
                }.onError(navigator)
        }
    }

    private suspend fun removeOrder(unitCode: String, id: String) {
        navigator.withScope<InStoreSellerScope> {
            withProgress {
                networkInStoreScope.deleteInStoreOrder(
                    unitCode = unitCode,
                    id = id
                ).onSuccess { body ->
                    it.loadItems(true)
                }.onError(navigator)
            }
        }
    }

    private suspend fun loadSellerInStore(isFirstLoad: Boolean) {
        loadHelper.load<InStoreSellerScope, InStoreSeller>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            networkInStoreScope.getInStoreSellers(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private suspend fun searchSellerInStore(search: String) {
        loadHelper.search<InStoreSellerScope, InStoreSeller>(searchValue = search) {
            val user = userRepo.requireUser()
            networkInStoreScope.getInStoreSellers(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private fun selectSellerInStore(
        unitcode: String,
        sellerName: String,
        address: String,
        phoneNumber: String
    ) {
        navigator.withScope<Scopable> {
            setScope(InStoreProductsScope(unitcode, sellerName, address, phoneNumber))
        }
    }

    private suspend fun loadProductInStore(
        isFirstLoad: Boolean, cartIfFirst: Boolean = true,
        page: Int, search: String, manufacturers: String
    ) {

        navigator.withScope<InStoreProductsScope> {
            val result = withProgress {
                networkInStoreScope.searchInStoreSeller(
                    unitCode = it.unitCode,
                    search = search,
                    page = page,
                    manufacturers = manufacturers
                )
            }

            result.onSuccess { _ ->
                val data = result.getBodyOrNull()
                it.autoComplete.value = emptyList()
                if (data?.data != null) {
                    it.items.value = data.data
                    it.totalItems.value = data.total
                    it.filtersManufactures.value =
                        data.facets?.find { s -> s.queryId == "manufacturers" }?.values
                            ?: emptyList()
                }
            }.onError(navigator)
        }

        if (isFirstLoad && cartIfFirst) loadCart()
    }

    private suspend fun searchProductInStore(search: String) {
        navigator.withScope<InStoreProductsScope> {
            val result =
                networkInStoreScope.searchInStoreSellerAutoComplete(
                    unitCode = it.unitCode,
                    search = search,
                )


            result.onSuccess { body ->
                it.autoComplete.value = body
                if (it.autoComplete.value.isEmpty()) {
                    it.showNoProducts.value = true
                }
                if (search.isNotEmpty() && body.isEmpty() && it.items.value.isNotEmpty()) {
                    it.items.value = emptyList()
                }
            }.onError(navigator)
        }
    }

    private fun selectProductInStore(item: InStoreProduct) {
        navigator.scope.value.bottomSheet.value = BottomSheet.InStoreViewProduct(item)
    }

    private suspend fun loadUserInStore(isFirstLoad: Boolean) {
        loadHelper.load<InStoreUsersScope, InStoreUser>(isFirstLoad = isFirstLoad) {
            val user = userRepo.requireUser()
            networkInStoreScope.getInStoreUsers(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private suspend fun searchUserInStore(search: String) {
        loadHelper.search<InStoreUsersScope, InStoreUser>(searchValue = search) {
            val user = userRepo.requireUser()
            networkInStoreScope.getInStoreUsers(
                unitCode = user.unitCode,
                search = searchText.value,
                pagination = pagination,
            ).getBodyOrNull()
        }
    }

    private suspend fun updatePincode(pincode: String) {
        navigator.withScope<InStoreAddUserScope> {
            it.registration.value = it.registration.value.copy(pincode = pincode)
            if (pincode.length == 6) {
                val result = withProgress { userRepo.getLocationData(pincode) }
//                it.pincodeValidation.value = result.validations
                result.onSuccess { body ->
                    it.locationData.value = body
                    it.registration.value = it.registration.value.copy(
                        pincode = pincode,
                        district = body.district,
                        state = body.state,
                    )
                }.onError(navigator)
            }
        }
    }

    private suspend fun addUser() {
        navigator.withScope<InStoreAddUserScope> {
            withProgress {
                networkInStoreScope.addUser(it.registration.value.copy(b2bUnitCode = userRepo.requireUser().unitCode))
            }.onSuccess { _ ->
                it.notifications.value = InStoreAddUserScope.UserAddedSuccessfully
            }.onError(navigator)
        }
    }

    private suspend fun finishAddUser() {
        navigator.withScope<InStoreAddUserScope> {
            dropScope()
        }
        loadUserInStore(isFirstLoad = true)
    }

    private var cartId = ""

    private suspend fun addItem(
        productName: String,
        productCode: String,
        spid: String,
        quantity: Double,
        freeQuantity: Double,
        page: Int,
        search: String,
    ) {
        navigator.withScope<Scopable> {
            withProgress {
                networkInStoreScope.addInStoreCartEntry(
                    InStoreCartRequest(
                        if (it is InStoreProductsScope) it.unitCode else "",
                        productCode,
                        spid,
                        quantity,
                        freeQuantity,
                    )
                )
            }.onSuccess { cart ->
                if (it is InStoreProductsScope) {
                    it.cart.value = cart
                    loadProductInStore(false, false, page, search,"")
                    it.setToast(
                        InStoreProductsScope.ToastItem(productName, quantity, freeQuantity),
                        true
                    )
                }
            }.onError(navigator)
        }
    }

    private suspend fun updateItem(
        productCode: String,
        spid: String,
        quantity: Double,
        freeQuantity: Double,
    ) {
        navigator.withScope<InStoreCartScope> {
            withProgress {
                networkInStoreScope.updateInStoreCartEntry(
                    InStoreCartRequest(
                        it.unitCode,
                        productCode,
                        spid,
                        quantity,
                        freeQuantity,
                    )
                )
            }.onSuccess { cart -> it.handleCart(cart) }
                .onError(navigator)
        }
    }

    private suspend fun removeItem(
        entryId: String,
        checkContains: Boolean = false,
    ) {
        navigator.withScope<InStoreCartScope> {
            if (!checkContains || it.items.value.any { it.id == entryId }) {
                withProgress {
                    networkInStoreScope.deleteInStoreCartEntry(
                        it.unitCode,
                        entryId,
                    ).onSuccess { cart ->
                        it.handleCart(cart)
                        if (it.items.value.isEmpty()) dropScope()
                    }.onError(navigator)
                }
            }
        }
    }

    private suspend fun clearCart() {
        navigator.withScope<InStoreCartScope> {
            withProgress {
                networkInStoreScope.deleteInStoreCart(it.unitCode, cartId)
            }.onSuccess { _ ->
                cartId = ""
                it.items.value = emptyList()
                it.total.value = null
                dropScope()
            }.onError(navigator)
        }
    }

    private suspend fun loadCart() {
        navigator.withScope<Scopable> {
            val unitCode = when (it) {
                is InStoreProductsScope -> it.unitCode
                is InStoreCartScope -> it.unitCode
                else -> ""
            }
            val response = networkInStoreScope.getInStoreCart(unitCode)
            when (it) {
                is InStoreCartScope -> response
                    .onSuccess { cart -> it.handleCart(cart) }
                    .onError(navigator)
                is InStoreProductsScope -> response
                    .onSuccess { cart -> it.cart.value = cart }
                    .onError(navigator)
            }
        }
    }

    private suspend fun confirmCartOrder() {
        navigator.withScope<InStoreCartScope> {
            withProgress {
                networkInStoreScope.confirmInStoreCart(it.unitCode, cartId)
            }.onSuccess { body ->
                navigator.dropScope(Navigator.DropStrategy.ToRoot, false)
                navigator.setScope(InStoreOrderPlacedScope(it.name, body))
            }.onError(navigator)
        }
    }

    private inline fun InStoreCartScope.handleCart(cart: InStoreCart) {
        cartId = cart.id.orEmpty()
        items.value = cart.entries
        total.value = cart.total
        paymentMethod.value = cart.paymentType
        showNoCart.value = cart.entries.isEmpty()
    }
}