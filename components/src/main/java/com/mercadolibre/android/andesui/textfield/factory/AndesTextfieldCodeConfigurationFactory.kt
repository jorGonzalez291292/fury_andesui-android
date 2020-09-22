package com.mercadolibre.android.andesui.textfield.factory

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.color.AndesColor
import com.mercadolibre.android.andesui.textfield.state.AndesTextfieldCodeState
import com.mercadolibre.android.andesui.textfield.state.AndesTextfieldState
import com.mercadolibre.android.andesui.textfield.state.AndesTextfieldStateInterface
import com.mercadolibre.android.andesui.textfield.style.AndesTextfieldCodeStyleInterface
import com.mercadolibre.android.andesui.typeface.getFontOrDefault

internal data class AndesTextfieldCodeConfiguration(
    val helperColor: AndesColor,
    val helperText: String? = null,
    val helperSize: Float,
    val helperTypeface: Typeface,
    val labelColor: AndesColor,
    val labelText: String? = null,
    val labelSize: Float,
    val boxState: AndesTextfieldState,
    val boxWidth: Int,
    val boxesPattern: IntArray,
    val marginBetweenBoxes: Int,
    val marginBetweenGroups: Int,
    val typeface: Typeface,
    val icon: Drawable?
)

internal object AndesTextfieldCodeConfigurationFactory {

    fun create(context: Context, andesTextfieldCodeAttrs: AndesTextfieldCodeAttrs): AndesTextfieldCodeConfiguration {
        return with(andesTextfieldCodeAttrs) {
            AndesTextfieldCodeConfiguration(
                helperColor = resolveHelperTextColor(state.state),
                helperText = helper,
                helperSize = resolveHelperSize(context),
                helperTypeface = resolveHelperTypeface(state.state, context),
                labelColor = resolveLabelTextColor(state.state),
                labelText = label,
                labelSize = resolveLabelSize(context),
                boxesPattern = resolveBoxPatter(style.style),
                boxState = resolveBoxState(state),
                boxWidth = resolveBoxWidth(context, style.style),
                marginBetweenBoxes = resolveMarginBetweenBoxes(context, style.style),
                marginBetweenGroups = resolveMarginBetweenGroups(context, style.style),
                typeface = resolveTypeface(context),
                icon = resolveIcon(context, state.state)
            )
        }
    }

    private fun resolveHelperTextColor(state: AndesTextfieldStateInterface): AndesColor = state.helpersColor()
    private fun resolveHelperSize(context: Context): Float = context.resources.getDimension(R.dimen.andes_textfield_helper_textSize)
    private fun resolveHelperTypeface(state: AndesTextfieldStateInterface, context: Context) = state.typeFace(context)
    private fun resolveLabelTextColor(state: AndesTextfieldStateInterface): AndesColor = state.labelColor()
    private fun resolveLabelSize(context: Context): Float = context.resources.getDimension(R.dimen.andes_textfield_label_textSize)
    private fun resolveTypeface(context: Context) = context.getFontOrDefault(R.font.andes_font_regular)
    private fun resolveIcon(context: Context, state: AndesTextfieldStateInterface): Drawable? = state.icon(context)
    private fun resolveBoxPatter(style: AndesTextfieldCodeStyleInterface): IntArray = style.pattern()
    private fun resolveMarginBetweenBoxes(
        context: Context,
        style: AndesTextfieldCodeStyleInterface
    ): Int = style.boxMargin(context)
    private fun resolveMarginBetweenGroups(
        context: Context,
        style: AndesTextfieldCodeStyleInterface
    ): Int = style.groupMargin(context)
    private fun resolveBoxWidth(
        context: Context,
        style: AndesTextfieldCodeStyleInterface):Int = style.boxWidth(context)
    private fun resolveBoxState(state: AndesTextfieldCodeState): AndesTextfieldState {
        return when (state) {
            AndesTextfieldCodeState.IDLE -> AndesTextfieldState.IDLE
            AndesTextfieldCodeState.DISABLED -> AndesTextfieldState.DISABLED
            AndesTextfieldCodeState.ERROR -> AndesTextfieldState.ERROR
        }
    }
}