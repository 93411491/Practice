package com.example.practice.draw

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import com.example.practice.R
import com.example.practice.ext.toDp

class TagWithTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.tag_with_text_constraint_layout,this)
        setPadding(10.toDp().toInt())
    }
}