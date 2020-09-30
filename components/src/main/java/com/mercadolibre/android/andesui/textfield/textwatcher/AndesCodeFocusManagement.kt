package com.mercadolibre.android.andesui.textfield.textwatcher

internal typealias NextFocus = (indexNextFocus: Int, indexPreviousFocus: Int) -> Unit

internal class AndesCodeFocusManagement(
    private val maxIndexFocus: Int,
    private val onNextFocus: NextFocus) {

    private var currentIndexFocus: Int = 0

    fun goToNextFocus() {
        (currentIndexFocus + 1).takeIf { it <= maxIndexFocus }?.let {
            onNextFocus(it, currentIndexFocus)
            currentIndexFocus = it
        }
    }

    fun goToPreviousFocus() {
        (currentIndexFocus - 1).takeIf { it >= 0 }?.let {
            onNextFocus(it, currentIndexFocus)
            currentIndexFocus = it
        }
    }

    fun reset(from: Int = 0) {
        currentIndexFocus = from
    }
}