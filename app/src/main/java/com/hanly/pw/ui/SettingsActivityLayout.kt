package com.hanly.pw.ui

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.hanly.pw.R

class SettingsActivityLayout(context: Context) : LinearLayout(context) {
    val changeBackgroundButton: Button
    val selectImageButton: Button
    val restoreDefaultButton: Button
    val backButton: Button

    init {
        orientation = VERTICAL
        setPadding(32.dp, 32.dp, 32.dp, 32.dp)
        setBackgroundColor(android.graphics.Color.TRANSPARENT)

        // Title
        val titleTextView = TextView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 40.dp)
            }
            text = "设置"
            textSize = 32f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            // Set white text with black shadow for better contrast against any background
            setTextColor(android.graphics.Color.WHITE)
            setShadowLayer(2f, 0f, 0f, android.graphics.Color.BLACK)
            gravity = Gravity.CENTER
        }
        addView(titleTextView)

        // Settings Card with Glass Mirror Effect
        val settingsCard = CardView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 32.dp)
            }
            // Glass mirror effect: semi-transparent background
            setCardBackgroundColor(android.graphics.Color.argb(140, 255, 255, 255))
            radius = 20f
            elevation = 12f
            cardElevation = 12f
            maxCardElevation = 16f
            // Add border for glass effect
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                outlineProvider = object : android.view.ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: android.graphics.Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height, 20f)
                    }
                }
                clipToOutline = true
            }
            // Add subtle border for glass effect
            setBackgroundResource(android.R.color.transparent)
            val borderDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                setColor(android.graphics.Color.argb(140, 255, 255, 255))
                setStroke(2, android.graphics.Color.argb(80, 255, 255, 255))
                cornerRadius = 20f
            }
            background = borderDrawable
        }

        val settingsContainer = LinearLayout(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = VERTICAL
            setPadding(24.dp, 24.dp, 24.dp, 24.dp)
        }



        changeBackgroundButton = Button(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                72.dp
            ).apply {
                setMargins(0, 0, 0, 20.dp)
            }
            text = "切换背景颜色"
            textSize = 18f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(Color.WHITE)
            setShadowLayer(2f, 0f, 0f, android.graphics.Color.BLACK)
            // Add gradient background with spots
            val gradientDrawable = android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    resources.getColor(R.color.primary, null),
                    resources.getColor(R.color.secondary, null),
                    resources.getColor(R.color.accent, null)
                )
            )
            gradientDrawable.shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            gradientDrawable.cornerRadius = 24f
            
            // Create spot pattern
            val spotDrawable = object : android.graphics.drawable.Drawable() {
                override fun draw(canvas: android.graphics.Canvas) {
                    val paint = android.graphics.Paint()
                    paint.isAntiAlias = true
                    
                    // Draw small spots
                    val colors = intArrayOf(
                        android.graphics.Color.argb(150, 255, 255, 255),
                        android.graphics.Color.argb(100, 255, 255, 255),
                        android.graphics.Color.argb(80, 255, 255, 255)
                    )
                    
                    val random = java.util.Random(44) // Fixed seed for consistent pattern
                    for (i in 0..20) {
                        val x = random.nextInt(bounds.width())
                        val y = random.nextInt(bounds.height())
                        val size = random.nextInt(8) + 4
                        paint.color = colors[random.nextInt(colors.size)]
                        canvas.drawCircle(x.toFloat(), y.toFloat(), size.toFloat(), paint)
                    }
                }
                
                override fun setAlpha(alpha: Int) {}
                override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}
                override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT
            }
            
            // Combine gradient and spots
            val layerDrawable = android.graphics.drawable.LayerDrawable(arrayOf(
                gradientDrawable,
                spotDrawable
            ))
            background = layerDrawable
            
            // Add ripple effect and smooth animation
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                stateListAnimator = android.animation.StateListAnimator().apply {
                    addState(
                        intArrayOf(android.R.attr.state_pressed),
                        android.animation.ObjectAnimator.ofFloat(this, "translationZ", 12f)
                    )
                    addState(
                        intArrayOf(),
                        android.animation.ObjectAnimator.ofFloat(this, "translationZ", 6f)
                    )
                }
                // Add ripple effect
                foreground = android.graphics.drawable.RippleDrawable(
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.argb(100, 255, 255, 255)
                    ),
                    null,
                    android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                        cornerRadius = 24f
                    }
                )
            }
            // Add click animation
            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        v.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(100)
                            .start()
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start()
                    }
                }
                false
            }
        }
        settingsContainer.addView(changeBackgroundButton)

        selectImageButton = Button(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                72.dp
            ).apply {
                setMargins(0, 0, 0, 20.dp)
            }
            text = "选择背景图片"
            textSize = 18f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(Color.WHITE)
            setShadowLayer(2f, 0f, 0f, android.graphics.Color.BLACK)
            // Add gradient background with spots
            val gradientDrawable = android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    resources.getColor(R.color.secondary, null),
                    resources.getColor(R.color.accent, null),
                    resources.getColor(R.color.primary, null)
                )
            )
            gradientDrawable.shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            gradientDrawable.cornerRadius = 24f
            
            // Create spot pattern
            val spotDrawable = object : android.graphics.drawable.Drawable() {
                override fun draw(canvas: android.graphics.Canvas) {
                    val paint = android.graphics.Paint()
                    paint.isAntiAlias = true
                    
                    // Draw small spots
                    val colors = intArrayOf(
                        android.graphics.Color.argb(150, 255, 255, 255),
                        android.graphics.Color.argb(100, 255, 255, 255),
                        android.graphics.Color.argb(80, 255, 255, 255)
                    )
                    
                    val random = java.util.Random(45) // Fixed seed for consistent pattern
                    for (i in 0..20) {
                        val x = random.nextInt(bounds.width())
                        val y = random.nextInt(bounds.height())
                        val size = random.nextInt(8) + 4
                        paint.color = colors[random.nextInt(colors.size)]
                        canvas.drawCircle(x.toFloat(), y.toFloat(), size.toFloat(), paint)
                    }
                }
                
                override fun setAlpha(alpha: Int) {}
                override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}
                override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT
            }
            
            // Combine gradient and spots
            val layerDrawable = android.graphics.drawable.LayerDrawable(arrayOf(
                gradientDrawable,
                spotDrawable
            ))
            background = layerDrawable
            
            // Add ripple effect and smooth animation
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                stateListAnimator = android.animation.StateListAnimator().apply {
                    addState(
                        intArrayOf(android.R.attr.state_pressed),
                        android.animation.ObjectAnimator.ofFloat(this, "translationZ", 12f)
                    )
                    addState(
                        intArrayOf(),
                        android.animation.ObjectAnimator.ofFloat(this, "translationZ", 6f)
                    )
                }
                // Add ripple effect
                foreground = android.graphics.drawable.RippleDrawable(
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.argb(100, 255, 255, 255)
                    ),
                    null,
                    android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                        cornerRadius = 24f
                    }
                )
            }
            // Add click animation
            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        v.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(100)
                            .start()
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start()
                    }
                }
                false
            }
        }
        settingsContainer.addView(selectImageButton)

        restoreDefaultButton = Button(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                72.dp
            )
            text = "恢复默认背景"
            textSize = 18f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(Color.WHITE)
            setShadowLayer(2f, 0f, 0f, android.graphics.Color.BLACK)
            // Add gradient background with spots
            val gradientDrawable = android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    resources.getColor(R.color.accent, null),
                    resources.getColor(R.color.primary, null),
                    resources.getColor(R.color.secondary, null)
                )
            )
            gradientDrawable.shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            gradientDrawable.cornerRadius = 24f
            
            // Create spot pattern
            val spotDrawable = object : android.graphics.drawable.Drawable() {
                override fun draw(canvas: android.graphics.Canvas) {
                    val paint = android.graphics.Paint()
                    paint.isAntiAlias = true
                    
                    // Draw small spots
                    val colors = intArrayOf(
                        android.graphics.Color.argb(150, 255, 255, 255),
                        android.graphics.Color.argb(100, 255, 255, 255),
                        android.graphics.Color.argb(80, 255, 255, 255)
                    )
                    
                    val random = java.util.Random(46) // Fixed seed for consistent pattern
                    for (i in 0..20) {
                        val x = random.nextInt(bounds.width())
                        val y = random.nextInt(bounds.height())
                        val size = random.nextInt(8) + 4
                        paint.color = colors[random.nextInt(colors.size)]
                        canvas.drawCircle(x.toFloat(), y.toFloat(), size.toFloat(), paint)
                    }
                }
                
                override fun setAlpha(alpha: Int) {}
                override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}
                override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT
            }
            
            // Combine gradient and spots
            val layerDrawable = android.graphics.drawable.LayerDrawable(arrayOf(
                gradientDrawable,
                spotDrawable
            ))
            background = layerDrawable
            
            // Add ripple effect and smooth animation
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                stateListAnimator = android.animation.StateListAnimator().apply {
                    addState(
                        intArrayOf(android.R.attr.state_pressed),
                        android.animation.ObjectAnimator.ofFloat(this, "translationZ", 12f)
                    )
                    addState(
                        intArrayOf(),
                        android.animation.ObjectAnimator.ofFloat(this, "translationZ", 6f)
                    )
                }
                // Add ripple effect
                foreground = android.graphics.drawable.RippleDrawable(
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.argb(100, 255, 255, 255)
                    ),
                    null,
                    android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                        cornerRadius = 24f
                    }
                )
            }
            // Add click animation
            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        v.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(100)
                            .start()
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start()
                    }
                }
                false
            }
        }
        settingsContainer.addView(restoreDefaultButton)

        settingsCard.addView(settingsContainer)
        addView(settingsCard)

        // Back Button
        backButton = Button(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                68.dp
            )
            text = "返回"
            textSize = 18f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(Color.WHITE)
            setShadowLayer(2f, 0f, 0f, android.graphics.Color.BLACK)
            setCornerRadius(20f, resources.getColor(R.color.gray, null))
            // Add ripple effect and smooth animation
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                stateListAnimator = android.animation.StateListAnimator().apply {
                    addState(
                        intArrayOf(android.R.attr.state_pressed),
                        android.animation.ObjectAnimator.ofFloat(this, "translationZ", 12f)
                    )
                    addState(
                        intArrayOf(),
                        android.animation.ObjectAnimator.ofFloat(this, "translationZ", 6f)
                    )
                }
                background = android.graphics.drawable.RippleDrawable(
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.argb(100, 255, 255, 255)
                    ),
                    background,
                    null
                )
            }
        }
        addView(backButton)
    }

    private val Int.dp get() = (this * resources.displayMetrics.density + 0.5f).toInt()

    private fun ViewGroup.MarginLayoutParams.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
        setMargins(left, top, right, bottom)
    }

    private fun Button.setCornerRadius(radius: Float, color: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            this.elevation = 4f
            this.stateListAnimator = null
        }
        this.background = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = radius
        }
    }
}