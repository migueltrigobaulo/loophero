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
fun PermissionScreen(navController: NavController) {

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
                            val lifecycleState =
                                LocalLifecycleOwner.current.lifecycle.observeAsState()
                            val state = lifecycleState.value

                            var hasNotificationPermission by remember {
                                if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    mutableStateOf(
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED
                                    )
                                } else mutableStateOf(true)
                            }

                            if (!checkAccessibilityService(context) || !hasNotificationPermission) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.4F),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = CenterHorizontally
                                ) {
                                    PermissionMessage()
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.6F),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = CenterHorizontally
                                ) {
                                    PermissionButtons(hasNotificationPermission, context)
                                }
                            } else {
                                if (context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                        .getBoolean("firstSetup", true)
                                ) {
                                    ReadyMessage()
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        RoundButtonWithArrow(onClick = {
                                            startService(context)
                                            context.getSharedPreferences(
                                                "MyPrefs",
                                                Context.MODE_PRIVATE
                                            ).edit().putBoolean("firstSetup", false).apply()
                                            navController.popBackStack()
                                            navController.navigate("configurationScreen")
                                        })
                                    }
                                } else {
                                    navController.popBackStack()
                                    navController.navigate("configurationScreen")
                                }

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

@Composable
private fun PermissionButtons(hasNotificationPermission: Boolean, context: Context) {
    var hasNotificationPermission1 by remember {
        mutableStateOf(hasNotificationPermission)
    }


    val launcherNotificationPermission =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasNotificationPermission1 = isGranted
            })
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp), onClick = {
            if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcherNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }, enabled = !hasNotificationPermission1
    ) {
        if (hasNotificationPermission1) {
            Icon(
                Icons.Default.Check,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))

        }
        Text(text = stringResource(R.string.notifications))
    }
    Spacer(modifier = Modifier.size(10.dp))
    DialogButton(context = context)

}

@Composable
fun DialogButton(context: Context) {
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showDialog.value = true },
            content = { Text(text = stringResource(id = R.string.accessibility)) }
        )

        if (showDialog.value) {
            Dialog(
                onDismissRequest = { showDialog.value = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface
                            )
                            .padding(20.dp)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.accessibility),
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.accessibility_permission_rationale),
                            style = TextStyle(fontSize = 14.sp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 10.dp), onClick = {
                                        showDialog.value = false
                                }
                            ) {
                                Text(text = stringResource(R.string.deny))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 10.dp), onClick = {
                                    requestAccessibilityPermission(context)
                                }
                            ) {
                                Text(text = stringResource(R.string.allow))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionMessage() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.permissions_needed),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        ),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.permissions_message),
        style = TextStyle(fontSize = 14.sp),
        textAlign = TextAlign.Center
    )
}


@Composable
fun RoundButtonWithArrow(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            state.value = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }
    return state
}

private fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
    val expectedComponentName = ComponentName(context, serviceClass)
    val enabledServicesSetting = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    return enabledServicesSetting?.contains(expectedComponentName.flattenToString()) ?: false
}

private fun isAccessibilitySettingsOn(context: Context): Boolean {
    val accessibilityEnabled = Settings.Secure.getInt(
        context.contentResolver,
        Settings.Secure.ACCESSIBILITY_ENABLED,
        0
    )
    if (accessibilityEnabled == 1) {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (!TextUtils.isEmpty(enabledServices)) {
            val packageName = context.packageName
            val serviceNames = enabledServices.split(":")
            for (serviceName in serviceNames) {
                val component = ComponentName.unflattenFromString(serviceName)
                if (component != null && TextUtils.equals(packageName, component.packageName)) {
                    return true
                }
            }
        }
    }
    return false
}

fun checkAccessibilityService(context: Context): Boolean {
    val isServiceEnabled =
        isAccessibilityServiceEnabled(context, MyAccessibilityService::class.java)
    val isSettingsOn = isAccessibilitySettingsOn(context)

    return isServiceEnabled && isSettingsOn
}


private fun requestAccessibilityPermission(context: Context) {
    if (!checkAccessibilityService(context)) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(context, intent, null)
    }
}

fun startService(context: Context) {
    // check if the user has already granted
    // the Draw over other apps permission
    if (checkAccessibilityService(context) && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // start the service based on the android version
        startForegroundService(
            context,
            Intent(
                context,
                MyAccessibilityService::class.java
            )
        )
    }
}


