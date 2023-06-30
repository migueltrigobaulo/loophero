package com.loopcorp.loophero

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import com.loopcorp.loophero.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessScreen(navController: NavController) {

    val context = LocalContext.current

    val channelId = "my_channel_id"
    val notificationManager =
        LocalContext.current.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channel = NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_HIGH)
    channel.enableLights(true)
    channel.lightColor = Color.RED
    channel.enableVibration(true)
    notificationManager.createNotificationChannel(channel)

    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(), content = {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        val imageLoader = ImageLoader.Builder(LocalContext.current)
                            .components {
                                add(ImageDecoderDecoder.Factory())
                            }
                            .build()

                        Image(
                            painter = rememberAsyncImagePainter(
                                R.drawable.shield_2,
                                imageLoader
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .alpha(.8F)
                                .fillMaxWidth(0.65F)
                        )
                        Column(modifier = Modifier.fillMaxWidth()) {

                                ReadyMessage()
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    RoundButtonWithArrow(onClick = {
                                        startService(context)
                                        navController.popBackStack()
                                        navController.navigate("configurationScreen")
                                    })
                            }
                        }
                    }
                }
            })
    }
}


@Composable
private fun ReadyMessage() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.everything_ready),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        ),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.can_continue),
        style = TextStyle(fontSize = 16.sp),
        textAlign = TextAlign.Center
    )
}


