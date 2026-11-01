package com.ericwathome.stovetime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StovetimeTheme {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(StovetimeTheme.colors.canvas)
                            .safeDrawingPadding(),
                    contentAlignment = Alignment.Center,
                ) {
                    AppName()
                }
            }
        }
    }
}

@Composable
private fun AppName(modifier: Modifier = Modifier) {
    StoveText(
        text = stringResource(R.string.app_name),
        style = StovetimeTheme.typography.display,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun AppNamePreview() {
    StovetimeTheme {
        AppName()
    }
}
