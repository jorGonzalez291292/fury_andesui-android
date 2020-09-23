package com.mercadolibre.android.andesui.textfield.style

import java.util.Locale


/**
 * Defines the possible styles an [AndesTextfieldCode] can take.
 * But as a bonus it gives you the proper implementation so you don't have to make any mapping.
 *
 * You ask me with, let's say 'THREESOME', and then I'll give you a proper implementation of that style.
 *
 * @property style Possible style that an [AndesTextfieldCode] may take.
 */
enum class AndesTextfieldCodeStyle {
    THREESOME,
    FOURSOME,
    THREE_BY_THREE;

    internal val style get() = getAndesTextfieldCodeStyle()

    private fun getAndesTextfieldCodeStyle(): AndesTextfieldCodeStyleInterface {
        return when (this) {
            THREESOME -> AndesTextfieldCodeThreesomeStyleInterface
            FOURSOME -> AndesTextfieldCodeFoursomeStyleInterface
            THREE_BY_THREE -> AndesTextfieldCodeThreeByThreeStyleInterface
        }
    }

    companion object {
        fun fromString(value: String): AndesTextfieldCodeStyle = valueOf(value.toUpperCase(Locale.getDefault()))
    }
}