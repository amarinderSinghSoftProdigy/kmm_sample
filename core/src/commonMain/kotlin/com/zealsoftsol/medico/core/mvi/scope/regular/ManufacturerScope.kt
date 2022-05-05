package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.AutoComplete
import com.zealsoftsol.medico.data.ManufacturerItem

class ManufacturerScope : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.OnlyBackHeader("manufacturers")

    val manufacturers: DataSource<MutableList<ManufacturerItem>> = DataSource(mutableListOf())
    var totalItems = 0
    private var mCurrentPage = 0

    init {
        getManufacturers(true)
    }

    fun getManufacturers(
        isFirstLoad: Boolean = false,
        search: String = "",
    ) {
        if (isFirstLoad)
            mCurrentPage = 0
        else
            mCurrentPage += 1

        EventCollector.sendEvent(
            Event.Action.Manufacturers.GetManufacturers(
                page = 1,
                search = search,
            )
        )
    }


    /**
     * start search for a maufacturer
     */
    fun startSearch(search: String?) {
        manufacturers.value.clear()
        if (search.isNullOrEmpty()) {
            getManufacturers(true)
        } else {
            mCurrentPage = 0
            EventCollector.sendEvent(
                Event.Action.Manufacturers.GetManufacturers(
                    page = 0,
                    search = search,
                )
            )
        }
    }


    /**
     * update current manufacturer and get new results
     */
    fun updateManufacturers(list: List<ManufacturerItem>) {
        if (manufacturers.value.isEmpty()) {
            manufacturers.value = list as MutableList<ManufacturerItem>
        } else {
            manufacturers.value.addAll(list)
        }
    }


    /**
     * Opens search screen with params required for search based on brand
     * @param searchTerm Search term for the brand
     * @param field Which field to search (for eg - manufacturer)
     */
    fun startBrandSearch(searchTerm: String) {
        val autoComplete = AutoComplete(
            query = "manufacturers", details = "in Manufacturers",
            suggestion = searchTerm
        )
        EventCollector.sendEvent(Event.Transition.Search(autoComplete))
    }


}