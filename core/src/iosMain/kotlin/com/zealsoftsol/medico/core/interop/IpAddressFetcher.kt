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
import platform.darwin.getifaddrs
import platform.darwin.ifaddrs
import platform.darwin.inet_ntoa
import platform.posix.AF_INET
import platform.posix.in_addr
import platform.posix.sockaddr_in

actual class IpAddressFetcher {

    actual fun getIpAddress(): String? = memScoped {
        val interfaces: CPointerVarOf<CPointer<ifaddrs>> = alloc()
        val tempAddr: CPointerVarOf<CPointer<ifaddrs>> = alloc()

        val success = getifaddrs(interfaces.ptr)

        if (success == 0) {
            tempAddr.value = interfaces.value
            while (tempAddr.value != null) {
                tempAddr.pointed?.let { ifa ->
                    val ifaAddr = ifa.ifa_addr!!.pointed
                    if (ifaAddr.sa_family.toInt() == AF_INET) {
                        val name = ifa.ifa_name!!.toKStringFromUtf8()

                        if (name == "en0" || name == "pdp_ip0") {
                            val inAddr = cValue<in_addr> {
                                ifaAddr.reinterpret<sockaddr_in>().sin_addr
                            }
                            inet_ntoa(inAddr)?.toKStringFromUtf8()?.let { return@memScoped it }
                        }
                    }
                    tempAddr.value = ifa.ifa_next
                }
            }
        }

        null
    }
}