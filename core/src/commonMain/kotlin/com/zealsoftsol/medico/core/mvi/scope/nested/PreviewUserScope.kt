package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.ScopeNotification
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.GeoPoints
import com.zealsoftsol.medico.data.PreviewItem
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3

data class PreviewUserScope(
    internal val registration2: UserRegistration2,
    internal val registration3: UserRegistration3,
    val isConfirmed: DataSource<Boolean> = DataSource(false),
    override val notifications: DataSource<ScopeNotification?> = DataSource(null)
) : Scope.Child.TabBar(TabBarInfo.Search(ScopeIcon.BACK)), CommonScope.WithNotifications,
    PreviewItem {
    override val traderName = registration3.tradeName
    override val gstin = registration3.gstin
    override val location = registration2.location
    override val city = registration2.city
    override val distance = ""
    override val geo: GeoPoints = GeoPoints(0.0, 0.0)
    override val phoneNumber: String = ""

    fun changeConfirm(value: Boolean) {
        isConfirmed.value = value
    }

    fun addRetailer(): Boolean {
        require(isConfirmed.value) { "confirm before adding retailer" }
        return EventCollector.sendEvent(Event.Action.Registration.ConfirmCreateRetailer)
    }

    data class Congratulations(val tradeName: String) : ScopeNotification {
        override val dismissEvent: Event = Event.Transition.Back
        override val isSimple: Boolean = false
        override val isDismissible: Boolean = true
        override val title: String = "congratulations"
        override val body: String = "retailer_added_template"
    }
}