package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.toScope
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.onError
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.mvi.scope.nested.BaseSearchScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SearchScope
import com.zealsoftsol.medico.core.mvi.scope.nested.StoresScope
import com.zealsoftsol.medico.core.mvi.scope.nested.ToastScope
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.CartData
import com.zealsoftsol.medico.data.ConnectedStockist
import com.zealsoftsol.medico.data.Facet
import com.zealsoftsol.medico.data.Filter
import com.zealsoftsol.medico.data.Option
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SortOption
import com.zealsoftsol.medico.data.Value
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

internal class SearchEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
    private val networkSearchScope: NetworkScope.Search,
) : EventDelegate<Event.Action.Search>(navigator) {

    private var activeFilters = hashMapOf<String, Option.StringValue>()
    private var searchJob: Job? = null

    override suspend fun handleEvent(event: Event.Action.Search) = when (event) {
        is Event.Action.Search.SearchInput -> searchInput(
            event.isOneOf,
            event.search,
            event.query,
        )
        is Event.Action.Search.SearchAutoComplete -> searchAutoComplete(
            event.value,
            event.sellerUnitCode
        )
        is Event.Action.Search.SearchFilter -> searchFilter(event.filter, event.value)
        is Event.Action.Search.SelectAutoComplete -> selectAutocomplete(event.autoComplete)
        is Event.Action.Search.SelectFilter -> selectFilter(event.filter, event.option)
        is Event.Action.Search.ClearFilter -> clearFilter(event.filter)
        is Event.Action.Search.SelectSortOption -> selectSortOption(event.option)
        is Event.Action.Search.LoadMoreProducts -> loadMoreProducts()
        is Event.Action.Search.ToggleFilter -> toggleFilter()
        is Event.Action.Search.SelectBatch -> updateBatchSelection(event.option, event.product)
        is Event.Action.Search.ViewAllItems -> viewAllManufacturers()
        is Event.Action.Search.Reset -> reset()
        is Event.Action.Search.ResetButton -> resetButton(event.item)
        is Event.Action.Search.AddToCart -> updateBatchSelection(true, event.product)
        is Event.Action.Search.showToast -> showToast(event.msg, event.cartData)
        is Event.Action.Search.UpdateFree -> updateQty(event.qty, event.id)
        is Event.Action.Search.ShowConnectedStockistBottomSheet -> showConnectedStockist(event.stockist)
        is Event.Action.Search.SelectAutoCompleteGlobal -> selectAutoCompleteGlobal(event.autoComplete)
        is Event.Action.Search.LoadStockist -> loadStockist(event.code, event.imageCode)
        is Event.Action.Search.GetLocalSearchData -> getLocalSearchData()
        is Event.Action.Search.ShowAltProds -> showAlternativeProducts(
            event.productCode,
            event.sellerName
        )
        is Event.Action.Search.ShowManufacturers -> showFilterManufacturers(event.data)
        is Event.Action.Search.ApplyManufacturersFilter -> updateSelectedManufacturersFilters(event.filters)
    }

    /**
     * this will get the manufacturers selected by user to be applied as filter
     */
    private suspend fun updateSelectedManufacturersFilters(filters: List<Value>) {
        navigator.withScope<SearchScope> {
            it.selectedFilters.value = filters
            it.isFilterApplied.value = filters.isNotEmpty()
            val autoComplete = AutoComplete(
                query = "manufacturers",
                suggestion = filters.joinToString(",") { data -> data.id },
                stockists = "",
                details = filters.joinToString(",") { data -> data.value }
            )
            selectAutocomplete(autoComplete)
        }
    }

    /**
     * show all the manufacturers to user that are available for filter
     * and send preselected filters if any
     */
    private fun showFilterManufacturers(data: List<Value>) {
        navigator.withScope<SearchScope> {
            val hostScope = scope.value
            hostScope.bottomSheet.value = BottomSheet.FilerManufacturers(
                data,
                it.selectedFilters.value,
                BottomSheet.FilerManufacturers.FilterScopes.SEARCH
            )
        }
    }

    /**
     * show alternate products based on product code
     */
    private suspend fun showAlternativeProducts(productCode: String, sellerName: String?) {
        navigator.withScope<SearchScope> {
            withProgress { networkSearchScope.getAlternateProducts(productCode) }
                .onSuccess { body ->
                    if (body.isNotEmpty())
                        this.scope.value.bottomSheet.value =
                            BottomSheet.AlternateProducts(body, sellerName)
                    else
                        it.showNoAlternateProdToast.value = true
                }.onError(navigator)
        }
    }

    /**
     * get local search history of user
     */
    private fun getLocalSearchData() {
        navigator.withScope<SearchScope> {
            val prefilledList = listOf(
                AutoComplete(
                    query = "manufacturers",
                    details = "in Manufacturers",
                    suggestion = "ABBOTT INDIA LTD"
                ),
                AutoComplete(
                    query = "manufacturers",
                    details = "in Manufacturers",
                    suggestion = "ALEMBIC PHARMACEUTICALS LTD"
                ),
                AutoComplete(
                    query = "manufacturers",
                    details = "in Manufacturers",
                    suggestion = "CIPLA LTD"
                ),
                AutoComplete(
                    query = "manufacturers",
                    details = "in Manufacturers",
                    suggestion = "ZYDUS CADILA"
                ),
                AutoComplete(
                    query = "manufacturers",
                    details = "in Manufacturers",
                    suggestion = "FDC LTD"
                )
            )
            it.autoComplete.value =
                userRepo.getLocalSearchHistory().toMutableList().plus(prefilledList).toSet()
                    .toList()
        }
    }

    /**
     * Load all stockist on a product
     */
    private suspend fun loadStockist(code: String, imageCode: String) {
        val address = userRepo.requireUser()
        navigator.withScope<SearchScope> {
            withProgress {
                networkSearchScope.loadStockist(
                    address.latitude,
                    address.longitude,
                    imageCode,
                    code
                )
                    .onSuccess { body ->
                        if (body.isNotEmpty())
                            showConnectedStockist(body)
                        else
                            it.manageAlertVisibility(true)

                    }.onError(navigator)
            }
        }
    }

    /**
     *  load the searched stockists on a bottom sheet
     */
    private fun showConnectedStockist(stockist: List<ConnectedStockist>) {
        navigator.withScope<SearchScope> {
            val hostScope = scope.value
            hostScope.bottomSheet.value = BottomSheet.ShowConnectedStockist(stockist)
        }
    }


    /**
     * search autocomplete items directly on global search
     */
    private suspend fun selectAutoCompleteGlobal(autoComplete: AutoComplete) {
        reset()
        navigator.withScope<BaseSearchScope> {
            it.showNoProducts.value = false
            it.productSearch.value = autoComplete.suggestion
            it.pagination.reset()
            withProgress {
                networkSearchScope.autocomplete(autoComplete.suggestion, null)
                    .onSuccess { body ->
                        it.autoComplete.value = body
                        if (it.autoComplete.value.isEmpty()) {
                            it.showNoProducts.value = true
                        }
                        if (body.isEmpty() && it.products.value.isNotEmpty()) {
                            it.products.value = emptyList()
                        }
                    }.onError(navigator)
            }
        }
    }


    private fun showToast(msg: String, cartData: CartData?) {
        navigator.withScope<ToastScope> {
            it.showToast.value = msg == "success"
            it.cartData.value = cartData
        }
    }

    private fun updateQty(qty: Double, id: String) {
        navigator.withScope<StoresScope.StorePreview> {
            it.freeQty.value = qty
            it.productId.value = id
        }
    }

    private suspend fun searchInput(
        isOneOf: Boolean,
        search: String?,
        query: Map<String, String>,
    ) {
        if (isOneOf)
            reset()
        navigator.withScope<BaseSearchScope> {
            if (search != null) it.pagination.reset()
            if (search != null) it.productSearch.value = search
            it.autoComplete.value = emptyList()
            val isWildcardSearch = search == null && query.isEmpty()
            withProgress {
                it.search(
                    it.pagination,
                    addPage = isOneOf,
                    withDelay = false,
                    withProgress = true,//if (it.supportsAutoComplete) !isWildcardSearch else false,
                    extraFilters = query.mapValues { (_, value) ->
                        Option.StringValue(
                            value,
                            false
                        )
                    },

                    )
            }
        }
    }

    private fun searchFilter(filter: Filter, value: String) {
        navigator.withScope<BaseSearchScope> {
            it.pagination.reset()
            val oldSearches = it.filterSearches.value.toMutableMap()
            oldSearches[filter.queryId] = value
            it.filterSearches.value = oldSearches

            it.filters.value = it.filters.value.map { f ->
                if (f.queryId == filter.queryId) {
                    var anyInvisible = false
                    val newOptions =
                        f.options.filterIsInstance<Option.StringValue>().mapIndexed { index, o ->
                            val vis = if (value.isNotEmpty()) {
                                o.value.contains(value, ignoreCase = true)
                            } else {
                                index < MAX_OPTIONS
                            }
                            anyInvisible = anyInvisible || !vis
                            o.copy(isVisible = vis)
                        }
                    f.copy(options = if (value.isEmpty() || anyInvisible) newOptions + Option.ViewMore else newOptions)
                } else {
                    f
                }
            }
        }
    }

    /**
     * search for Autocomplete item
     * @param value - term for search
     * @param sellerB2bCode - send seller B2B code is searching for stores products
     */
    private suspend fun searchAutoComplete(value: String, sellerB2bCode: String? = null) {
        reset()
        navigator.withScope<BaseSearchScope> {
            it.showNoProducts.value = false
            it.productSearch.value = value
            searchAsync(withDelay = true, withProgress = false) {

                val unitCodeForStores: String? = sellerB2bCode
                    ?: when (it) {
                        is StoresScope.StorePreview -> userRepo.requireUser().unitCode
                        is SearchScope -> null
                        else -> throw UnsupportedOperationException("unknown search scope")
                    }
                networkSearchScope.autocomplete(value, unitCodeForStores)
                    .onSuccess { body ->
                        it.autoComplete.value = body
                        if (it.autoComplete.value.isEmpty()) {
                            it.showNoProducts.value = true
                        }
                        if (value.isNotEmpty() && body.isEmpty() && it.products.value.isNotEmpty()) {
                            it.products.value = emptyList()
                        }
                    }.onError(navigator)
            }
        }
    }

    private suspend fun selectAutocomplete(autoComplete: AutoComplete) {
//        userRepo.saveLocalSearchHistory(autoComplete) //un comment to save local search history
        reset()
        activeFilters.putAll(
            mapOf(
                autoComplete.query to Option.StringValue(
                    autoComplete.suggestion,
                    false
                )
            )
        )
        navigator.withScope<BaseSearchScope> {
            it.productSearch.value = autoComplete.details.ifEmpty { autoComplete.suggestion }
            it.pagination.reset()
            withProgress {
                it.search(
                    it.pagination,
                    addPage = false,
                    withDelay = false,
                    withProgress = true,
                    onEnd = { it.autoComplete.value = emptyList() },
                )
            }
        }
    }

    private suspend fun selectFilter(filter: Filter, option: Option) {
        navigator.withScope<BaseSearchScope> {
            when (option) {
                is Option.ViewMore -> {
                    it.filters.value = it.filters.value.map { f ->
                        if (filter.queryId == f.queryId) {
                            f.copy(
                                options = f.options.filterIsInstance<Option.StringValue>()
                                    .map { o -> o.copy(isVisible = true) })
                        } else {
                            f
                        }
                    }
                }
                is Option.StringValue -> {
                    it.pagination.reset()
                    it.filters.value = it.filters.value.map { f ->
                        if (filter.queryId == f.queryId) {
                            f.copy(options = f.options.map { op ->
                                when {
                                    op is Option.StringValue && option.value == op.value -> {
                                        val newOption = op.copy(isSelected = !op.isSelected)
                                        if (newOption.isSelected) {
                                            activeFilters[f.queryId] = newOption
                                        } else {
                                            activeFilters.remove(f.queryId)
                                        }
                                        it.calculateActiveFilterNames()
                                        newOption
                                    }
                                    op is Option.StringValue && op.isSelected -> op.copy(isSelected = false)
                                    else -> op
                                }
                            })
                        } else {
                            f
                        }
                    }

                    //when offers switch is on in stores orders
                    val extraFilters = mutableMapOf<String, Option.StringValue>()
                    if (filter.queryId == "offers") {
                        extraFilters["offers"] = option
                        activeFilters =
                            (activeFilters + extraFilters) as HashMap<String, Option.StringValue>
                    }
                    it.search(
                        it.pagination,
                        addPage = false,
                        withDelay = false,
                        withProgress = true,
                        extraFilters = extraFilters
                    )
                }
            }
        }
    }

    private suspend fun clearFilter(filter: Filter?) {
        navigator.withScope<BaseSearchScope> {
            it.pagination.reset()
            it.filters.value = it.filters.value.map { f ->
                when {
                    filter?.queryId == f.queryId -> {
                        it.filterSearches.value = it.filterSearches.value.toMutableMap().apply {
                            remove(f.queryId)
                        }
                        f.copy(
                            options = f.options.map { op ->
                                if (op is Option.StringValue && op.isSelected) {
                                    activeFilters.remove(f.queryId)
                                    it.calculateActiveFilterNames()
                                    op.copy(isSelected = false)
                                } else {
                                    op
                                }
                            }
                        )
                    }
                    else -> f
                }
            }

            //remove offers key if offers switch is off in stores products
            if (filter?.queryId == "offers") {
                activeFilters.remove("offers")
            }

            if (filter == null) {
                activeFilters.clear()
                it.calculateActiveFilterNames()
                it.selectedSortOption.value = it.sortOptions.value.firstOrNull()
                it.productSearch.value = ""
                it.filterSearches.value = emptyMap()
            }
            it.search(
                it.pagination,
                addPage = false,
                withDelay = false,
                withProgress = true,
            )
        }
    }

    private suspend fun selectSortOption(option: SortOption?) {
        navigator.withScope<BaseSearchScope> {
            it.selectedSortOption.value = option ?: it.sortOptions.value.firstOrNull()
            searchInput(false, "", emptyMap())
        }
    }

    private suspend fun loadMoreProducts() {
        navigator.withScope<BaseSearchScope> {
            if (!navigator.scope.value.isInProgress.value && it.pagination.canLoadMore()) {
                setHostProgress(true)
                it.search(
                    it.pagination,
                    addPage = true,
                    withDelay = false,
                    withProgress = true,
                )
            }
        }
    }

    private fun toggleFilter() {
        navigator.withScope<BaseSearchScope> {
            it.isFilterOpened.value = !it.isFilterOpened.value
        }
    }

    private fun updateBatchSelection(check: Boolean, product: ProductSearch) {
        navigator.withScope<BaseSearchScope> {
            it.isBatchSelected.value = check
            it.checkedProduct.value = product
        }
    }

    private fun viewAllManufacturers() {
        searchJob?.cancel()
        activeFilters.clear()
//        it.calculateActiveFilterNames()
    }

    private fun reset() {
        //searchJob?.cancel()
        activeFilters.clear()
//        it.calculateActiveFilterNames()
    }

    private fun resetButton(check: Boolean) {
        navigator.withScope<StoresScope.StorePreview> {
            it.enableButton.value = check
        }
    }

    private fun BaseSearchScope.calculateActiveFilterNames() {
        activeFilterIds.value = activeFilters.map { it.key }
    }

    private suspend inline fun BaseSearchScope.search(
        pagination: Pagination,
        addPage: Boolean = false,
        withDelay: Boolean,
        withProgress: Boolean,
        extraFilters: Map<String, Option.StringValue> = emptyMap(),
        crossinline onEnd: () -> Unit = {}
    ) {
        //searchAsync(withDelay = withDelay, withProgress = withProgress) {
        //val address = userRepo.requireUser().addressData
        val address = userRepo.requireUser()
        showNoProducts.value = false
        networkSearchScope.search(
            selectedSortOption.value?.code,
            (activeFilters + extraFilters).map { (queryName, option) -> queryName to option.value },
            unitCode.takeIf { this is StoresScope.StorePreview },
            address.latitude,
            address.longitude,
            pagination,
            addPage,
        ).onSuccess { body ->
            pagination.setTotal(body.totalResults)
            filtersManufactures.value =
                body.facets.find { s -> s.queryId == "manufacturers" }?.values ?: emptyList()
            filters.value = body.facets.toFilter()
            products.value = /*if (!addPage)*/
                body.products /*else products.value + body.products*/
            totalResults.value = body.totalResults
            sortOptions.value = body.sortOptions
            autoComplete.value = emptyList()
            connectedStockist.value = body.connectedStockists
            selectedStockist.value = body.selectedStockist
            for (item in connectedStockist.value) {
                if (item.unitCode == selectedStockist.value) {
                    selectedTradename.value = item.tradeName
                    break
                }
            }
            if (body.products.isEmpty()) {
                showNoProducts.value = true
            }
            if (selectedSortOption.value == null) {
                selectedSortOption.value = sortOptions.value.firstOrNull()
            }
        }.onError(navigator)
        onEnd()
        //}
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

    private inline fun List<Facet>.toFilter(): List<Filter> {
        return map { facet ->
            val options = facet.values.mapIndexed { index, v ->
                Option.StringValue(
                    v.value,
                    isSelected = activeFilters[facet.queryId]
                        ?.takeIf { v.value == it.value }?.isSelected
                        ?: false,
                    isVisible = index < MAX_OPTIONS,
                )
            }
            Filter(
                name = facet.displayName,
                queryId = facet.queryId,
                options = if (facet.values.size > MAX_OPTIONS) options + Option.ViewMore else options,
            )
        }
    }

    private inline fun List<Facet>.toManufactureFilter(): List<Filter> {
        return map { facet ->
            val options = facet.values.mapIndexed { index, v ->
                Option.StringValue(
                    id = v.id,
                    value = v.value,
                    isSelected = activeFilters[facet.queryId]
                        ?.takeIf { v.value == it.value }?.isSelected
                        ?: false,
                    isVisible = true,
                )
            }
            Filter(
                name = facet.displayName,
                queryId = facet.queryId,
                options = options,
            )
        }
    }

    companion object {
        private const val MAX_OPTIONS = 5
    }
}