package com.hanly.pw

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isNotEmpty
import androidx.activity.result.contract.ActivityResultContracts
import com.hanly.pw.R
import com.hanly.pw.ui.SettingsActivityLayout
import java.io.File
import java.io.FileOutputStream

public class SettingsActivity : AppCompatActivity() {
    private lateinit var backgroundManager: BackgroundManager
    private var colorIndex = 0
    private var isUsingImageBackground = false
    private var backgroundImageUri: String? = null
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

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        android.util.Log.d("SettingsActivity", "Image picker result: ${result.resultCode}")
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            android.util.Log.d("SettingsActivity", "Image picker data: $data")
            val uri = data?.data
            android.util.Log.d("SettingsActivity", "Image picker URI: $uri")
            if (uri != null) {
                backgroundImageUri = uri.toString()
                isUsingImageBackground = true
                saveImageToPrivateDir(uri)
            } else {
                android.util.Log.e("SettingsActivity", "No URI returned from image picker")
                Toast.makeText(this, "未选择图片", Toast.LENGTH_SHORT).show()
            }
        } else {
            android.util.Log.d("SettingsActivity", "Image picker cancelled or failed")
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                android.Manifest.permission.READ_MEDIA_IMAGES
            } else {
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            }
            if (shouldShowRequestPermissionRationale(permission)) {
                Toast.makeText(this, "需要访问相册权限才能选择图片", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "权限被拒绝。请在设置中手动开启相册访问权限", Toast.LENGTH_LONG).show()
            }
        }
    }

    private lateinit var settingsLayout: SettingsActivityLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        settingsLayout = SettingsActivityLayout(this)
        setContentView(settingsLayout)
        
        backgroundManager = BackgroundManager(this)
        loadSavedBackground()
        loadSavedBackgroundColor()

        val changeBackgroundButton = settingsLayout.changeBackgroundButton
        val selectImageButton = settingsLayout.selectImageButton
        val restoreDefaultButton = settingsLayout.restoreDefaultButton
        val backButton = settingsLayout.backButton

        updateChangeBackgroundButtonText()

        changeBackgroundButton.setOnClickListener { changeBackground() }
        selectImageButton.setOnClickListener { checkAndRequestPermission() }
        restoreDefaultButton.setOnClickListener { restoreDefaultBackground() }
        backButton.setOnClickListener { finish() }

        setupSystemUI()
    }

    override fun onResume() {
        super.onResume()
        setupSystemUI()
        loadSavedBackground()
        loadSavedBackgroundColor()
        updateChangeBackgroundButtonText()
    }
    
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reload background when system theme changes
        loadSavedBackground()
        loadSavedBackgroundColor()
        updateChangeBackgroundButtonText()
    }

    private fun setupSystemUI() {
        window.insetsController?.apply {
            hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
            systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun loadSavedBackground() {
        try {
            val file = File(filesDir, "background_image.jpg")
            if (file.exists()) {
                backgroundImageUri = Uri.fromFile(file).toString()
                isUsingImageBackground = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadSavedBackgroundColor() {
        colorIndex = backgroundManager.colorIndex
        backgroundColorId = backgroundColors[colorIndex % backgroundColors.size]
        
        // Check if we should use image background
        try {
            val file = File(filesDir, "background_image.jpg")
            if (file.exists()) {
                val options = android.graphics.BitmapFactory.Options()
                options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
                options.inDither = true
                val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath, options)
                if (bitmap != null) {
                    // Remove any existing image view first
                    removeBackgroundImageView()
                    
                    // Create a full screen background using ScaleType.CENTER_CROP
                    val imageView = ImageView(this@SettingsActivity)
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
                    
                    isUsingImageBackground = true
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Fall back to color background
        removeBackgroundImageView()
        window.decorView.setBackgroundResource(backgroundColorId)
        isUsingImageBackground = false
    }

    private fun removeBackgroundImageView() {
        // Remove the image view if it exists
        val decorView = window.decorView as ViewGroup
        if (decorView.isNotEmpty()) {
            val firstChild = decorView.getChildAt(0)
            if (firstChild is ImageView) {
                decorView.removeView(firstChild)
            }
        }
    }

    private fun changeBackground() {
        // Always delete background image file when switching to color background
        backgroundImageUri = null
        isUsingImageBackground = false
        val file = File(filesDir, "background_image.jpg")
        if (file.exists()) {
            file.delete()
            backgroundManager.markImageChanged()
        }

        colorIndex = (colorIndex + 1) % backgroundColors.size
        backgroundColorId = backgroundColors[colorIndex]
        
        // Remove any existing image view and set color background
        removeBackgroundImageView()
        window.decorView.setBackgroundResource(backgroundColorId)
        
        backgroundManager.saveColorIndex(colorIndex)
        updateChangeBackgroundButtonText()
        android.util.Log.d("SettingsActivity", "Background color changed to index: $colorIndex, colorId: $backgroundColorId")
    }

    private fun updateChangeBackgroundButtonText() {
        val changeBackgroundButton = settingsLayout.changeBackgroundButton
        changeBackgroundButton.text = if (isUsingImageBackground) "切换为颜色背景" else "切换背景颜色"
    }

    private fun checkAndRequestPermission() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else if (shouldShowRequestPermissionRationale(permission)) {
            Toast.makeText(this, "需要访问相册权限才能选择背景图片", Toast.LENGTH_SHORT).show()
            requestPermissionLauncher.launch(permission)
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun openGallery() {
        try {
            android.util.Log.d("SettingsActivity", "Opening gallery")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("image/jpeg", "image/png", "image/gif")
            )
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            android.util.Log.d("SettingsActivity", "Launching image picker")
            pickImageLauncher.launch(Intent.createChooser(intent, "选择背景图片"))
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("SettingsActivity", "Error opening gallery: ${e.message}")
            Toast.makeText(this, "无法打开相册: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToPrivateDir(uri: Uri) {
        try {
            android.util.Log.d("SettingsActivity", "Saving image from URI: $uri")
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val file = File(filesDir, "background_image.jpg")
                android.util.Log.d("SettingsActivity", "Saving to file: ${file.absolutePath}")
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var read: Int
                var totalRead = 0
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                    totalRead += read
                }
                outputStream.close()
                inputStream.close()
                android.util.Log.d("SettingsActivity", "Image saved successfully, total bytes: $totalRead")
                backgroundManager.markImageChanged()
                Toast.makeText(this, "背景图片已保存", Toast.LENGTH_SHORT).show()
            } else {
                android.util.Log.e("SettingsActivity", "Failed to open input stream from URI")
                Toast.makeText(this, "无法打开图片流", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("SettingsActivity", "Error saving image: ${e.message}")
            Toast.makeText(this, "保存图片失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreDefaultBackground() {
        isUsingImageBackground = false
        backgroundImageUri = null
        colorIndex = 0
        backgroundColorId = backgroundColors[colorIndex]
        window.decorView.setBackgroundResource(backgroundColorId)
        
        val file = File(filesDir, "background_image.jpg")
        if (file.exists()) {
            file.delete()
            backgroundManager.markImageChanged()
        }
        
        backgroundManager.saveColorIndex(colorIndex)
        updateChangeBackgroundButtonText()
    }


}
