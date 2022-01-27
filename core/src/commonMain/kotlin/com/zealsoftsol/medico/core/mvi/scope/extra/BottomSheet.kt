package com.zealsoftsol.medico.core.mvi.scope.extra

import android.util.Base64
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.nested.BaseSearchScope
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.InvoiceEntry
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.TaxInfo
import java.io.File

sealed class BottomSheet {

    class UploadDocuments(
        val supportedFileTypes: Array<FileType>,
        val isSeasonBoy: Boolean,
    ) : BottomSheet() {

        fun uploadAadhaar(base64: String): Boolean {
            return if (sizeInBytes(base64) <= MAX_FILE_SIZE) {
                EventCollector.sendEvent(Event.Action.Registration.UploadAadhaar(base64))
            } else {
                EventCollector.sendEvent(Event.Action.Registration.UploadFileTooBig)
                false
            }
        }

        fun uploadDrugLicense(base64: String, fileType: FileType): Boolean {
            return if (sizeInBytes(base64) <= MAX_FILE_SIZE) {
                EventCollector.sendEvent(
                    Event.Action.Registration.UploadDrugLicense(
                        base64,
                        fileType
                    )
                )
            } else {
                EventCollector.sendEvent(Event.Action.Registration.UploadFileTooBig)
                false
            }
        }

        private fun sizeInBytes(base64: String): Int =
            (base64.length * 3 / 4) - base64.takeLast(2).count { it == '=' }

        companion object {
            private const val MAX_FILE_SIZE = 10_000_000
        }
    }

    class UploadProfileData(
        val type: String,
        val supportedFileTypes: Array<FileType>,
        val isSeasonBoy: Boolean,
    ) : BottomSheet() {

        fun uploadProfile(base64: String, file: File, fileType: FileType, type: String): Boolean {
            return if (sizeInBytes(base64) <= MAX_FILE_SIZE) {
                EventCollector.sendEvent(
                    Event.Action.Profile.UploadUserProfile(
                        file,
                        fileType,
                        type
                    )
                )
            } else {
                EventCollector.sendEvent(Event.Action.Profile.UploadFileTooBig)
                false
            }
        }

        private fun sizeInBytes(base64: String): Int =
            (base64.length * 3 / 4) - base64.takeLast(2).count { it == '=' }

        companion object {
            private const val MAX_FILE_SIZE = 10_000_000
        }
    }

    class PreviewManagementItem(
        val entityInfo: EntityInfo,
        val isSeasonBoy: Boolean,
        val canSubscribe: Boolean,
    ) : BottomSheet() {

        fun subscribe() =
            EventCollector.sendEvent(Event.Action.Management.RequestSubscribe(entityInfo))
    }

    class ModifyOrderEntry(
        val orderEntry: OrderEntry,
        val canEdit: Boolean,
        val isChecked: DataSource<Boolean>,
    ) : BottomSheet() {

        val quantity = DataSource(orderEntry.servedQty.value)
        val freeQuantity = DataSource(orderEntry.freeQty.value)
        val ptr = DataSource(orderEntry.price.value.toString())
        val batch = DataSource(orderEntry.batchNo)
        val expiry = DataSource(orderEntry.expiryDate?.formatted ?: "")

        fun updateQuantity(value: Double) {
            quantity.value = value
        }

        fun updateFreeQuantity(value: Double) {
            freeQuantity.value = value
        }

        fun updatePtr(value: String) {
            ptr.value = value
        }

        fun updateBatch(value: String) {
            batch.value = value
        }

        fun updateExpiry(value: String) {
            expiry.value = value
        }

        fun toggleCheck() {
            if (EventCollector.sendEvent(Event.Action.Orders.ToggleCheckEntry(orderEntry))) {
                isChecked.value = !isChecked.value
            }
        }

        fun save() =
            EventCollector.sendEvent(
                Event.Action.Orders.SaveEntryQty(
                    orderEntry,
                    quantity.value,
                    freeQuantity.value,
                    ptr.value.toDouble(),
                    batch.value,
                    expiry.value
                )
            )
    }

    data class PreviewStockist(val sellerInfo: SellerInfo) : BottomSheet()

    data class ViewTaxInfo(val taxInfo: TaxInfo) : BottomSheet()

    data class ViewItemTax(val invoiceEntry: InvoiceEntry) : BottomSheet()

    data class ViewQrCode(val qrUrl: String) : BottomSheet()

    data class InStoreViewProduct(val product: InStoreProduct) : BottomSheet() {

        fun addToCart(quantity: Double, freeQuantity: Double): Boolean =
            EventCollector.sendEvent(
                Event.Action.InStore.AddCartItem(
                    product.code,
                    product.spid,
                    quantity,
                    freeQuantity,
                )
            )
    }


    data class BatchViewProduct(val product: ProductSearch, val scope: BaseSearchScope) :
        BottomSheet() {

        fun addToCart(quantity: Double, freeQuantity: Double): Boolean =
            EventCollector.sendEvent(
                Event.Action.InStore.AddCartItem(
                    product.code,
                    product.sellerInfo?.spid ?: "",
                    quantity,
                    freeQuantity,
                )
            )
    }
}