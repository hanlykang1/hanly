package com.hanly.pw.ui

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.hanly.pw.R

class MainActivityLayout(context: Context) : LinearLayout(context) {
    val randomStringEditText: EditText
    val lengthTextView: TextView
    val lengthSeekBar: SeekBar
    val generateButton: Button
    val settingsButton: Button
    val randomStringCard: CardView
    val lengthCard: CardView

    init {
        orientation = VERTICAL
        setPadding(32.dp, 32.dp, 32.dp, 32.dp)
        setBackgroundColor(android.graphics.Color.TRANSPARENT)

        // Header Container with GitHub Button only (removed title)
        val headerContainer = LinearLayout(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 20.dp)
            }
            orientation = HORIZONTAL
            gravity = Gravity.END
        }

        // GitHub Button in Top Right (using a simple GitHub-style button)
        settingsButton = Button(context).apply {
            layoutParams = LayoutParams(56.dp, 56.dp)
            // Remove text, use GitHub logo instead
            text = ""
            // Remove default button background
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            
            // Create GitHub-style drawable
            val githubDrawable = object : android.graphics.drawable.Drawable() {
                override fun draw(canvas: android.graphics.Canvas) {
                    val paint = android.graphics.Paint()
                    paint.isAntiAlias = true
                    
                    // Draw GitHub logo
                    val centerX = bounds.width() / 2f
                    val centerY = bounds.height() / 2f
                    val radius = bounds.width() / 2f - 6
                    
                    // GitHub logo colors
                    paint.color = android.graphics.Color.rgb(36, 41, 46) // GitHub dark gray
                    
                    // Draw outer circle
                    canvas.drawCircle(centerX, centerY, radius, paint)
                    
                    // Draw GitHub "octocat" shape
                    paint.color = android.graphics.Color.WHITE
                    
                    // Draw upper part
                    canvas.drawCircle(centerX - radius * 0.25f, centerY - radius * 0.25f, radius * 0.3f, paint)
                    canvas.drawCircle(centerX + radius * 0.25f, centerY - radius * 0.25f, radius * 0.3f, paint)
                    
                    // Draw lower part
                    val rect = android.graphics.RectF(
                        centerX - radius * 0.4f,
                        centerY - radius * 0.1f,
                        centerX + radius * 0.4f,
                        centerY + radius * 0.3f
                    )
                    canvas.drawRect(rect, paint)
                    
                    // Draw nose
                    canvas.drawCircle(centerX, centerY - radius * 0.1f, radius * 0.1f, paint)
                }
                
                override fun setAlpha(alpha: Int) {}
                override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}
                override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT
            }
            
            // Add ripple effect
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // Create combined drawable with GitHub logo and ripple
                val rippleDrawable = android.graphics.drawable.RippleDrawable(
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.argb(180, 255, 255, 255)
                    ),
                    githubDrawable, // Use GitHub drawable as content
                    android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.OVAL
                        setColor(android.graphics.Color.argb(60, 255, 255, 255))
                    }
                )
                background = rippleDrawable
                
                // Add elevation
                stateListAnimator = android.animation.StateListAnimator().apply {
                    addState(
                        intArrayOf(android.R.attr.state_pressed),
                        android.animation.ObjectAnimator.ofFloat(this, "translationZ", 16f)
                    )
                    addState(
                        intArrayOf(),
                        android.animation.ObjectAnimator.ofFloat(this, "translationZ", 8f)
                    )
                }
            } else {
                // For older versions, just use the GitHub drawable
                background = githubDrawable
            }
            
            // Add click animation
            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        v.animate()
                            .scaleX(0.8f)
                            .scaleY(0.8f)
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
        headerContainer.addView(settingsButton)
        addView(headerContainer)

        // Random String Card with Glass Mirror Effect
        randomStringCard = CardView(context).apply {
            id = View.generateViewId()
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

        randomStringEditText = EditText(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                180.dp
            )
            setBackgroundResource(android.R.color.transparent)
            gravity = Gravity.CENTER
            hint = "点击生成按钮获取随机字符"
            inputType = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setPadding(24.dp, 24.dp, 24.dp, 24.dp)
            textSize = 22f
            typeface = android.graphics.Typeface.MONOSPACE
            // Set white text with black shadow for better contrast against any background
            setTextColor(android.graphics.Color.WHITE)
            setShadowLayer(2f, 0f, 0f, android.graphics.Color.BLACK)
            setHintTextColor(android.graphics.Color.argb(180, 255, 255, 255))
            setOnClickListener {
                // Click event handled in MainActivity
            }
            // Add ripple effect to indicate clickability
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                foreground = android.graphics.drawable.RippleDrawable(
                    android.content.res.ColorStateList.valueOf(
                        resources.getColor(R.color.primary, null)
                    ),
                    null,
                    android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                        setColor(android.graphics.Color.WHITE)
                        cornerRadius = 20f
                    }
                )
            }
            // Add text selection handle customization
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                try {
                    val method = TextView::class.java.getMethod("setTextSelectHandleColor", Int::class.java)
                    method.invoke(this, resources.getColor(R.color.primary, null))
                    val methodLeft = TextView::class.java.getMethod("setTextSelectHandleLeftColor", Int::class.java)
                    methodLeft.invoke(this, resources.getColor(R.color.primary, null))
                    val methodRight = TextView::class.java.getMethod("setTextSelectHandleRightColor", Int::class.java)
                    methodRight.invoke(this, resources.getColor(R.color.primary, null))
                } catch (e: Exception) {
                    // Ignore if methods are not available
                }
            }
        }
        randomStringCard.addView(randomStringEditText)
        addView(randomStringCard)

        // Length Card with Glass Mirror Effect
        lengthCard = CardView(context).apply {
            id = View.generateViewId()
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

        val lengthContainer = LinearLayout(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = VERTICAL
            setPadding(24.dp, 24.dp, 24.dp, 24.dp)
        }

        lengthTextView = TextView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 24.dp)
            }
            text = "长度: 16"
            textSize = 20f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            // Set white text with black shadow for better contrast against any background
            setTextColor(android.graphics.Color.WHITE)
            setShadowLayer(2f, 0f, 0f, android.graphics.Color.BLACK)
            gravity = Gravity.CENTER
        }
        lengthContainer.addView(lengthTextView)

        // Beautified SeekBar
        lengthSeekBar = SeekBar(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                64.dp
            )
            // Use gradient for progress
            val progressGradient = android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    resources.getColor(R.color.primary, null),
                    resources.getColor(R.color.secondary, null),
                    resources.getColor(R.color.accent, null)
                )
            )
            progressGradient.shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            progressGradient.cornerRadius = 10f
            
            // Set progress tint
            progressTintList = android.content.res.ColorStateList.valueOf(resources.getColor(R.color.primary, null))
            progressBackgroundTintList = android.content.res.ColorStateList.valueOf(resources.getColor(R.color.border, null))
            min = 8
            max = 32
            progress = 16
            
            // Set custom thumb drawable for better visual effect
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // Create gradient thumb
                val thumbGradient = android.graphics.drawable.GradientDrawable(
                    android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(
                        resources.getColor(R.color.primary, null),
                        resources.getColor(R.color.secondary, null)
                    )
                )
                thumbGradient.shape = android.graphics.drawable.GradientDrawable.OVAL
                thumbGradient.setSize(36.dp, 36.dp)
                
                val thumbDrawable = android.graphics.drawable.RippleDrawable(
                    android.content.res.ColorStateList.valueOf(resources.getColor(R.color.primary, null)),
                    thumbGradient,
                    null
                )
                thumb = thumbDrawable
            }
        }
        lengthContainer.addView(lengthSeekBar)
        lengthCard.addView(lengthContainer)
        addView(lengthCard)

        // Generate Button
        generateButton = Button(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                72.dp
            )
            text = "生成"
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
                    
                    val random = java.util.Random(42) // Fixed seed for consistent pattern
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
        addView(generateButton)
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