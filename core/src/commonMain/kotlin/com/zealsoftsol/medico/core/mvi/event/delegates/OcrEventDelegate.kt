package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.extensions.log
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.CommonScope
import com.zealsoftsol.medico.core.mvi.scope.extra.BottomSheet
import com.zealsoftsol.medico.core.mvi.scope.regular.OcrScope
import com.zealsoftsol.medico.core.repository.UserRepo

internal class OcrEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.Ocr>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Ocr) = when (event) {
        is Event.Action.Ocr.ShowUploadBottomSheet -> showUploadBottomSheet(event.type)
        is Event.Action.Ocr.GetOcrImage -> setOcrImage(event.filePath)
    }

    private fun setOcrImage(filePath: String) {
        navigator.withScope<OcrScope> {
            it.imagePath.value = filePath
        }
    }

    private fun showUploadBottomSheet(type: String) {
        navigator.withScope<CommonScope.UploadDocument> {
            val scope = scope.value
            scope.bottomSheet.value = BottomSheet.GetOcrImageData(
                type,
                it.supportedFileTypes,
                it.isSeasonBoy,
            )
        }
    }

}