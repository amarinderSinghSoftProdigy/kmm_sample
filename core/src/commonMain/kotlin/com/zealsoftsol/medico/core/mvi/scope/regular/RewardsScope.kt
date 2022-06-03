package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.RewardItem

class RewardsScope : Scope.Child.TabBar(),
    CommonScope.CanGoBack {

    var totalItems = 0
    private var mCurrentPage = 0
    val rewardsList = DataSource<MutableList<RewardItem>>(mutableListOf())
    val showNoCashback = DataSource(false)

    override fun overrideParentTabBarInfo(tabBarInfo: TabBarInfo) =
        TabBarInfo.OnlyBackHeader("rewards_cashback")

    init {
        getRewards(true)
    }


    /**
     * get all available rewards
     */
    fun getRewards(
        isFirstLoad: Boolean = false,
    ) {
        if (isFirstLoad)
            mCurrentPage = 0
        else
            mCurrentPage += 1

        EventCollector.sendEvent(Event.Action.Rewards.GetRewards(page = mCurrentPage))
    }

    /**
     * update current rewards and get new results
     */
    fun updateRewards(list: List<RewardItem>) {
        if (rewardsList.value.isEmpty()) {
            rewardsList.value = list as MutableList<RewardItem>
        } else {
            rewardsList.value.addAll(list)
        }
    }

}