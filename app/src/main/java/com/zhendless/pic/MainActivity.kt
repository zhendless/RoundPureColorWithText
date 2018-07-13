package com.zhendless.pic

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val mColorList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "start generating pure color pic!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            checkPermissionWriteExternalStorage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun generatePicture(letter: String, color: String) {
        val bitmap = Bitmap.createBitmap(PICTURE_HEIGHT, PICTURE_HEIGHT,
                Bitmap.Config.ARGB_8888)
        try {
            bitmap.eraseColor(Color.parseColor(color))//填充颜色
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.textSize = TEXT_SIZE
            paint.color = Color.WHITE
            paint.flags = 1
            paint.style = Paint.Style.FILL_AND_STROKE
            val fontMetrics = paint.fontMetrics
            val fontHeight = fontMetrics.bottom - fontMetrics.top
            val textBaseY = PICTURE_HEIGHT - (PICTURE_HEIGHT - fontHeight) / 2 - fontMetrics.bottom
            val textBaseX = (PICTURE_HEIGHT - paint.measureText(letter)) / 2
            canvas.drawText(letter, textBaseX, textBaseY, paint)
            saveBitmap(bitmap, color + "_" + letter)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Toast.makeText(this, "parse color error!", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveBitmap(bitmap: Bitmap, fileName: String) {
        val savePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + packageName + File.separator
        try {
            val filePic = File("$savePath$fileName.jpg")
            if (!filePic.exists()) {
                filePic.parentFile.mkdirs()
                filePic.createNewFile()
            }
            val fos = FileOutputStream(filePic)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private inner class DrawPicThread : Thread() {
        override fun run() {
            super.run()
            for (colorStr in mColorList) {
                var letter = 'A'
                while (letter <= 'Z') {
                    generatePicture(letter.toString(), colorStr)
                    letter++
                }
            }
        }
    }

    fun checkPermissionWriteExternalStorage() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (PackageManager.PERMISSION_GRANTED != permissionCheck) {
                requestPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        getString(R.string.is_permission_rationale_write_storage),
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
            } else {
                onWriteExternalStorageGranted()
            }
        }
    }

    private fun requestPermission(activity: Activity, permission: String, rationale: String, requestCode: Int) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            AlertDialog.Builder(activity)
                    .setTitle(R.string.dialog_tips_is_permission)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.dialog_button_ok, DialogInterface.OnClickListener { _, _ -> ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode) })
                    .setNegativeButton(R.string.dialog_button_cancel, null)
                    .create().show()
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onWriteExternalStorageGranted()
                } else {
                    onWriteExternalStorageDenied()
                }
            }
        }
    }

    private fun onWriteExternalStorageDenied() {
        Toast.makeText(this, "need permission to save picture!", Toast.LENGTH_LONG).show()
    }

    private fun onWriteExternalStorageGranted() {
        addColorIntoList()
        DrawPicThread().start()
    }

    private fun addColorIntoList() {
        mColorList.clear()
        mColorList.add(COLOR_HEX_RED)
        mColorList.add(COLOR_HEX_PINK)
        mColorList.add(COLOR_HEX_PURPLE)

        mColorList.add(COLOR_HEX_DEEP_PURPLE)
        mColorList.add(COLOR_HEX_INDIGO)
        mColorList.add(COLOR_HEX_BLUE)

        mColorList.add(COLOR_HEX_LIGHT_BLUE)
        mColorList.add(COLOR_HEX_CYAN)
        mColorList.add(COLOR_HEX_TEAL)

        mColorList.add(COLOR_HEX_GREEN)
        mColorList.add(COLOR_HEX_LIGHT_GREEN)
        mColorList.add(COLOR_HEX_LIME)

        mColorList.add(COLOR_HEX_YELLOW)
        mColorList.add(COLOR_HEX_AMBER)
        mColorList.add(COLOR_HEX_ORANGE)

        mColorList.add(COLOR_HEX_DEEP_ORANGE)
        mColorList.add(COLOR_HEX_BROWN)
        mColorList.add(COLOR_HEX_GREY)

        mColorList.add(COLOR_HEX_BLUE_GREY)
        mColorList.add(COLOR_HEX_BLACK)
    }

    companion object {
        private const val PICTURE_HEIGHT = 300
        private const val TEXT_SIZE = 180f
        private const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 998
        private const val COLOR_HEX_RED = "#F44336"
        private const val COLOR_HEX_PINK = "#E91E63"
        private const val COLOR_HEX_PURPLE = "#9C27B0"

        private const val COLOR_HEX_DEEP_PURPLE = "#673AB7"
        private const val COLOR_HEX_INDIGO = "#3F51B5"
        private const val COLOR_HEX_BLUE = "#2196F3"

        private const val COLOR_HEX_LIGHT_BLUE = "#03A9F4"
        private const val COLOR_HEX_CYAN = "#00BCD4"
        private const val COLOR_HEX_TEAL = "#009688"

        private const val COLOR_HEX_GREEN = "#4CAF50"
        private const val COLOR_HEX_LIGHT_GREEN = "#8BC34A"
        private const val COLOR_HEX_LIME = "#CDDC39"

        private const val COLOR_HEX_YELLOW = "#FFEB3B"
        private const val COLOR_HEX_AMBER = "#FFC107"
        private const val COLOR_HEX_ORANGE = "#FF9800"

        private const val COLOR_HEX_DEEP_ORANGE = "#FF5722"
        private const val COLOR_HEX_BROWN = "#795548"
        private const val COLOR_HEX_GREY = "#9E9E9E"

        private const val COLOR_HEX_BLUE_GREY = "#607D8B"
        private const val COLOR_HEX_BLACK = "#000000"

    }
}
