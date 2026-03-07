package com.hanly.pw

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hanly.pw.R
import com.hanly.pw.ui.MainActivityLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        // Modern color palette
        android.R.color.white,                // White (default)
        android.R.color.holo_blue_light,      // Blue
        android.R.color.holo_green_light,     // Green
        android.R.color.holo_orange_light,    // Orange
        android.R.color.holo_red_light,       // Red
        android.R.color.holo_purple,          // Purple
        R.color.primary,                      // Custom primary blue
        R.color.secondary,                    // Custom secondary green
        R.color.accent,                       // Custom accent orange
        android.R.color.holo_blue_dark,       // Dark blue
        android.R.color.holo_green_dark,      // Dark green
        android.R.color.holo_red_dark,        // Dark red
        android.R.color.darker_gray           // Dark gray (replacement for holo_purple_dark)
    )

    private lateinit var mainLayout: MainActivityLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        mainLayout = MainActivityLayout(this)
        setContentView(mainLayout)
        
        backgroundManager = BackgroundManager(this)
        backgroundManager.registerChangeListener(backgroundChangeListener)
        reloadBackground()

        randomStringEditText = mainLayout.randomStringEditText
        lengthTextView = mainLayout.lengthTextView
        lengthSeekBar = mainLayout.lengthSeekBar
        val generateButton = mainLayout.generateButton
        val settingsButton = mainLayout.settingsButton

        randomStringEditText.setText("点击生成按钮获取随机字符")
        randomStringEditText.isEnabled = true
        randomStringEditText.isFocusable = false
        randomStringEditText.isClickable = true
        randomStringEditText.setOnClickListener { 
            val text = randomStringEditText.text.toString()
            if (text != "点击生成按钮获取随机字符") {
                copyToClipboard(text)
            }
        }
        
        lengthSeekBar.min = 8
        lengthSeekBar.max = 32
        lengthSeekBar.progress = stringLength
        setColorfulLengthText(stringLength)

        lengthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                stringLength = progress
                setColorfulLengthText(progress)
                // Add animation for length text change
                lengthTextView.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(100)
                    .withEndAction { 
                        lengthTextView.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                    }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        generateButton.setOnClickListener { 
            // Add button press animation
            generateButton.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction { 
                    generateButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                }
            generateRandomString()
        }
        
        settingsButton.setOnClickListener { 
            // Add button press animation
            settingsButton.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction { 
                    settingsButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction { 
                            startActivity(Intent(this, SettingsActivity::class.java))
                        }
                }
        }

        setupSystemUI()
        // Add entrance animation for all elements
        startEntranceAnimations()
    }
    
    private fun startEntranceAnimations() {
        // Animate title - find first TextView child
        val titleTextView = mainLayout.getChildAt(0) as? android.widget.TextView ?: return
        titleTextView.alpha = 0f
        titleTextView.translationY = -50f
        titleTextView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(100)
            .start()
        
        // Animate random string card
        val randomStringCard = mainLayout.randomStringCard
        randomStringCard.alpha = 0f
        randomStringCard.translationY = 50f
        randomStringCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(200)
            .start()
        
        // Animate length card
        val lengthCard = mainLayout.lengthCard
        lengthCard.alpha = 0f
        lengthCard.translationY = 50f
        lengthCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(300)
            .start()
        
        // Animate buttons
        val generateButton = mainLayout.generateButton
        generateButton.alpha = 0f
        generateButton.translationX = -50f
        generateButton.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(500)
            .setStartDelay(400)
            .start()
        
        val settingsButton = mainLayout.settingsButton
        settingsButton.alpha = 0f
        settingsButton.translationX = 50f
        settingsButton.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(500)
            .setStartDelay(400)
            .start()
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
    
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reload background when system theme changes
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
                backgroundImageUri = uri
                isUsingImageBackground = true
                if (isImageChanged) {
                    backgroundImageTimestamp = System.currentTimeMillis()
                }
                android.util.Log.d("MainActivity", "Background image file found, setting isUsingImageBackground to true")
            } else {
                backgroundImageUri = null
                isUsingImageBackground = false
                if (isImageChanged) {
                    backgroundImageTimestamp = System.currentTimeMillis()
                }
                android.util.Log.d("MainActivity", "No background image file found, setting isUsingImageBackground to false")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            backgroundImageUri = null
            isUsingImageBackground = false
            if (isImageChanged) {
                backgroundImageTimestamp = System.currentTimeMillis()
            }
            android.util.Log.e("MainActivity", "Error checking background image file: ${e.message}")
        }

        // 先设置一个默认的颜色背景，避免黑屏
        val colorIndex = backgroundManager.colorIndex
        backgroundColorId = backgroundColors[colorIndex % backgroundColors.size]
        
        // If not using image background, remove any existing image view
        if (!isUsingImageBackground) {
            removeBackgroundImageView()
            window.decorView.setBackgroundResource(backgroundColorId)
        }

        // 如果使用图片背景，再在后台线程中加载图片
        if (isUsingImageBackground && backgroundImageUri != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val file = File(filesDir, "background_image.jpg")
                    android.util.Log.d("MainActivity", "Loading background image from: ${file.absolutePath}")
                    android.util.Log.d("MainActivity", "File exists: ${file.exists()}, file size: ${file.length()} bytes")
                    
                    val options = android.graphics.BitmapFactory.Options()
                    options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
                    options.inDither = true
                    val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath, options)
                    
                    if (bitmap != null) {
                        android.util.Log.d("MainActivity", "Image loaded successfully, width: ${bitmap.width}, height: ${bitmap.height}")
                        withContext(Dispatchers.Main) {
                            // Remove any existing image view first
                            removeBackgroundImageView()
                            
                            // Create a full screen background using ScaleType.CENTER_CROP
                            val imageView = ImageView(this@MainActivity)
                            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                            imageView.setImageBitmap(bitmap)
                            
                            // Create a layout params to fill the entire screen
                            val params = WindowManager.LayoutParams(
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT
                            )
                            
                            // Add the image view as the first child of decor view
                            (window.decorView as ViewGroup).addView(imageView, 0, params)
                            
                            // Set background to transparent to show our image view
                            window.decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        }
                    } else {
                        android.util.Log.e("MainActivity", "Failed to decode bitmap from file")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    android.util.Log.e("MainActivity", "Error loading background image: ${e.message}")
                    // 保持当前的颜色背景
                }
            }
        }

        if (isImageChanged) {
            backgroundManager.clearImageChanged()
        }
    }

    private fun removeBackgroundImageView() {
        // Remove the image view if it exists
        val decorView = window.decorView as ViewGroup
        if (decorView.childCount > 0) {
            val firstChild = decorView.getChildAt(0)
            if (firstChild is ImageView) {
                decorView.removeView(firstChild)
            }
        }
    }

    // Character set constant - simplified
    private val CHARACTER_SET = buildString {
        append(('A'..'Z').joinToString("")) // Uppercase letters
        append(('a'..'z').joinToString("")) // Lowercase letters
        append(('0'..'9').joinToString("")) // Numbers
        append("!@#$%^&*()_+-=[]{}|;:,.<>") // Special characters
    }

    private fun generateRandomString() {
        CoroutineScope(Dispatchers.IO).launch {
            val maxLength = minOf(stringLength, CHARACTER_SET.length)
            val random = Random()
            val result = buildString {
                val usedChars = mutableSetOf<Char>()
                repeat(maxLength) {
                    var char: Char
                    do {
                        char = CHARACTER_SET[random.nextInt(CHARACTER_SET.length)]
                    } while (usedChars.contains(char))
                    usedChars.add(char)
                    append(char)
                }
            }
            withContext(Dispatchers.Main) {
                // Set colorful text
                val spannable = android.text.SpannableString(result)
                val colors = intArrayOf(
                    android.graphics.Color.RED,
                    android.graphics.Color.BLUE,
                    android.graphics.Color.GREEN,
                    android.graphics.Color.YELLOW,
                    android.graphics.Color.MAGENTA,
                    android.graphics.Color.CYAN
                )
                for (i in 0 until result.length) {
                    val color = colors[i % colors.size]
                    spannable.setSpan(
                        android.text.style.ForegroundColorSpan(color),
                        i, i + 1, 0
                    )
                }
                randomStringEditText.setText(spannable)
            }
        }
    }

    private fun copyToClipboard(text: String) {
        if (text == "点击生成按钮获取随机字符") return

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("RandomString", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
    }

    private fun setColorfulLengthText(length: Int) {
        val text = "长度: $length"
        val spannable = android.text.SpannableString(text)
        val colors = intArrayOf(
            android.graphics.Color.RED,
            android.graphics.Color.BLUE,
            android.graphics.Color.GREEN,
            android.graphics.Color.YELLOW,
            android.graphics.Color.MAGENTA,
            android.graphics.Color.CYAN
        )
        for (i in 0 until text.length) {
            val color = colors[i % colors.size]
            spannable.setSpan(
                android.text.style.ForegroundColorSpan(color),
                i, i + 1, 0
            )
        }
        lengthTextView.text = spannable
    }
}
