package com.zealsoftsol.medico.core.mvi.scope.extra

class Pagination(internal val itemsPerPage: Int = DEFAULT_ITEMS_PER_PAGE) {
    private var currentPage: Int = 0
    private var totalItems: Int = 0

    fun canLoadMore() = (currentPage + 1) * itemsPerPage < totalItems

    internal fun setTotal(value: Int) {
        totalItems = value
    }

    internal fun reset() {
        currentPage = 0
        totalItems = 0
    }

    internal fun pageLoaded() {
        if (totalItems != 0) currentPage++
    }

    internal fun nextPage(): Int {
        return if (totalItems == 0) 0 else currentPage + 1
    }

    companion object {
        internal const val DEFAULT_ITEMS_PER_PAGE = 20
    }
}