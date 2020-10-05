package com.mercadolibre.android.andesui.textfieldcode

import android.os.Build
import com.mercadolibre.android.andesui.BuildConfig
import com.mercadolibre.android.andesui.textfield.textwatcher.AndesCodeFocusManagement
import com.mercadolibre.android.andesui.textfield.textwatcher.NextFocus
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.annotation.Config

@RunWith(MockitoJUnitRunner::class)
@Config(constants = BuildConfig::class, sdk = [Build.VERSION_CODES.LOLLIPOP])
class AndesCodeFocusManagementTest {

    @Mock private lateinit var onNextFocus: NextFocus
    private lateinit var codeFocusManagement: AndesCodeFocusManagement
    private val maxBoxes = 3

    @Before
    fun setUp() {
        codeFocusManagement = AndesCodeFocusManagement(maxBoxes - 1, onNextFocus)
    }

    @Test
    fun `when go to the nex box`() {
        codeFocusManagement.goToNextFocus()
        verify(onNextFocus).invoke(1, 0)
    }

    @Test
    fun `when go to the previous box`() {
        codeFocusManagement.goToNextFocus()
        codeFocusManagement.goToPreviousFocus()
        verify(onNextFocus).invoke(1, 0)
    }

    @Test
    fun `when go to the last box`() {
        val captorIndexNextFocus = argumentCaptor<Int>()
        val captorIndexPreviousFocus = argumentCaptor<Int>()
        val nextFocusExpected = maxBoxes - 1
        val previousFocusExpected = nextFocusExpected - 1

        for (i in 0 until maxBoxes) {
            codeFocusManagement.goToNextFocus()
        }

        verify(onNextFocus, times(maxBoxes - 1)).invoke(
            captorIndexNextFocus.capture(),
            captorIndexPreviousFocus.capture()
        )
        assertEquals(nextFocusExpected, captorIndexNextFocus.lastValue)
        assertEquals(previousFocusExpected, captorIndexPreviousFocus.lastValue)
    }

    @Test
    fun `when go back to the first box`() {
        val captorIndexNextFocus = argumentCaptor<Int>()
        val captorIndexPreviousFocus = argumentCaptor<Int>()
        val nextFocusExpected = 0
        val previousFocusExpected = 1

        for (i in 0 until maxBoxes) {
            codeFocusManagement.goToNextFocus()
        }

        for (i in maxBoxes downTo 0) {
            codeFocusManagement.goToPreviousFocus()
        }

        verify(onNextFocus, times(4)).invoke(captorIndexNextFocus.capture(), captorIndexPreviousFocus.capture())
        assertEquals(nextFocusExpected, captorIndexNextFocus.lastValue)
        assertEquals(previousFocusExpected, captorIndexPreviousFocus.lastValue)
    }

}