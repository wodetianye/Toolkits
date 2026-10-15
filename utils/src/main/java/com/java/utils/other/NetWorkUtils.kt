package com.java.utils.other

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import com.java.utils.BaseUtils
import com.java.utils.log.Logger

/**
 * @Introduce: 网络状态监听.
 * @Author: Lww
 * @Date: 2023/11/13
 * @Description: 通过 [NetworkObserver] 监听设备的网络状态.
 *
 * 注意：使用前必须调用 [initUtils] 初始化。
 */
object NetWorkUtils : BaseUtils {
    private const val TAG = "NetWorkUtils"

    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: NetworkCallback? = null
    private val netObservers: MutableList<NetworkObserver> = mutableListOf()

    // 自定义网络类型常量，避免依赖 NetworkCapabilities 的版本差异
    const val NETWORK_TYPE_NONE = -1
    const val NETWORK_TYPE_WIFI = 0
    const val NETWORK_TYPE_CELLULAR = 1
    const val NETWORK_TYPE_ETHERNET = 2
    const val NETWORK_TYPE_BLUETOOTH = 3
    const val NETWORK_TYPE_VPN = 4
    const val NETWORK_TYPE_WIFI_AWARE = 5
    const val NETWORK_TYPE_LOWPAN = 6
    const val NETWORK_TYPE_USB = 7
    const val NETWORK_TYPE_OTHER = 99

    override fun initUtils(context: Context) {
        if (connectivityManager != null) return   // 避免重复注册
        val cm = context.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return
        connectivityManager = cm
        networkCallback = NetworkCallback()
        try {
            cm.registerDefaultNetworkCallback(networkCallback!!)
        } catch (e: Exception) {
            Logger.e("$TAG registerDefaultNetworkCallback failed: ${e.message}")
        }
    }

    override fun unInitUtils() {
        try {
            networkCallback?.let { connectivityManager?.unregisterNetworkCallback(it) }
        } catch (e: Exception) {
            Logger.e("$TAG unregisterNetworkCallback failed: ${e.message}")
        }
        networkCallback = null
        connectivityManager = null
        netObservers.clear()
    }

    /**
     * 注册网络观察者。
     *
     * 例：registerNetworkObserver(observer)
     */
    @JvmStatic
    fun registerNetworkObserver(observer: NetworkObserver) {
        if (!netObservers.contains(observer)) {
            netObservers.add(observer)
        }
    }

    /**
     * 反注册网络观察者。
     *
     * 例：unRegisterNetworkObserver(observer)
     */
    @JvmStatic
    fun unRegisterNetworkObserver(observer: NetworkObserver) {
        netObservers.remove(observer)
    }

    /**
     * 是否有可用网络（已通过系统验证）。
     *
     * 例：checkNetwork() -> true
     */
    @JvmStatic
    fun checkNetwork(): Boolean {
        val cm = connectivityManager ?: return false
        val capabilities = getActiveCapabilities(cm) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * 是否已连接 WiFi。
     *
     * 例：isWifiConnected() -> true
     */
    @JvmStatic
    fun isWifiConnected(): Boolean = getNetworkTypeValue() == NETWORK_TYPE_WIFI

    /**
     * 是否使用蜂窝网络。
     *
     * 例：isMobileConnected() -> true
     */
    @JvmStatic
    fun isMobileConnected(): Boolean = getNetworkTypeValue() == NETWORK_TYPE_CELLULAR

    /**
     * 是否使用以太网。
     */
    @JvmStatic
    fun isEthernetConnected(): Boolean = getNetworkTypeValue() == NETWORK_TYPE_ETHERNET

    /**
     * 获取当前网络类型（自定义常量）。
     *
     * 例：getNetworkTypeValue() -> NETWORK_TYPE_WIFI
     */
    @JvmStatic
    fun getNetworkTypeValue(): Int {
        if (!checkNetwork()) return NETWORK_TYPE_NONE
        val cm = connectivityManager ?: return NETWORK_TYPE_NONE
        val capabilities = getActiveCapabilities(cm) ?: return NETWORK_TYPE_NONE
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NETWORK_TYPE_WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NETWORK_TYPE_CELLULAR
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NETWORK_TYPE_ETHERNET
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> NETWORK_TYPE_BLUETOOTH
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> NETWORK_TYPE_VPN
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) ->
                NETWORK_TYPE_WIFI_AWARE
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) ->
                NETWORK_TYPE_LOWPAN
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_USB) ->
                NETWORK_TYPE_USB
            else -> NETWORK_TYPE_OTHER
        }
    }

    /**
     * 获取当前网络类型名称（简洁，可直接展示）。
     *
     * 例：getNetworkTypeStr() -> "WiFi"
     */
    @JvmStatic
    fun getNetworkTypeStr(): String {
        return when (getNetworkTypeValue()) {
            NETWORK_TYPE_NONE -> "无网络"
            NETWORK_TYPE_WIFI -> "WiFi"
            NETWORK_TYPE_CELLULAR -> "蜂窝网络"
            NETWORK_TYPE_ETHERNET -> "以太网"
            NETWORK_TYPE_BLUETOOTH -> "蓝牙"
            NETWORK_TYPE_VPN -> "VPN"
            NETWORK_TYPE_WIFI_AWARE -> "Wi-Fi 感知"
            NETWORK_TYPE_LOWPAN -> "LoWPAN"
            NETWORK_TYPE_USB -> "USB"
            else -> "其他网络"
        }
    }

    private fun getActiveCapabilities(cm: ConnectivityManager): NetworkCapabilities? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm.getNetworkCapabilities(cm.activeNetwork)
            } else {
                @Suppress("DEPRECATION")
                cm.getNetworkCapabilities(cm.activeNetworkInfo?.let { cm.activeNetwork })
            }
        } catch (e: Exception) {
            Logger.e("$TAG getActiveCapabilities failed: ${e.message}")
            null
        }
    }

    class NetworkCallback : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                Logger.i("$TAG 网络验证完成")
                safeNotify { it.onNetValidated() }
            }
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Logger.i("$TAG 网络连接成功")
            safeNotify { it.onNetConnectSuccess() }
        }

        override fun onUnavailable() {
            super.onUnavailable()
            Logger.i("$TAG 网络不可用")
            safeNotify { it.onNetUnavailable() }
        }

        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            super.onBlockedStatusChanged(network, blocked)
            Logger.i("$TAG 网络阻塞状态变化: blocked=$blocked")
            safeNotify { it.onNetBlockedStatusChanged() }
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            Logger.i("$TAG 网络正在断开: maxMsToLive=$maxMsToLive")
            safeNotify { it.onNetLosing() }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Logger.i("$TAG 网络已断开")
            safeNotify { it.onNetDisConnect() }
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties)
            Logger.i("$TAG 网络属性变化")
            safeNotify { it.onNetLinkPropertiesChanged() }
        }

        /** 安全遍历观察者，避免 unInitUtils 后回调导致 NPE。 */
        private inline fun safeNotify(action: (NetworkObserver) -> Unit) {
            val snapshot = netObservers.toList()
            snapshot.forEach { observer ->
                try {
                    action(observer)
                } catch (e: Exception) {
                    Logger.e("$TAG notify observer failed: ${e.message}")
                }
            }
        }
    }

    /**
     * 网络观察者接口。
     *
     * 建议 Java 使用者继承 [SimpleNetworkObserver]，避免重写所有方法。
     */
    interface NetworkObserver {
        fun onNetValidated()
        fun onNetConnectSuccess()
        fun onNetUnavailable()
        fun onNetBlockedStatusChanged()
        fun onNetLosing()
        fun onNetLinkPropertiesChanged()
        fun onNetDisConnect()
    }

    /**
     * 网络观察者空实现，方便只重写关心的方法。
     *
     * Java 用法：
     * ```
     * NetWorkUtils.registerNetworkObserver(new NetWorkUtils.SimpleNetworkObserver() {
     *     @Override
     *     public void onNetConnectSuccess() { ... }
     * });
     * ```
     */
    abstract class SimpleNetworkObserver : NetworkObserver {
        override fun onNetValidated() {}
        override fun onNetConnectSuccess() {}
        override fun onNetUnavailable() {}
        override fun onNetBlockedStatusChanged() {}
        override fun onNetLosing() {}
        override fun onNetLinkPropertiesChanged() {}
        override fun onNetDisConnect() {}
    }
}