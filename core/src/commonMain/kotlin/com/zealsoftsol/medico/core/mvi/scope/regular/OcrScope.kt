package com.zealsoftsol.medico.core.mvi.scope.regular

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.data.FileType

class OcrScope : Scope.Child.TabBar(),
    CommonScope.CanGoBack, CommonScope.UploadDocument {

    fun previewImage(value: String) =
        EventCollector.sendEvent(Event.Action.Stores.ShowLargeImage(value,"file"))

    val imagePath = DataSource("")
    override val supportedFileTypes: Array<FileType> = FileType.forDrugLicense()

}