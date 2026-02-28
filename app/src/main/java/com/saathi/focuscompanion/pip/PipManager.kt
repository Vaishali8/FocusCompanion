package com.saathi.focuscompanion.pip

import android.app.Activity
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Icon
import android.util.Rational
import com.saathi.focuscompanion.MainActivity

object PipManager {

    const val ACTION_RETURN_FROM_PIP = "com.saathi.ACTION_RETURN_FROM_PIP"

    fun enterStudyPip(activity: Activity, companionName: String) {
        val returnAction = RemoteAction(
            Icon.createWithBitmap(createReturnIconBitmap()),
            "Wapas Chalein",
            "Return to $companionName",
            PendingIntent.getActivity(
                activity, 0,
                Intent(activity, MainActivity::class.java).apply {
                    action = ACTION_RETURN_FROM_PIP
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(1, 1))
            .setActions(listOf(returnAction))
            .build()

        activity.enterPictureInPictureMode(params)
    }

    private fun createReturnIconBitmap(): Bitmap {
        val size = 48
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.WHITE
            isAntiAlias = true
            strokeWidth = 4f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        // Draw a back arrow (<-)
        val path = Path().apply {
            // Arrow head
            moveTo(20f, 10f)
            lineTo(8f, 24f)
            lineTo(20f, 38f)
            // Arrow body
            moveTo(8f, 24f)
            lineTo(40f, 24f)
        }
        canvas.drawPath(path, paint)

        return bitmap
    }
}
