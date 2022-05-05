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

    val segmentList: DataSource<MutableList<SegmentedScroller>>  = DataSource(mutableListOf())

    data class SegmentedScroller(
        val title: String,
        var isSelected: Boolean
    )

    private fun prepareSegmentedList(){
       val titles = listOf("0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G",
            "H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")

        titles.forEach {
            val segmentData = SegmentedScroller(it, false)
            segmentList.value.add(segmentData)
        }
    }

    init {
        getManufacturers(true)
        prepareSegmentedList()
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
                page = mCurrentPage,
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

    fun refreshCheckStatus(index: Int) {
        val tempList = segmentList.value
        tempList.forEachIndexed { ind, it ->
            if (it.isSelected) {
                tempList[ind].isSelected = false
            }
        }
        tempList[index].isSelected = true
        segmentList.value = tempList
        startSearch(segmentList.value[index].title)
    }


}