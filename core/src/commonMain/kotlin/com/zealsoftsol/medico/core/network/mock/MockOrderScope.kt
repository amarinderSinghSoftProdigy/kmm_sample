package com.zealsoftsol.medico.core.network.mock

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.interop.Time
import com.zealsoftsol.medico.core.mvi.scope.extra.Pagination
import com.zealsoftsol.medico.core.network.NetworkScope
import com.zealsoftsol.medico.data.ConfirmOrderRequest
import com.zealsoftsol.medico.data.ErrorCode
import com.zealsoftsol.medico.data.Order
import com.zealsoftsol.medico.data.OrderInfo
import com.zealsoftsol.medico.data.OrderNewQtyRequest
import com.zealsoftsol.medico.data.OrderResponse
import com.zealsoftsol.medico.data.OrderType
import com.zealsoftsol.medico.data.PaginatedData
import com.zealsoftsol.medico.data.PaymentMethod
import com.zealsoftsol.medico.data.Response
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
    ): Response.Wrapped<PaginatedData<Order>> = mockResponse {
        Response.Wrapped(longPaginatedData(20, rnd), true)
    }

    override suspend fun getOrder(
        type: OrderType,
        unitCode: String,
        orderId: String
    ): Response.Wrapped<OrderResponse> = mockResponse {
        Response.Wrapped(null, false)
    }

    override suspend fun confirmOrder(request: ConfirmOrderRequest): Response.Wrapped<ErrorCode> =
        mockResponse {
            Response.Wrapped(null, true)
        }

    override suspend fun saveNewOrderQty(request: OrderNewQtyRequest): Response.Wrapped<OrderResponse> =
        mockResponse {
            Response.Wrapped(null, true)
        }
}

private fun longPaginatedData(size: Int, rnd: Random) =
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
                        status = "status",
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