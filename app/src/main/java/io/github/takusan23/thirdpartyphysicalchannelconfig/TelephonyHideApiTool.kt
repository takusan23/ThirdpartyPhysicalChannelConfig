package io.github.takusan23.thirdpartyphysicalchannelconfig

import android.content.Context
import android.os.ServiceManager
import android.telephony.CellInfo
import android.telephony.PhysicalChannelConfig
import android.telephony.SubscriptionManager
import android.telephony.TelephonyCallback
import com.android.internal.telephony.IOnSubscriptionsChangedListener
import com.android.internal.telephony.ISub
import com.android.internal.telephony.ITelephony
import com.android.internal.telephony.ITelephonyRegistry
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import rikka.shizuku.ShizukuBinderWrapper

/** システムサービスの名前は adb shell system list でクラス名と名前が紐づいたリストが取得できます。 */
object TelephonyHideApiTool {

    val telephony: ITelephony
        get() = ITelephony.Stub.asInterface(
            ShizukuBinderWrapper(ServiceManager.getService("phone"))
        )

    val telephonyRegistry: ITelephonyRegistry
        get() = ITelephonyRegistry.Stub.asInterface(
            ShizukuBinderWrapper(ServiceManager.getService("telephony.registry"))
        )

    val subscription: ISub
        get() = ISub.Stub.asInterface(
            ShizukuBinderWrapper(ServiceManager.getService("isub"))
        )

    val defaultSubscriptionId: Int
        get() = SubscriptionManager.getActiveDataSubscriptionId()

    /**
     * [PhysicalChannelConfig]を取得する
     *
     * @param context [Context]
     * @param subscriptionId SIMカードを選択する際に利用。[SubscriptionManager.getActiveSubscriptionIdList]参照
     */
    fun collectPhysicalChannelConfigList(
        context: Context,
        subscriptionId: Int = defaultSubscriptionId
    ) = callbackFlow {
        val callback = object : TelephonyCallback(), TelephonyCallback.PhysicalChannelConfigListener {
            override fun onPhysicalChannelConfigChanged(configs: MutableList<PhysicalChannelConfig>) {
                trySend(configs)
            }
        }

        callback.init(context.mainExecutor)
        telephonyRegistry.listenWithEventList(
            true,
            true,
            subscriptionId,
            telephony.currentPackageName,
            context.attributionTag,
            callback.callback,
            intArrayOf(TelephonyCallback.EVENT_PHYSICAL_CHANNEL_CONFIG_CHANGED),
            true
        )

        awaitClose {
            telephonyRegistry.listenWithEventList(
                false,
                false,
                subscriptionId,
                telephony.currentPackageName,
                context.attributionTag,
                callback.callback,
                intArrayOf(0),
                false
            )
        }
    }

    /**
     * [CellInfo]を取得する
     * TODO これも公開APIなので、Shizuku-API を利用する必要はありません
     *
     * @param context [Context]
     * @param subscriptionId SIMカードを選択する際に利用。[SubscriptionManager.getActiveSubscriptionIdList]参照
     */
    fun collectCellInfoList(
        context: Context,
        subscriptionId: Int = defaultSubscriptionId
    ) = callbackFlow {
        val callback = object : TelephonyCallback(), TelephonyCallback.CellInfoListener {
            override fun onCellInfoChanged(cellInfo: MutableList<CellInfo>) {
                trySend(cellInfo)
            }
        }

        callback.init(context.mainExecutor)
        telephonyRegistry.listenWithEventList(
            false,
            false,
            subscriptionId,
            telephony.currentPackageName,
            context.attributionTag,
            callback.callback,
            intArrayOf(TelephonyCallback.EVENT_CELL_INFO_CHANGED),
            true
        )
        awaitClose {
            telephonyRegistry.listenWithEventList(
                false,
                false,
                subscriptionId,
                telephony.currentPackageName,
                context.attributionTag,
                callback.callback,
                intArrayOf(0),
                false
            )
        }
    }

    /**
     * SubscriptionManager を利用して、SIMカードの情報を取得
     * TODO SubscriptionManager は公開されていて、一般にアクセスできるため、Shizuku-API を経由する必要はありません
     *
     * @param context [Context]
     */
    fun collectActiveSubscriptionList(context: Context) = callbackFlow {
        val callback: IOnSubscriptionsChangedListener = object : IOnSubscriptionsChangedListener.Stub() {
            override fun onSubscriptionsChanged() {
                val subscriptionList = subscription.getActiveSubscriptionInfoList(telephony.currentPackageName, context.attributionTag, /*isForAllUserProfiles*/ true)
                trySend(subscriptionList)
            }
        }

        telephonyRegistry.addOnSubscriptionsChangedListener(
            telephony.currentPackageName,
            context.attributionTag,
            callback
        )
        awaitClose {
            telephonyRegistry.removeOnSubscriptionsChangedListener(
                telephony.currentPackageName,
                callback
            )
        }
    }

}