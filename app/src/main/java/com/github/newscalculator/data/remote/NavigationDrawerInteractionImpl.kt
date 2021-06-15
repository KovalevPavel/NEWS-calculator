package com.github.newscalculator.data.remote

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.github.newscalculator.R
import com.github.newscalculator.data.NavigationDrawerInteraction
import com.github.newscalculator.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NavigationDrawerInteractionImpl : NavigationDrawerInteraction {
    companion object {
        private const val EMAIL_ADDRESS = "inere8@gmail.com"
        private const val EMAIL_SUBJECT = "Bugreport/NEWS-calculator"
    }

    override fun shareApp(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            val string = "${activity.resources.getString(R.string.share_String)}\n " +
                    "${Uri.parse("https://play.google.com/store/apps/details?id=${activity.packageName}")}"
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, string)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, null)
            activity.startActivity(shareIntent)
        }
    }

    override fun rateApp(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            val packageName = activity.packageName
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
                activity.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
                activity.startActivity(intent)
            }
        }
    }

    override fun requestHelp() {
        TODO("Not yet implemented")
    }

    override fun reportBug(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT)
                data = Uri.parse("mailto:$EMAIL_ADDRESS")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                activity.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val string = activity.resources.getString(R.string.noEmailClient)
                showToast(activity, string)
            }
        }
    }
}