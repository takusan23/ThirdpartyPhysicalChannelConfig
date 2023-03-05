package io.github.takusan23.thirdpartyphysicalchannelconfig.ui.screen

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import rikka.shizuku.Shizuku

/** メイン画面 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val startRoute = remember {
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) MainScreenNav.TelephonyScreen else MainScreenNav.PermissionScreen
    }

    NavHost(navController = navController, startDestination = startRoute.path) {
        composable(MainScreenNav.PermissionScreen.path) {
            PermissionScreen { navController.navigate(MainScreenNav.TelephonyScreen.path) }
        }
        composable(MainScreenNav.TelephonyScreen.path) {
            TelephonyScreen()
        }
    }

}

enum class MainScreenNav(val path: String) {
    /** Shizuku 権限要求画面 */
    PermissionScreen("permission"),

    /** [android.telephony.PhysicalChannelConfig] を表示する画面 */
    TelephonyScreen("telephony"),
}