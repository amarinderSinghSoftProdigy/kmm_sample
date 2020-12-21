package com.zealsoftsol.medico.core.interop

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.cinterop.value
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.toKString
import platform.darwin.getifaddrs
import platform.darwin.ifaddrs
import platform.darwin.inet_ntoa
import platform.darwin.freeifaddrs
import platform.posix.getnameinfo
import platform.posix.AF_INET
import platform.posix.AF_INET6
import platform.posix.NI_MAXHOST
import platform.posix.in_addr
import platform.posix.sockaddr_in
import platform.posix.NI_NUMERICHOST

actual class IpAddressFetcher {

    actual fun getIpAddress(): String? = memScoped {
        var address : String? = null
        
        // Get list of all interfaces on the local machine:
        val ifaddr: CPointerVarOf<CPointer<ifaddrs>> = alloc()

        val success = getifaddrs(ifaddr.ptr)
        if (success != 0) return null
        
        val tempAddr = ifaddr

        while (tempAddr.value != null) {
            tempAddr.pointed?.let { ifa ->
                val ifaAddr = ifa.ifa_addr!!.pointed
                if (ifaAddr.sa_family.toInt() == AF_INET || 
                    ifaAddr.sa_family.toInt() == AF_INET6) {
                    val name = ifa.ifa_name!!.toKStringFromUtf8()

                    if (name == "en0" || name == "pdp_ip0") {
                        val iptext = allocArray<ByteVar>(NI_MAXHOST)

                        getnameinfo(ifa.ifa_addr, ifaAddr.sa_len.toUInt(),
                            iptext, NI_MAXHOST,
                            null, 0, NI_NUMERICHOST)

                        address = iptext.toKString()
                    }
                }
                tempAddr.value = ifa.ifa_next
            }
        }

        freeifaddrs(ifaddr.value)
        return address
    }
}
