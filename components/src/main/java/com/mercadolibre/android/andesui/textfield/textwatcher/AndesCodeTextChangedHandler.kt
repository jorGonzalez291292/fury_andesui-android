package com.mercadolibre.android.andesui.textfield.textwatcher

internal typealias OnChange = (text: String) -> Unit
internal typealias OnComplete = (isFull: Boolean) -> Unit

internal class AndesCodeTextChangedHandler(
    private val boxCount: Int,
    private val onChange: OnChange,
    private val onComplete: OnComplete) {

    private val textArray = Array(boxCount) {""}

    fun onTextChanged(charSequence: CharSequence, indexChild: Int) {
        val oldCharSequence = textArray[indexChild]
        if (oldCharSequence != charSequence) {
            textArray[indexChild] = "$charSequence"
            val currentText = textArray.joinToString(separator = "")
            onChange(currentText)
            onComplete(currentText.length == boxCount)
        }
    }

    fun reset(from: Int) {
        IntRange(from, textArray.lastIndex).forEach { textArray[it] = "" }
    }
}