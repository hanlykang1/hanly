package com.hanly.pw

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.hanly.pw.R
import java.io.File
import java.io.FileOutputStream

public class SettingsActivity : AppCompatActivity() {
    private lateinit var backgroundManager: BackgroundManager
    private var colorIndex = 0
    private var isUsingImageBackground = false
    private var backgroundImageUri: String? = null
    private var backgroundColorId = 0
    private val backgroundColors = arrayOf(
        android.R.color.holo_blue_light,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light,
        android.R.color.holo_purple
    )

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val uri = data?.data
            if (uri != null) {
                backgroundImageUri = uri.toString()
                isUsingImageBackground = true
                saveImageToPrivateDir(uri)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES)) {
            Toast.makeText(this, "需要访问相册权限才能选择图片", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "权限被拒绝。请在设置中手动开启相册访问权限", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        backgroundManager = BackgroundManager(this)
        loadSavedBackground()
        loadSavedBackgroundColor()

        val changeBackgroundButton = findViewById<Button>(R.id.changeBackgroundButton)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        val restoreDefaultButton = findViewById<Button>(R.id.restoreDefaultButton)
        val backButton = findViewById<Button>(R.id.backButton)

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
        window.decorView.setBackgroundResource(backgroundColorId)
    }

    private fun changeBackground() {
        if (isUsingImageBackground) {
            backgroundImageUri = null
            isUsingImageBackground = false
            val file = File(filesDir, "background_image.jpg")
            if (file.exists()) {
                file.delete()
                backgroundManager.markImageChanged()
            }
        }

        colorIndex = (colorIndex + 1) % backgroundColors.size
        backgroundColorId = backgroundColors[colorIndex]
        window.decorView.setBackgroundResource(backgroundColorId)
        backgroundManager.saveColorIndex(colorIndex)
        updateChangeBackgroundButtonText()
    }

    private fun updateChangeBackgroundButtonText() {
        val changeBackgroundButton = findViewById<Button>(R.id.changeBackgroundButton)
        changeBackgroundButton.text = if (isUsingImageBackground) "切换为颜色背景" else "切换背景颜色"
    }

    private fun checkAndRequestPermission() {
        val permission = android.Manifest.permission.READ_MEDIA_IMAGES
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
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("image/jpeg", "image/png", "image/gif")
            )
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            pickImageLauncher.launch(Intent.createChooser(intent, "选择背景图片"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "无法打开相册: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToPrivateDir(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val file = File(filesDir, "background_image.jpg")
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.close()
                inputStream.close()
                backgroundManager.markImageChanged()
                Toast.makeText(this, "背景图片已保存", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
