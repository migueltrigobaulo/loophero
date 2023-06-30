package com.loopcorp.loophero

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import com.loopcorp.loophero.R
import kotlin.random.Random


class MyAccessibilityService : AccessibilityService() {

    private val titleArray = arrayListOf<String>()
    private val contentArray = arrayListOf<String>()


    private var counter = 0
    private var lastEventTime = 0L

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
        serviceInfo = info

        val notification = Notification.Builder(this, "my_channel_id")
            .setContentTitle(getString(R.string.im_watching_title))
            .setContentText(getString(R.string.im_watching_content))
            .setSmallIcon(R.drawable.shield)
            .build()

        startForeground(33, notification)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            if (event.packageName == "com.instagram.android" && event.className == "androidx.viewpager.widget.ViewPager") {
                checkAndCountScroll(event)
            } else if (event.packageName == "com.zhiliaoapp.musically" && event.className == "androidx.viewpager.widget.ViewPager") {
                checkAndCountScroll(event)
            } else if (event.packageName == "com.google.android.youtube" && event.className == "android.view.ViewGroup") {
                checkAndCountScroll(event)
            } else if (event.packageName != "com.instagram.android" && event.packageName != "com.zhiliaoapp.musically" && event.packageName != "com.google.android.youtube") {
                counter = 0
            }
            showMessageAndResetCounter()
        }
    }

    private fun showMessageAndResetCounter() {

        val sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        var maxReels = sharedPreferences.getInt("numericInput", 10)

        if (counter >= maxReels) {
            showNotification()
            counter = 0
        }
    }

    private fun checkAndCountScroll(event: AccessibilityEvent) {
        val verticalScrollDirection =
            event.scrollDeltaY
        if (event.eventTime - lastEventTime > 500) {
            if (verticalScrollDirection > 20) {
                // Registro de deslizar hacia arriba
                Log.d(
                    "com.loopcorp.loophero.MyAccessibilityService",
                    "Deslizar hacia arriba: $counter"
                )
                counter++
                lastEventTime = event.eventTime
            } else if (verticalScrollDirection < -20) {
                // Registro de deslizar hacia abajo
                counter++
                Log.d(
                    "com.loopcorp.loophero.MyAccessibilityService",
                    "Deslizar hacia abajo: $counter"
                )
                lastEventTime = event.eventTime
            }
        }
    }

    override fun onInterrupt() {
        Log.d("com.loopcorp.loophero.MyAccessibilityService", "SERVICE INTERRUPTED")
    }

    private fun showNotification() {

        val sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        titleArray.addAll(applicationContext.resources.getStringArray(R.array.title))
        contentArray.addAll(applicationContext.resources.getStringArray(R.array.content))

        var dropdownValue = sharedPreferences.getInt("dropdownValue", 0)

        val channelId = "my_channel_id"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val vibrationPattern =
            longArrayOf(0, 500, 200, 500) // Patrón de vibración (pausa, duración, pausa, duración)

        val randomIndex = Random.nextInt(titleArray.size)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(titleArray[randomIndex])
            .setContentText(contentArray[randomIndex])
            .setSmallIcon(R.drawable.shield)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE) // Configurar la vibración por defecto
            .setVibrate(vibrationPattern) // Asignar el patrón de vibración (solo para versiones anteriores a Android 8.0)
            .build()

        notificationManager.notify(0, notification)
    }

    private fun showPopup(message: String) {
        // Create and display your popup
        // This can be achieved by using a system alert window or by launching an activity with a transparent background

        // Example: Using a system alert window
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_layout, null)
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(popupView, params)
    }

}