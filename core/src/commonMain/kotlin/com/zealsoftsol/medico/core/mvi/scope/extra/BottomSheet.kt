package com.zealsoftsol.medico.core.mvi.scope.extra

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.core.mvi.scope.Scope
import com.zealsoftsol.medico.core.mvi.scope.nested.BaseSearchScope
import com.zealsoftsol.medico.core.mvi.scope.nested.OffersScope
import com.zealsoftsol.medico.data.Batch
import com.zealsoftsol.medico.data.BatchUpdateRequest
import com.zealsoftsol.medico.data.EntityInfo
import com.zealsoftsol.medico.data.FileType
import com.zealsoftsol.medico.data.FormattedData
import com.zealsoftsol.medico.data.InStoreProduct
import com.zealsoftsol.medico.data.InvoiceEntry
import com.zealsoftsol.medico.data.OfferProductRequest
import com.zealsoftsol.medico.data.OrderEntry
import com.zealsoftsol.medico.data.ProductSearch
import com.zealsoftsol.medico.data.ProductsData
import com.zealsoftsol.medico.data.PromotionType
import com.zealsoftsol.medico.data.Promotions
import com.zealsoftsol.medico.data.SellerInfo
import com.zealsoftsol.medico.data.TaxInfo

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

        fun uploadProfile(base64: String, fileType: FileType, type: String): Boolean {
            return if (sizeInBytes(base64) <= MAX_FILE_SIZE) {
                EventCollector.sendEvent(
                    Event.Action.Profile.UploadUserProfile(
                        size = sizeInBytes(base64).toString(),
                        asBase64 = base64,
                        fileType = fileType,
                        type = type
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

    class UpdateOfferStatus(
        val info: String,
        val name: String,
        val active: Boolean
    ) : BottomSheet() {
        fun update() =
            EventCollector.sendEvent(Event.Action.Offers.UpdateOffer(info, active))
    }

    class EditBatchSheet(
        val info: Batch,
        val productsData: ProductsData
    ) : BottomSheet() {

        val promo = DataSource(info)
        val quantity = DataSource(info.stock.value.toString())
        val mrp = DataSource(info.mrp.value.toString())
        val ptr = DataSource(info.ptr.value.toString())
        val expiry = DataSource(info.expiryDate)
        val batchNo = DataSource(info.batchNo)

        fun updateExpiry(value: String) {
            expiry.value = value
        }

        fun updateMrp(value: String) {
            mrp.value = value
        }

        fun updatePtr(value: String) {
            ptr.value = value
        }

        fun updateQuantity(value: String) {
            quantity.value = value
        }

        fun updateBatch(value: String) {
            batchNo.value = value
        }


        fun editBatch() {
            val request = BatchUpdateRequest(
                productCode = productsData.id ?: "",
                manufacturerCode = productsData.manufacturerCode ?: "",
                hsnCode = promo.value.hsncode,
                vendorProductName = productsData.vendorProductName ?: "",
                spid = promo.value.spid,
                stock = quantity.value.toDouble(),
                expiryDate = expiry.value,
                ptr = ptr.value.toDouble(),
                mrp = mrp.value.toDouble(),
                batchLotNo = batchNo.value,
                mfgDate = "",
                warehouseUnitCode = productsData.warehouseUnitCode ?: "",
                warehouseCode = productsData.warehouseCode ?: "",
                status = productsData.status ?: "",
            )
            EventCollector.sendEvent(Event.Action.Inventory.UpdateBatch(request))
        }
    }

    class UpdateOffer(
        val scope: Scope,
        val info: Promotions,
        val types: List<PromotionType>
    ) : BottomSheet() {
        fun update() {
            val request = OfferProductRequest()
            request.discount = discount.value
            request.buy = quantity.value
            request.free = freeQuantity.value
            request.manufacturerCode = promo.value.manufacturerCode
            request.productCode = promo.value.productCode
            request.active = active.value
            request.spid = promo.value.spid
            request.isOfferForAllUsers = true
            request.connectedUsers = ArrayList()
            request.stock = 0.0
            request.startDate = 1644214031075
            request.endDate = 1675750031075
            request.promotionType = promotionType.value
            if (scope is OffersScope.ViewOffers) {
                EventCollector.sendEvent(
                    Event.Action.Offers.EditOffer(
                        promo.value.promoCode,
                        request
                    )
                )
            } else {
                EventCollector.sendEvent(
                    Event.Action.Offers.EditCreatedOffer(
                        promo.value.promoCode,
                        request
                    )
                )

            }
        }

        val promo = DataSource(info)
        val active = DataSource(info.active)
        val quantity = DataSource(info.buy.value)
        val freeQuantity = DataSource(info.free.value)
        val discount = DataSource(info.productDiscount.value)
        val promotionType = DataSource(info.promotionTypeData.code)

        fun updateQuantity(value: Double) {
            quantity.value = value
        }

        fun updateActive(value: Boolean) {
            active.value = value
        }

        fun updateFreeQuantity(value: Double) {
            freeQuantity.value = value
        }

        fun updateDiscount(value: Double) {
            discount.value = value
        }

        fun updatePromotionType(value: String) {
            promotionType.value = value
        }

        fun getIndex(): Int {
            types.forEachIndexed { index, value ->
                if (promotionType.value == value.code) {
                    return index
                }
            }
            return 0
        }

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