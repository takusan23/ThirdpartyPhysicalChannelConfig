package io.github.takusan23.thirdpartyphysicalchannelconfig

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import io.github.takusan23.thirdpartyphysicalchannelconfig.ui.screen.MainScreen
import io.github.takusan23.thirdpartyphysicalchannelconfig.ui.theme.ThirdpartyPhysicalChannelConfigTheme
import org.lsposed.hiddenapibypass.HiddenApiBypass

/**
 * MainActivity
 *
 * README.md を読むことをおすすめします
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HiddenApiBypass.addHiddenApiExemptions("")

        setContent {
            ThirdpartyPhysicalChannelConfigTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}
