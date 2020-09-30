package com.mercadolibre.android.andesui.textfield.factory

import android.content.Context
import android.util.AttributeSet
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.textfield.state.AndesTextfieldCodeState
import com.mercadolibre.android.andesui.textfield.style.AndesTextfieldCodeStyle

/**
 * The data class that contains the public components of the andes code textfield.
 */
internal data class AndesTextfieldCodeAttrs(
    val label: String?,
    val helper: String?,
    val style: AndesTextfieldCodeStyle,
    val state: AndesTextfieldCodeState
)

/**
 * This object parse the attribute set and return an instance of andesTextfieldCodeAttrs to be used by andesTextfieldCode
 */
internal object AndesTextfieldCodeAttrsParser {

    private const val ANDES_CODE_TEXTFIELD_STYLE_THREESOME = "1000"
    private const val ANDES_CODE_TEXTFIELD_STYLE_FOURSOME = "1001"
    private const val ANDES_CODE_TEXTFIELD_STYLE_THREE_BY_THREE = "1002"
    private const val ANDES_CODE_TEXTFIELD_STATE_IDLE = "2000"
    private const val ANDES_CODE_TEXTFIELD_STATE_DISABLE = "2001"
    private const val ANDES_CODE_TEXTFIELD_STATE_ERROR = "2002"

    fun parse(context: Context, attr: AttributeSet?): AndesTextfieldCodeAttrs {
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.AndesTextfieldCode)

        val label = typedArray.getString(R.styleable.AndesTextfieldCode_andesTextfieldCodeLabel)
        val helper = typedArray.getString(R.styleable.AndesTextfieldCode_andesTextfieldCodeHelper)

        val style = when (typedArray.getString(R.styleable.AndesTextfieldCode_andesTextfieldCodeStyle)) {
            ANDES_CODE_TEXTFIELD_STYLE_THREESOME -> AndesTextfieldCodeStyle.THREESOME
            ANDES_CODE_TEXTFIELD_STYLE_FOURSOME -> AndesTextfieldCodeStyle.FOURSOME
            ANDES_CODE_TEXTFIELD_STYLE_THREE_BY_THREE -> AndesTextfieldCodeStyle.THREE_BY_THREE
            else -> AndesTextfieldCodeStyle.THREESOME
        }

        val state = when (typedArray.getString(R.styleable.AndesTextfieldCode_andesTextfieldCodeState)) {
            ANDES_CODE_TEXTFIELD_STATE_IDLE -> AndesTextfieldCodeState.IDLE
            ANDES_CODE_TEXTFIELD_STATE_DISABLE -> AndesTextfieldCodeState.DISABLED
            ANDES_CODE_TEXTFIELD_STATE_ERROR -> AndesTextfieldCodeState.ERROR
            else -> AndesTextfieldCodeState.IDLE
        }

        return AndesTextfieldCodeAttrs(label, helper, style, state).also { typedArray.recycle() }
    }
}