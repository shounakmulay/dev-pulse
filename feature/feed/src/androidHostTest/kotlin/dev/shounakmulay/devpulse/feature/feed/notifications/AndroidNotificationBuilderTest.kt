package dev.shounakmulay.devpulse.feature.feed.notifications

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import dev.shounakmulay.devpulse.core.notifications.AndroidNotificationBuilder
import dev.shounakmulay.devpulse.core.notifications.AndroidNotificationContent
import dev.shounakmulay.devpulse.core.notifications.DPNotificationChannel
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [35])
class AndroidNotificationBuilderTest {
    @Test
    fun `Given message or progress notification When tapped Then the app launcher receives the intent`() {
        val context = RuntimeEnvironment.getApplication() as Application
        val launchIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.packageName)
        shadowOf(context.packageManager).addResolveInfoForIntent(launchIntent, ResolveInfo().apply {
            activityInfo = ActivityInfo().apply {
                packageName = context.packageName
                name = "dev.shounakmulay.devpulse.MainActivity"
            }
        })
        val builder = AndroidNotificationBuilder(context)
        val content = AndroidNotificationContent(channel = DPNotificationChannel.FEED_DATA_SYNC, title = "Updates", text = "New articles")
        for (notification in listOf(builder.createMessageNotification(content), builder.createProgressNotification(content))) {
            val pendingIntent = assertNotNull(notification.contentIntent)
            assertTrue(pendingIntent.isImmutable)
            assertTrue(pendingIntent.isActivity)
            pendingIntent.send()
            val started = shadowOf(context).nextStartedActivity
            assertEquals("dev.shounakmulay.devpulse.MainActivity", started.component?.className)
            assertEquals(Intent.ACTION_MAIN, started.action)
            assertTrue(Intent.CATEGORY_LAUNCHER in started.categories)
            assertTrue(started.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
        }
    }

    @Test
    fun `Given a custom tap action When building a notification Then it is preserved`() {
        val context = RuntimeEnvironment.getApplication() as Application
        val customIntent = PendingIntent.getActivity(
            context, 42, Intent("custom").setPackage(context.packageName), PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = AndroidNotificationBuilder(context).createMessageNotification(
            AndroidNotificationContent(
                channel = DPNotificationChannel.FEED_DATA_SYNC,
                title = "Updates",
                text = "New articles",
                contentIntent = customIntent,
            ),
        )
        assertEquals(customIntent, notification.contentIntent)
    }
}
