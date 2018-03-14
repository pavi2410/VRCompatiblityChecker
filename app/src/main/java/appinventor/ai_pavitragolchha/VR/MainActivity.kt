package appinventor.ai_pavitragolchha.VR

import android.graphics.*
import android.hardware.Sensor
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val SHARE = "\uF1E0"
        const val THUMBS_UP = "\uF164"
        const val THUMBS_DOWN = "\uF165;"
        const val NEUTRAL_FACE = "\uF11A;"
        const val CHECK = "\uF00C"
        const val CROSS = "\uF00D"
        const val SUCCESS_COMMENT = "Congratulations!!! Your device fully supports VR"
        const val SUCCESS_PARTIAL_COMMENT = "It's OK! Your device supports limited features of VR"
        const val FAIL_COMMENT = "Oops! Your device doesn't support VR"
        const val ROBOTO_THIN = "Roboto-Thin.ttf"
        const val FONTAWESOME = "fontawesome-webfont.ttf"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setImageBitmap(textToBitmap(Companion.SHARE, 40f, Color.parseColor("#ff9800")))

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Share Function Coming Soon!", Snackbar.LENGTH_SHORT).show()
        }

        fab.setOnLongClickListener {
            Toast.makeText(this, "Share Result", Toast.LENGTH_SHORT).show()
            true
        }

        val tvs1 = listOf(resultComment, accText, gyroText, compassText, screenSizeText,
                screenSizeResult, screenSizeText, screenResResult, androidText, ramText)
        for (tv in tvs1)
            tv.typeface = Typeface.createFromAsset(assets, Companion.ROBOTO_THIN)

        val tvs2 = listOf(resultIcon, accResult, gyroResult, compassResult, androidResult, ramResult)
        for (tv in tvs2)
            tv.typeface = Typeface.createFromAsset(assets, Companion.FONTAWESOME)

        checkButton.setOnClickListener {
            checkButton.visibility = View.GONE
            resultBox.visibility = View.VISIBLE
            fab.visibility = View.VISIBLE

            init()
        }
    }

    fun init() {
        val result = check()
        resultIcon.text = when (result) {
            0 -> Companion.THUMBS_UP
            1 -> Companion.THUMBS_DOWN
            2 -> Companion.NEUTRAL_FACE
            else -> "error"
        }
        resultComment.text = when (result) {
            0 -> Companion.SUCCESS_COMMENT
            1 -> Companion.SUCCESS_PARTIAL_COMMENT
            2 -> Companion.FAIL_COMMENT
            else -> "error"
        }

        val pi = PhoneInfo(this)

        accResult.text = if (pi.checkSensor(Sensor.TYPE_ACCELEROMETER)) Companion.CHECK else Companion.CROSS
        gyroResult.text = if (pi.checkSensor(Sensor.TYPE_GYROSCOPE)) Companion.CHECK else Companion.CROSS
        compassResult.text = if (pi.checkSensor(Sensor.TYPE_MAGNETIC_FIELD)) Companion.CHECK else Companion.CROSS

        screenSizeResult.text = "${"%.1f".format(pi.getScreenSize())}\""

        val (w, h) = pi.getScreenRes()
        screenResResult.text = "${w}x$h"

        androidResult.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) Companion.CHECK else Companion.CROSS

        ramResult.text = if (pi.getRam() > 1.5 * 1024 * 1024 * 1024) Companion.CHECK else Companion.CROSS
    }

    fun check(): Int {
        val pi = PhoneInfo(this)

        val acc = pi.checkSensor(Sensor.TYPE_ACCELEROMETER)
        val gyro = pi.checkSensor(Sensor.TYPE_GYROSCOPE)
        val compass = pi.checkSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val android = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        return when {
            acc && gyro && compass && android -> 0 // All things available
            (acc || gyro || compass) && android -> 1 // Something is available
            else -> 2 // Nothing is available
        }
    }

    private fun textToBitmap(text: String, textSize: Float, textColor: Int): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.createFromAsset(assets, Companion.FONTAWESOME)
        val baseline = - paint.ascent()
        val w = paint.measureText(text)
        val h = paint.descent() + baseline

        val image = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(image)
        canvas.drawText(text,0f, baseline, paint)

        return image
    }
}
