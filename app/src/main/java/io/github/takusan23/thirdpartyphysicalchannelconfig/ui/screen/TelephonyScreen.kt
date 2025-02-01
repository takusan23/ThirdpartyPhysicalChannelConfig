package io.github.takusan23.thirdpartyphysicalchannelconfig.ui.screen

import android.telephony.SubscriptionInfo
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.takusan23.thirdpartyphysicalchannelconfig.TelephonyHideApiTool
import java.lang.reflect.Method

@Composable
fun TelephonyScreen() {
    val context = LocalContext.current
    val selectedTabIndex = remember { mutableIntStateOf(0) }
    val selectSubscriptionInfo = remember { mutableStateOf<SubscriptionInfo?>(null) }
    val selectedSubscriptionId = remember { mutableIntStateOf(TelephonyHideApiTool.defaultSubscriptionId) }
    val subscriptionInfoList = remember { TelephonyHideApiTool.collectActiveSubscriptionList(context) }.collectAsStateWithLifecycle(initialValue = emptyList())

    val physicalChannelConfigList = remember(selectedSubscriptionId.intValue) {
        TelephonyHideApiTool.collectPhysicalChannelConfigList(context, selectedSubscriptionId.intValue)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    val cellInfoList = remember(selectedSubscriptionId.intValue) {
        TelephonyHideApiTool.collectCellInfoList(context, selectedSubscriptionId.intValue)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            item {
                if (subscriptionInfoList.value.isNotEmpty()) {
                    TabRow(selectedTabIndex = selectedTabIndex.intValue) {
                        subscriptionInfoList.value.forEachIndexed { index, subscriptionInfo ->

                            Tab(
                                selected = selectedTabIndex.intValue == index,
                                onClick = {
                                    selectedTabIndex.intValue = index
                                    selectSubscriptionInfo.value = subscriptionInfo
                                }
                            ) {
                                Text(
                                    modifier = Modifier.padding(5.dp),
                                    text = subscriptionInfo?.carrierName.toString()
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    text = "PhysicalChannelConfig ( ${selectSubscriptionInfo.value?.carrierName} )",
                    fontSize = 24.sp
                )
            }
            items(physicalChannelConfigList.value) { config ->
/*
            Text(text = "band = ${config.band}")
            Text(text = "cellBandwidthDownlinkKhz = ${config.cellBandwidthDownlinkKhz}")
            Text(text = "cellBandwidthUplinkKhz = ${config.cellBandwidthUplinkKhz}")
            Text(text = "connectionStatus = ${config.connectionStatus}")
            Text(text = "downlinkChannelNumber = ${config.downlinkChannelNumber}")
            Text(text = "downlinkFrequencyKhz = ${config.downlinkFrequencyKhz}")
            Text(text = "networkType = ${config.networkType}")
            Text(text = "physicalCellId = ${config.physicalCellId}")
            Text(text = "uplinkChannelNumber = ${config.uplinkChannelNumber}")
            Text(text = "uplinkFrequencyKhz = ${config.uplinkFrequencyKhz}")
            Text(text = "uplinkFrequencyKhz = ${config.uplinkFrequencyKhz}")
            Text(text = "toString = $config")
*/
                Text(
                    text = config::class.java.allMethod
                        .map { it.name to it.safeInvoke(config) }
                        .joinToString(separator = "\n") { "${it.first} = ${it.second}" }
                )

                HorizontalDivider()
            }
            item {
                Text(
                    text = "CellInfo ( ${selectSubscriptionInfo.value?.carrierName} )",
                    fontSize = 24.sp
                )
            }
            items(cellInfoList.value) { cellInfo ->
                Text(text = cellInfo.toString())
                HorizontalDivider()
            }
        }
    }

}

private fun Method.safeInvoke(obj: Any) = runCatching { this.invoke(obj) }.getOrNull()

private val <T> Class<T>.allMethod
    get() = (this.methods + this.declaredMethods).distinctBy { it.name }