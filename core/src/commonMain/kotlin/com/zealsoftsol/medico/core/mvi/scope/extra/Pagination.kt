package com.zealsoftsol.medico.core.mvi.scope.extra

class Pagination : PaginationHelper {
    internal var currentPage: Int = 0
    internal var totalItems: Int = 0

    override fun canLoadMore() = (currentPage + 1) * DEFAULT_ITEMS_PER_PAGE < totalItems

    companion object {
        internal const val DEFAULT_ITEMS_PER_PAGE = 20
    }
}

interface PaginationHelper {
    fun canLoadMore(): Boolean
}