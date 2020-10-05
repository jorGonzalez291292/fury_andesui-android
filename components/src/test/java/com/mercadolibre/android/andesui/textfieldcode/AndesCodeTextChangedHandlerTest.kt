package com.mercadolibre.android.andesui.textfieldcode

import android.os.Build
import com.mercadolibre.android.andesui.BuildConfig
import com.mercadolibre.android.andesui.textfield.textwatcher.AndesCodeTextChangedHandler
import com.mercadolibre.android.andesui.textfield.textwatcher.OnChange
import com.mercadolibre.android.andesui.textfield.textwatcher.OnComplete
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.annotation.Config

@RunWith(MockitoJUnitRunner::class)
@Config(constants = BuildConfig::class, sdk = [Build.VERSION_CODES.LOLLIPOP])
class AndesCodeTextChangedHandlerTest {

    @Mock private lateinit var onComplete: OnComplete
    @Mock private lateinit var onChange: OnChange
    private lateinit var codeTextChangedHandler: AndesCodeTextChangedHandler

    @Before
    fun setUp() {
        codeTextChangedHandler = AndesCodeTextChangedHandler(3, onChange, onComplete)
        codeTextChangedHandler.onTextChanged("1", 0)
        codeTextChangedHandler.onTextChanged("2", 1)
        codeTextChangedHandler.onTextChanged("3", 2)
    }

    @Test
    fun `on change called`() {
        verify(onChange, times(3)).invoke(any())
        verifyNoMoreInteractions(onChange)
    }

    @Test
    fun `on complete called`() {
        verify(onComplete, times(2)).invoke(false)
        verify(onComplete, times(1)).invoke(true)
        verifyNoMoreInteractions(onComplete)
    }

    @Test
    fun `without interactions`() {
        codeTextChangedHandler.onTextChanged("1", 0)
        codeTextChangedHandler.onTextChanged("2", 1)
        codeTextChangedHandler.onTextChanged("3", 2)
        verify(onChange, times(3)).invoke(any())
        verify(onComplete, times(2)).invoke(false)
        verify(onComplete, times(1)).invoke(true)
    }

    @Test
    fun `reset handler`() {
        codeTextChangedHandler.reset(0)
        codeTextChangedHandler.onTextChanged("1", 0)
        codeTextChangedHandler.onTextChanged("2", 1)
        codeTextChangedHandler.onTextChanged("3", 2)
        verify(onChange, times(6)).invoke(any())
        verify(onComplete, times(4)).invoke(false)
        verify(onComplete, times(2)).invoke(true)
    }

}