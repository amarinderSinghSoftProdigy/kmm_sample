package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.AddressData
import com.zealsoftsol.medico.data.B2BData
import com.zealsoftsol.medico.data.ConfirmOrderRequest
import com.zealsoftsol.medico.data.FormattedData
import com.zealsoftsol.medico.data.Invoice
import com.zealsoftsol.medico.data.InvoiceEntry
import com.zealsoftsol.medico.data.InvoiceInfo
import com.zealsoftsol.medico.data.InvoiceResponse
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderInfo
import com.zealsoftsol.medico.data.OrderNewQtyRequest
import com.zealsoftsol.medico.data.OrderResponse
import com.zealsoftsol.medico.data.OrderStatus
import com.zealsoftsol.medico.data.OrderType
import com.zealsoftsol.medico.data.PaginatedData
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.Total
import kotlin.random.Random

class MockOrderScope : NetworkScope.Orders {

    private val rnd = Random(Time.now)

    init {
        "USING MOCK ORDER SCOPE".logIt()
    }

    override suspend fun getOrders(
        type: OrderType,
        unitCode: String,
        search: String,
        from: Long?,
        to: Long?,
        pagination: Pagination
    ) = mockResponse {
        longPaginatedOrderData(20, rnd)
    }

    override suspend fun getOrder(
        type: OrderType,
        unitCode: String,
        orderId: String
    ) = mockResponse<OrderResponse> {
        null
    }

    override suspend fun confirmOrder(request: ConfirmOrderRequest) =
        mockResponse {
            mockEmptyMapBody()
        }

    override suspend fun saveNewOrderQty(request: OrderNewQtyRequest) =
        mockResponse<OrderResponse> {
            null
        }

    override suspend fun getInvoice(
        unitCode: String,
        invoiceId: String
    ) = mockResponse {
        InvoiceResponse(
            data = B2BData(
                addressData = AddressData(
                    "India",
                    "landmark",
                    "Delhi",
                    "Vijayawada",
                    0.0,
                    0.0,
                    "Some location",
                    520001,
                    "",
                    ""
                ),
                drugLicenseNo1 = "drug1",
                drugLicenseNo2 = "drug2",
                gstin = "12345",
                phoneNumber = "+1111111",
                panNumber = "55532",
                tradeName = "Test Trader",
            ),
            invoice = Invoice(
                info = InvoiceInfo(
                    id = Time.now.toString(),
                    date = "${rnd.nextInt(31)}/05/2021",
                    time = "${rnd.nextInt(12)}:${rnd.nextInt(59)}",
                    total = Total(
                        formattedPrice = "₹${rnd.nextInt(10_000)}",
                        price = 0.0,
                        itemCount = 0,
                    ),
                    paymentMethod = PaymentMethod.CASH,
                ),
                tradeName = "Trader 1",
            ),
            invoiceEntries = listOf(
                InvoiceEntry(
                    productName = "Some product",
                    manufacturerName = "Some manufacturer",
                    price = FormattedData("₹ 1,110.09", 0.0),
                    totalAmount = FormattedData("₹ 110.09", 0.0),
                    quantity = FormattedData("2", 2.0),
                )
            )
        )
    }

    override suspend fun getInvoices(
        unitCode: String,
        search: String,
        from: Long?,
        to: Long?,
        pagination: Pagination
    ) = mockResponse {
        longPaginatedInvoiceData(20, rnd)
    }
}

private fun longPaginatedOrderData(size: Int, rnd: Random) =
    PaginatedData(
        (0 until size)
            .map {
                Order(
                    seasonBoyRetailerName = if (rnd.nextInt(3) == 1) "Season boy retailer" else null,
                    tradeName = "Seller $it",
                    info = OrderInfo(
                        id = Time.now.toString(),
                        date = "${rnd.nextInt(31)}/05/2021",
                        time = "${rnd.nextInt(12)}:${rnd.nextInt(59)}",
                        status = OrderStatus.COMPLETED,
                        paymentMethod = if (rnd.nextBoolean()) PaymentMethod.CASH else PaymentMethod.CREDIT,
                        total = Total(
                            formattedPrice = "₹${rnd.nextInt(10_000)}",
                            price = 0.0,
                            itemCount = 0,
                        )
                    ),
                )
            },
        9999999,
    )

private fun longPaginatedInvoiceData(size: Int, rnd: Random) =
    PaginatedData(
        (0 until size)
            .map {
                Invoice(
                    tradeName = "Buyer $it",
                    info = InvoiceInfo(
                        id = Time.now.toString(),
                        date = "${rnd.nextInt(31)}/05/2021",
                        time = "${rnd.nextInt(12)}:${rnd.nextInt(59)}",
                        total = Total(
                            formattedPrice = "₹${rnd.nextInt(10_000)}",
                            price = 0.0,
                            itemCount = 0,
                        ),
                        paymentMethod = if (rnd.nextBoolean()) PaymentMethod.CASH else PaymentMethod.CREDIT,
                    ),
                )
            },
        9999999,
    )