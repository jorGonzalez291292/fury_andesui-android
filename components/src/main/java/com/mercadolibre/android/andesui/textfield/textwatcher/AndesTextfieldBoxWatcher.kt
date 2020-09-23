package com.mercadolibre.android.andesui.textfield.textwatcher

import android.text.Editable
import android.text.TextWatcher

internal class AndesTextfieldBoxWatcher(
    private val andesTextfieldCodeWatcher: AndesTextfieldCodeWatcher,
    private val indexView: Int
    ): TextWatcher {
    override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {
        andesTextfieldCodeWatcher.isDeleting = count > after
    }

    override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
        andesTextfieldCodeWatcher.onTextChanged(charSequence, indexView)
    }

    override fun afterTextChanged(editable: Editable) = Unit

}