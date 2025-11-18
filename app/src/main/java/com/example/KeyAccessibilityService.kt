package com.example.smartkeywake

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import android.view.KeyEvent
import android.content.Context
import android.util.Log

class KeyAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "KeyAccessibilityService"
        private val SUPPORTED_KEYCODES = intArrayOf(
            KeyEvent.KEYCODE_BUTTON_1,
            KeyEvent.KEYCODE_BUTTON_2,
            KeyEvent.KEYCODE_ASSIST,
            KeyEvent.KEYCODE_F1,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            301 // <-- твой smart key
        )
    }

    override fun onServiceConnected() {
        Log.i(TAG, "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val code = event.keyCode
            Log.i(TAG, "Key event keyCode=$code")
            if (SUPPORTED_KEYCODES.contains(code)) {
                wakeScreen()
                return true
            }
        }
        return super.onKeyEvent(event)
    }

    private fun wakeScreen() {
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    pm.wakeUp(SystemClock.uptimeMillis())
                    Log.i(TAG, "PowerManager.wakeUp() called")
                    return
                } catch (e: Exception) {
                    Log.w(TAG, "wakeUp() failed: ${e.message}")
                }
            }

            val wl = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "SmartKeyWake:WakeLockTag"
            )
            wl.acquire(1000)
            Log.i(TAG, "WakeLock acquired")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to wake screen: ${e.message}")
        }
    }
}
