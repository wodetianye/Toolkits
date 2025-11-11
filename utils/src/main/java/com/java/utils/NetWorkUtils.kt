package com.java.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import com.java.utils.log.Logger

/**
 * @Introduce: 网络状态监听.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description :可以通过{@link NetworkObserver}来监听设备的网络状态
 */
object NetWorkUtils : BaseUtils {
    private const val TAG: String = "NetWorkUtils"
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: NetworkCallback? = null
    private var netObservers: MutableList<NetworkObserver>? = null

    init {
        netObservers = ArrayList()
        networkCallback = NetworkCallback()
    }

    override fun initUtils(context: Context) {
        connectivityManager =
            context.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.registerDefaultNetworkCallback(networkCallback!!)
    }

    override fun unInitUtils() {
        connectivityManager?.unregisterNetworkCallback(networkCallback!!)
        networkCallback = null
        netObservers?.clear()
        netObservers = null
    }


    fun registerNetworkObserver(networkCall: NetworkObserver) {
        netObservers?.add(networkCall)
    }

    fun unRegisterNetworkObserver(networkCall: NetworkObserver) {
        netObservers?.remove(networkCall)
    }

    fun checkNetwork(): Boolean? {
        val networkCapabilities =
            connectivityManager?.getNetworkCapabilities(connectivityManager!!.activeNetwork)
        if (ConvertUtils.isNull(networkCapabilities)) return false
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }


    fun getNetworkTypeValue(): Int {
        if (checkNetwork() == false) return -1
        val networkCapabilities =
            connectivityManager?.getNetworkCapabilities(connectivityManager!!.activeNetwork)
        return if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            NetworkCapabilities.TRANSPORT_WIFI
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
            NetworkCapabilities.TRANSPORT_CELLULAR
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true) {
            NetworkCapabilities.TRANSPORT_CELLULAR
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == true) {
            NetworkCapabilities.TRANSPORT_BLUETOOTH
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true) {
            NetworkCapabilities.TRANSPORT_VPN
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) == true) {
            NetworkCapabilities.TRANSPORT_WIFI_AWARE
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) == true) {
            NetworkCapabilities.TRANSPORT_LOWPAN
        } else {
            Logger.d(TAG, "current network type is 其他网络")
            7
        }
    }

    fun getNetworkTypeStr(): String {
        if (checkNetwork() == false) return "无网络"
        val networkCapabilities =
            connectivityManager?.getNetworkCapabilities(connectivityManager!!.activeNetwork)
        return if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            "current network type is 此网络使用WiFi传输"
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
            "current network type is 此网络使用蜂窝传输"
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true) {
            "current network type is 此网络使用以太网传输"
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == true) {
            "current network type is 此网络使用蓝牙传输"
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true) {
            "current network type is 此网络使用VPN传输"
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) == true) {
            "current network type is 此网络使用Wi-Fi感知传输"
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) == true) {
            "current network type is 此网络使用LoWpAn传输"
        } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_USB) == true) {
            "current network type is 此网络使用USB传输"
        } else {
            "current network type is 其他网络"
        }
    }

    class NetworkCallback : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                Logger.i(TAG, "网络验证完成")
                for (code in netObservers!!) {
                    code.onNetValidated() // 新增验证完成回调
                }
            }
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Logger.i(TAG, "网络连接成功,可以使用的时候调用")
            for (code in netObservers!!) {
                code.onNetConnectSuccess()
            }
        }

        override fun onUnavailable() {
            super.onUnavailable()
            Logger.i(TAG, "当网络连接超时或网络请求达不到可用要求时调用")
        }

        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            super.onBlockedStatusChanged(network, blocked)
            Logger.i(TAG, "当访问指定的网络被阻止或解除阻塞时调用")
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            Logger.i(TAG, "当网络正在断开连接时调用")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Logger.i(TAG, "当网络已断开连接时调用")
            for (code in netObservers!!) {
                code.onNetDisConnect()
            }
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties)
            Logger.i(TAG, "当网络连接的属性被修改时调用")
        }
    }

    interface NetworkObserver {
        fun onNetValidated()

        fun onNetConnectSuccess()

        fun onNetUnavailable()

        fun onNetBlockedStatusChanged()

        fun onNetLosing()

        fun onNetLinkPropertiesChanged()

        fun onNetDisConnect()
    }
}