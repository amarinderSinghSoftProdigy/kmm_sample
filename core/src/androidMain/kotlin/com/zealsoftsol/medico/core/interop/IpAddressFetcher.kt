package com.zealsoftsol.medico.core.interop

import java.net.NetworkInterface
import java.util.Locale

actual class IpAddressFetcher {

    actual fun getIpAddress(): String? {
        NetworkInterface.getNetworkInterfaces().toList().forEach { intf ->
            intf.inetAddresses.toList().forEach { addr ->
                if (!addr.isLoopbackAddress) {
                    val sAddr: String = addr.hostAddress
                    val isIPv4 = sAddr.indexOf(':') < 0
                    return if (isIPv4) {
                        sAddr
                    } else {
                        val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                        if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
                            0,
                            delim
                        ).uppercase(Locale.getDefault())
                    }
                }
            }
        }
        return null
    }
}