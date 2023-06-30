package com.loopcorp.loophero

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import com.loopcorp.loophero.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    var numericInputValue by remember {
        mutableStateOf(
            sharedPreferences.getInt("numericInput", 10)
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar = remember { mutableStateOf(false) }

    val changesSavedString = stringResource(R.string.changes_saved)

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.fillMaxSize(), content = {
                LaunchedEffect(showSnackbar.value) {
                    if (showSnackbar.value) {
                        snackbarHostState.showSnackbar(
                            message = changesSavedString,
                            duration = SnackbarDuration.Short
                        )
                        showSnackbar.value = false
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(it)
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
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
                            modifier = Modifier.height(150.dp)
                        )

                        PermissionMessage()

                        SliderWithLabel(
                            value = numericInputValue.toFloat(), onRadiusChange = { newValue ->
                                numericInputValue = newValue.toFloat().toInt()
                            },
                            valueRange = 4f..40f
                        )

                        Spacer(modifier = Modifier.fillMaxHeight(.4f))


                        Button(
                            onClick = {
                                sharedPreferences.edit()
                                    .putInt("numericInput", numericInputValue)
                                    .apply()
                                showSnackbar.value = !showSnackbar.value
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 15.dp)
                        ) {
                            Icon(
                                Icons.Default.Done,
                                contentDescription = stringResource(R.string.save)
                            )
                            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                            Text(text = stringResource(R.string.save))
                        }


                        Spacer(modifier = Modifier.height(100.dp))

                    }
                }
            })
    }
}

@Composable
fun SliderWithLabel(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    labelMinWidth: Dp = 24.dp,
    onRadiusChange: (String) -> Unit
) {

    val primary = MaterialTheme.colorScheme.primary.toArgb()
    val onPrimary = MaterialTheme.colorScheme.onPrimary.toArgb()
    var color by remember {
        mutableStateOf(
            androidx.core.graphics.ColorUtils.blendARGB(
                primary,
                Color.Red.toArgb(),
                calcFractionExp(0f, 40f, value)
            )
        )
    }
    var fontColor by remember { mutableStateOf(if (value < 27) onPrimary else Color.White.toArgb()) }

    Column(modifier = Modifier.padding(horizontal = 15.dp)) {

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            val offset = getSliderOffset(
                value = value,
                valueRange = valueRange,
                boxWidth = maxWidth,
                labelWidth = labelMinWidth + 8.dp // Since we use a padding of 4.dp on either sides of the SliderLabel, we need to account for this in our calculation
            )

            val endValueText = value.toInt().toString()


            SliderLabel(
                color = color, fontColor = fontColor,
                label = endValueText, minWidth = labelMinWidth, modifier = Modifier
                    .padding(start = offset)
            )

        }

        Slider(
            colors = SliderDefaults.colors(
                thumbColor = Color(color),
                activeTrackColor = Color(color)
            ),
            value = value, onValueChange = {
                onRadiusChange(it.toString())
                color = androidx.core.graphics.ColorUtils.blendARGB(
                    primary,
                    Color.Red.toArgb(),
                    calcFractionExp(0f, 40f, it)
                )
                fontColor = if (it < 27) onPrimary else Color.White.toArgb()
            },
            valueRange = valueRange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        )

    }
}


@Composable
fun SliderLabel(
    label: String,
    minWidth: Dp,
    modifier: Modifier = Modifier,
    color: Int,
    fontColor: Int
) {
    Text(
        label,
        textAlign = TextAlign.Center,
        color = Color(fontColor),
        modifier = modifier
            .background(
                color = Color(color),
                shape = CircleShape
            )
            .padding(4.dp)
            .defaultMinSize(minWidth = minWidth)
    )
}


private fun getSliderOffset(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    boxWidth: Dp,
    labelWidth: Dp
): Dp {

    val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val positionFraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)

    return (boxWidth - labelWidth) * positionFraction
}


// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

private fun calcFractionExp(a: Float, b: Float, pos: Float): Float {
    val exponentialFactor = 3.5f // Exponential factor to control the rate of change

    val normalizedPos = (pos - a) / (b - a) // Normalize the position between a and b

    val fraction = Math.pow(normalizedPos.toDouble(), exponentialFactor.toDouble()).toFloat()
    return fraction.coerceIn(0f, 1f) // Clamp the fraction between 0 and 1
}

@Composable
private fun PermissionMessage() {

    val lifecycleState =
        LocalLifecycleOwner.current.lifecycle.observeAsState()
    val state = lifecycleState.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.configuration),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.configure_reels_counter),
            style = TextStyle(fontSize = 14.sp),
            textAlign = TextAlign.Center
        )
    }
}