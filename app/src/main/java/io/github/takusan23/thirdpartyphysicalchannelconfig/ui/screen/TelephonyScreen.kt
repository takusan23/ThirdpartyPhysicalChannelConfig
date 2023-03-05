package io.github.takusan23.thirdpartyphysicalchannelconfig.ui.screen

import android.os.Build
import android.telephony.SubscriptionInfo
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.takusan23.thirdpartyphysicalchannelconfig.ui.TelephonyHideApiTool

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun TelephonyScreen() {
    val context = LocalContext.current
    val selectedTabIndex = remember { mutableStateOf(0) }
    val selectSubscriptionInfo = remember { mutableStateOf<SubscriptionInfo?>(null) }
    val selectedSubscriptionId = remember { mutableStateOf(TelephonyHideApiTool.defaultSubscriptionId) }
    val subscriptionInfoList = remember { TelephonyHideApiTool.collectActiveSubscriptionList(context) }.collectAsStateWithLifecycle(initialValue = emptyList())

    val physicalChannelConfigList = remember(selectedSubscriptionId.value) { TelephonyHideApiTool.collectPhysicalChannelConfigList(context, selectedSubscriptionId.value) }.collectAsStateWithLifecycle(initialValue = emptyList())
    val cellInfoList = remember(selectedSubscriptionId.value) { TelephonyHideApiTool.collectCellInfoList(context, selectedSubscriptionId.value) }.collectAsStateWithLifecycle(initialValue = emptyList())

    LazyColumn(
        modifier = Modifier.padding(5.dp)
    ) {
        item {
            if (subscriptionInfoList.value.isNotEmpty()) {
                TabRow(selectedTabIndex = selectedTabIndex.value) {
                    subscriptionInfoList.value.forEachIndexed { index, subscriptionInfo ->

                        Tab(
                            selected = selectedTabIndex.value == index,
                            onClick = {
                                selectedTabIndex.value = index
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
            Text(text = "toString = ${config.toString()}")
            Divider()
        }
        item {
            Text(
                text = "CellInfo ( ${selectSubscriptionInfo.value?.carrierName} )",
                fontSize = 24.sp
            )
        }
        items(cellInfoList.value) { cellInfo ->
            Text(text = cellInfo.toString())
            Divider()
        }
    }

}