package com.mercadolibre.android.andesui.textfieldcode

import android.os.Build
import android.text.Editable
import android.text.SpannableStringBuilder
import com.facebook.soloader.SoLoader
import com.mercadolibre.android.andesui.BuildConfig
import com.mercadolibre.android.andesui.textfield.textwatcher.AndesCodeFocusManagement
import com.mercadolibre.android.andesui.textfield.textwatcher.AndesCodeTextChangedHandler
import com.mercadolibre.android.andesui.textfield.textwatcher.AndesTextfieldBoxWatcher
import com.mercadolibre.android.andesui.textfield.textwatcher.AndesTextfieldBoxWatcher.Companion.DIRTY_CHARACTER
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [Build.VERSION_CODES.LOLLIPOP])
class AndesTextfieldBoxWatcherTest {
    @Mock
    private lateinit var focusManagement: AndesCodeFocusManagement
    @Mock
    private lateinit var codeTextChangedHandler: AndesCodeTextChangedHandler
    private lateinit var editable: Editable
    private lateinit var textfieldBoxWatcher: AndesTextfieldBoxWatcher

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            SoLoader.setInTestMode()
        }
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        editable = SpannableStringBuilder()
        textfieldBoxWatcher = AndesTextfieldBoxWatcher(codeTextChangedHandler, focusManagement, 0)
    }

    @Test
    fun `when sets dirty character`() {
        editable.insert(0, DIRTY_CHARACTER)
        textfieldBoxWatcher.beforeTextChanged("", 0, 0, 1)
        textfieldBoxWatcher.onTextChanged(DIRTY_CHARACTER, 0, 0, 0)
        textfieldBoxWatcher.afterTextChanged(editable)
        verifyZeroInteractions(codeTextChangedHandler)
        verifyZeroInteractions(focusManagement)
    }

    @Test
    fun `when sets dirty character with valid character`() {
        val textChange = "1"
        val editableText = DIRTY_CHARACTER + textChange
        val captor = argumentCaptor<CharSequence>()
        editable.insert(0, editableText)

        textfieldBoxWatcher.beforeTextChanged(DIRTY_CHARACTER, 0, 1, 2)
        textfieldBoxWatcher.onTextChanged(editableText, 0, 0, 0)
        textfieldBoxWatcher.afterTextChanged(editable)

        verify(codeTextChangedHandler).onTextChanged(captor.capture(), any())
        assertEquals(textChange, captor.firstValue.toString())
        verify(focusManagement).goToNextFocus()
        verifyNoMoreInteractions(focusManagement)
    }

    @Test
    fun `when sets only valid character`() {
        val expected = "1"
        val captor = argumentCaptor<CharSequence>()
        editable.insert(0, expected)

        textfieldBoxWatcher.beforeTextChanged("", 0, 1, 2)
        textfieldBoxWatcher.onTextChanged(expected, 0, 0, 0)
        textfieldBoxWatcher.afterTextChanged(editable)

        verify(codeTextChangedHandler).onTextChanged(captor.capture(), any())
        assertEquals(expected, captor.firstValue.toString())
        verify(focusManagement).goToNextFocus()
        verifyNoMoreInteractions(focusManagement)
    }

    @Test
    fun `when delete valid character with dirty character`() {
        val expected = ""
        val captor = argumentCaptor<CharSequence>()

        editable.insert(0, DIRTY_CHARACTER)

        textfieldBoxWatcher.beforeTextChanged(DIRTY_CHARACTER + "1", 0, 2, 1)
        textfieldBoxWatcher.onTextChanged(DIRTY_CHARACTER, 0, 0, 0)
        textfieldBoxWatcher.afterTextChanged(editable)

        verify(codeTextChangedHandler).onTextChanged(captor.capture(), any())
        assertEquals(expected, captor.firstValue.toString())
        verifyZeroInteractions(focusManagement)
    }

    @Test
    fun `when delete dirty character`() {
        textfieldBoxWatcher = AndesTextfieldBoxWatcher(codeTextChangedHandler, focusManagement, 1)
        val expected = ""
        editable.insert(0, expected)

        textfieldBoxWatcher.beforeTextChanged(DIRTY_CHARACTER, 0, 1, 0)
        textfieldBoxWatcher.onTextChanged(expected, 0, 0, 0)
        textfieldBoxWatcher.afterTextChanged(editable)

        verifyZeroInteractions(codeTextChangedHandler)
        verify(focusManagement).goToPreviousFocus()
        verifyNoMoreInteractions(focusManagement)
    }
}