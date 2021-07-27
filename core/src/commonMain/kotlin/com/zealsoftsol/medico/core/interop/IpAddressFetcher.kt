package com.zealsoftsol.medico.core.interop

expect class IpAddressFetcher constructor() {
    fun getIpAddress(): String?
}