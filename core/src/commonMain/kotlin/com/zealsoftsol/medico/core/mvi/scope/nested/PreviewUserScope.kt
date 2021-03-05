package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.ScopeIcon
import com.zealsoftsol.medico.core.mvi.scope.TabBarInfo
import com.zealsoftsol.medico.data.GeoData
import com.zealsoftsol.medico.data.GeoPoints
import com.zealsoftsol.medico.data.PreviewItem
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3

data class PreviewUserScope(
    internal val registration2: UserRegistration2,
    internal val registration3: UserRegistration3,
    val isConfirmed: DataSource<Boolean> = DataSource(false),
) : Scope.Child.TabBar(TabBarInfo.Search(ScopeIcon.BACK)),
    PreviewItem {
    override val tradeName = registration3.tradeName
    override val gstin = registration3.gstin
    override val geoData = GeoData(
        location = registration2.location,
        pincode = registration2.pincode,
        city = registration2.city,
        distance = "",
        origin = GeoPoints(0.0, 0.0),
        destination = GeoPoints(0.0, 0.0),
    )
    override val phoneNumber: String = ""

    fun changeConfirm(value: Boolean) {
        isConfirmed.value = value
    }

    fun addRetailer(): Boolean {
        require(isConfirmed.value) { "confirm before adding retailer" }
        TODO("undefined")
    }
}