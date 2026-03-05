package com.hanly.pw

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hanly.pw.R
import java.io.File
import java.util.Random

public class MainActivity : AppCompatActivity() {
    private lateinit var backgroundManager: BackgroundManager
    private val backgroundChangeListener = SharedPreferences.OnSharedPreferenceChangeListener {
            _, key ->
        if (key == BackgroundManager.KEY_IMAGE_CHANGED || key == BackgroundManager.KEY_COLOR_INDEX) {
            reloadBackground()
        }
    }

    private lateinit var randomStringEditText: EditText
    private lateinit var lengthTextView: TextView
    private lateinit var lengthSeekBar: SeekBar
    private var stringLength = 16
    private var isUsingImageBackground = false
    private var backgroundImageUri: String? = null
    private var backgroundImageTimestamp = 0L
    private var backgroundColorId = 0
    private val backgroundColors = arrayOf(
        android.R.color.holo_blue_light,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light,
        android.R.color.holo_purple
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        backgroundManager = BackgroundManager(this)
        backgroundManager.registerChangeListener(backgroundChangeListener)
        reloadBackground()

        randomStringEditText = findViewById(R.id.randomStringEditText)
        lengthTextView = findViewById(R.id.lengthTextView)
        lengthSeekBar = findViewById(R.id.lengthSeekBar)
        val generateButton = findViewById<Button>(R.id.generateButton)
        val copyButton = findViewById<Button>(R.id.copyButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)

        randomStringEditText.setText("点击生成按钮获取随机字符")
        randomStringEditText.isEnabled = false
        
        lengthSeekBar.min = 8
        lengthSeekBar.max = 32
        lengthSeekBar.progress = stringLength
        lengthTextView.text = "长度: $stringLength"

        lengthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                stringLength = progress
                lengthTextView.text = "长度: $stringLength"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        generateButton.setOnClickListener { generateRandomString() }
        copyButton.setOnClickListener { copyToClipboard(randomStringEditText.text.toString()) }
        settingsButton.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }

        setupSystemUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundManager.unregisterChangeListener(backgroundChangeListener)
    }

    override fun onResume() {
        super.onResume()
        setupSystemUI()
        reloadBackground()
    }

    private fun setupSystemUI() {
        window.insetsController?.apply {
            hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
            systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun reloadBackground() {
        val isImageChanged = backgroundManager.isImageChanged
        try {
            val file = File(filesDir, "background_image.jpg")
            if (file.exists()) {
                val uri = Uri.fromFile(file).toString()
                if (isImageChanged || backgroundImageUri != uri) {
                    backgroundImageUri = uri
                    isUsingImageBackground = true
                    if (isImageChanged) {
                        backgroundImageTimestamp = System.currentTimeMillis()
                    }
                }
            } else if (isUsingImageBackground || isImageChanged) {
                backgroundImageUri = null
                isUsingImageBackground = false
                if (isImageChanged) {
                    backgroundImageTimestamp = System.currentTimeMillis()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            backgroundImageUri = null
            isUsingImageBackground = false
            if (isImageChanged) {
                backgroundImageTimestamp = System.currentTimeMillis()
            }
        }

        val colorIndex = backgroundManager.colorIndex
        backgroundColorId = backgroundColors[colorIndex % backgroundColors.size]
        window.decorView.setBackgroundResource(backgroundColorId)

        if (isImageChanged) {
            backgroundManager.clearImageChanged()
        }
    }

    private fun generateRandomString() {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?"
        val maxLength = Math.min(stringLength, chars.length)
        val random = Random()
        val sb = StringBuilder(maxLength)
        val usedChars = mutableSetOf<Char>()

        for (i in 0 until maxLength) {
            var char: Char
            do {
                char = chars[random.nextInt(chars.length)]
            } while (usedChars.contains(char))
            usedChars.add(char)
            sb.append(char)
        }

        randomStringEditText.setText(sb.toString())
    }

    private fun copyToClipboard(text: String) {
        if (text == "点击生成按钮获取随机字符") return

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("RandomString", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
    }
}
