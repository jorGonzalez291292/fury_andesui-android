package com.mercadolibre.android.andesui.textfield.textwatcher

internal typealias OnChange = (indexView: Int, text: String) -> Unit
internal typealias OnComplete = () -> Unit

internal class AndesTextfieldCodeWatcher(
    private val boxCount: Int,
    private val onChange: OnChange,
    private val onComplete: OnComplete) {

    var isDeleting = false
    private var currentText = ""

    fun onTextChanged(charSequence: CharSequence, indexChild: Int) {
        if(charSequence.isEmpty()) {
            (indexChild + 1).takeIf { currentText.length >= it }?.let {
                currentText = currentText.removeRange(indexChild, it)
            }
        } else {
            currentText += charSequence
        }
        onChange(indexChild, currentText)
        if (currentText.length == boxCount) {
            onComplete()
        }
    }
}