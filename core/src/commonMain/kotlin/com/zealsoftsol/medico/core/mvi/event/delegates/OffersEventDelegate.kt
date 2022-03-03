package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.OfferProductRequest
import com.zealsoftsol.medico.data.PromotionUpdateRequest
import com.zealsoftsol.medico.data.Promotions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class OffersEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkOffersScope: NetworkScope.OffersStore,
) : EventDelegate<Event.Action.Offers>(navigator), CommonScope.CanGoBack {
    private var searchJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Offers) = when (event) {
        is Event.Action.Offers.ShowBottomSheet -> showBottomSheet(
            event.promotionType,
            event.name,
            event.active
        )
        is Event.Action.Offers.ShowEditBottomSheet -> showEditBottomSheet(event.promotion)
        is Event.Action.Offers.GetOffers -> getOffers(event.search, event.query)
        is Event.Action.Offers.UpdateOffer -> updateOffer(event.promotionType, event.active)
        is Event.Action.Offers.LoadMoreProducts -> loadMoreProducts()
        is Event.Action.Offers.OpenCreateOffer -> loadMoreProducts()
        is Event.Action.Offers.EditOffer -> editOffer(event.promoCode, event.request)

        is Event.Action.Offers.GetTypes -> getPromotionTypes()
        is Event.Action.Offers.SearchAutoComplete -> searchAutoComplete(event.value)
        is Event.Action.Offers.SelectAutoComplete -> selectAutocomplete(event.autoComplete)
        is Event.Action.Offers.SaveOffer -> saveOffer(event.request)
        is Event.Action.Offers.EditCreatedOffer -> saveOffer(event.request)
    }

    private suspend fun getOffers(search: String?, query: ArrayList<String>) {
        navigator.withScope<OffersScope.ViewOffers> {
            it.pagination.reset()
            it.productSearch.value = search ?: ""
            if (!query.isNullOrEmpty()) it.manufacturerSearch.value = query
            val isWildcardSearch = search.isNullOrBlank()
            it.search(
                addPage = false,
                withDelay = false,
                withProgress = isWildcardSearch,
                extraFilters = search,
                manufacturers = query
            )
        }
    }

    private suspend fun loadMoreProducts() {
        navigator.withScope<OffersScope.ViewOffers> {
            if (!navigator.scope.value.isInProgress.value && it.pagination.canLoadMore()) {
                setHostProgress(true)
                it.search(
                    addPage = true,
                    withDelay = false,
                    withProgress = true,
                    "",
                    ArrayList()
                )
            }
        }
    }


    private suspend fun updateOffer(promotionType: String, active: Boolean) {
        val user = userRepo.requireUser()
        networkOffersScope.updateOffer(
            unitCode = user.unitCode,
            PromotionUpdateRequest(promoCode = promotionType, active = active)
        ).onSuccess {
            navigator.withScope<OffersScope.ViewOffers> {
                getOffers(
                    it.productSearch.value,
                    it.manufacturerSearch.value
                )
            }
        }.onError(navigator)
    }


    private fun showBottomSheet(promotionType: String, name: String, active: Boolean) {
        navigator.withScope<OffersScope> {
            val hostScope = scope.value
            hostScope.bottomSheet.value = BottomSheet.UpdateOfferStatus(
                promotionType, name, active
            )
        }

    }

    private suspend fun showEditBottomSheet(promotion: Promotions) {
        val user = userRepo.requireUser()
        networkOffersScope.getPromotionTypes(
            unitCode = user.unitCode,
        ).onSuccess { body ->
            navigator.withScope<OffersScope> {
                val hostScope = scope.value
                hostScope.bottomSheet.value = BottomSheet.UpdateOffer(
                    it, promotion, body.promotionTypes
                )
            }
        }.onError(navigator)

    }

    private suspend inline fun OffersScope.ViewOffers.search(
        addPage: Boolean,
        withDelay: Boolean,
        withProgress: Boolean,
        extraFilters: String? = "",
        manufacturers: ArrayList<String>?,
        crossinline onEnd: () -> Unit = {}
    ) {
        searchAsync(withDelay = withDelay, withProgress = withProgress) {
            val user = userRepo.requireUser()
            networkOffersScope.getOffersData(
                unitCode = user.unitCode,
                search = extraFilters,
                manufacturer = manufacturers,
                pagination = pagination,
            ).onSuccess { body ->
                pagination.setTotal(body.totalResults)
                statuses.value = body.promotionStatusDatas
                manufacturer.value = body.manufacturers
                items.value = if (!addPage) body.promotions else items.value + body.promotions
            }.onError(navigator)
            onEnd()
        }
    }

    private suspend fun searchAsync(
        withDelay: Boolean,
        withProgress: Boolean,
        search: suspend () -> Unit
    ) {
        searchJob?.cancel()
        searchJob = coroutineContext.toScope().launch {
            if (withDelay) delay(500)
            if (withProgress) navigator.setHostProgress(true)
            search()
            if (withProgress) navigator.setHostProgress(false)
        }
    }


    //Create offer methods
    private suspend fun getPromotionTypes() {
        val user = userRepo.requireUser()
        networkOffersScope.getPromotionTypes(
            unitCode = user.unitCode,
        ).onSuccess { body ->
            navigator.withScope<OffersScope.CreateOffer> {
                it.promoTypes.value = body.promotionTypes
                if (!it.promoTypes.value.isNullOrEmpty()) {
                    it.promoType.value = it.promoTypes.value[0].code
                    it.activeTab.value = it.promoTypes.value[0].name
                    it.activeType.value = 0
                }
            }
        }.onError(navigator)
    }

    private suspend fun searchAutoComplete(value: String) {
        val user = userRepo.requireUser()
        navigator.withScope<OffersScope.CreateOffer> {
            it.productSearch.value = value
            searchAsync(withDelay = true, withProgress = false) {
                networkOffersScope.autocompleteOffers(value, user.unitCode)
                    .onSuccess { body ->
                        it.autoComplete.value = body
                    }.onError(navigator)
            }
        }
    }

    private suspend fun selectAutocomplete(autoComplete: AutoComplete) {
        navigator.withScope<OffersScope.CreateOffer> {
            withProgress {
                it.productSearch.value = ""
                it.autoComplete.value = emptyList()
                val user = userRepo.requireUser()
                networkOffersScope.getAutocompleteItem(
                    unitCode = user.unitCode, input = autoComplete.suggestion
                ).onSuccess { body ->
                    it.selectedProduct.value = body
                    it.selectedProduct.value = body
                }
            }.onError(navigator)
        }
    }

    private suspend fun saveOffer(request: OfferProductRequest) {
        navigator.withScope<OffersScope.CreateOffer> {
            withProgress {
                it.productSearch.value = ""
                it.autoComplete.value = emptyList()
                val user = userRepo.requireUser()
                networkOffersScope.saveOffer(
                    unitCode = user.unitCode, request = request
                ).onSuccess { body ->
                    it.dialogMessage.value = body
                    it.showAlert.value = true
                }
            }.onError(navigator)
        }
    }

    private suspend fun editOffer(promoCode: String, request: OfferProductRequest) {
        val user = userRepo.requireUser()
        navigator.withScope<OffersScope.ViewOffers> {
            withProgress {
                networkOffersScope.editOffer(
                    unitCode = user.unitCode, promoCode, request
                ).onSuccess {
                    navigator.withScope<OffersScope.ViewOffers> {
                        getOffers(
                            it.productSearch.value,
                            it.manufacturerSearch.value
                        )
                    }
                }.onError(navigator)
            }
        }
    }

    private suspend fun editCreateOffer(promoCode: String, request: OfferProductRequest) {
        val user = userRepo.requireUser()
        networkOffersScope.editOffer(
            unitCode = user.unitCode, promoCode, request
        ).onSuccess {
            navigator.withScope<OffersScope.CreateOffer> {
                getOffers(
                    it.productSearch.value,
                    arrayListOf("")
                )
            }
        }.onError(navigator)
    }
}