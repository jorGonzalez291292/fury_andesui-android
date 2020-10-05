package com.mercadolibre.android.andesui.textfieldcode

import android.os.Build
import com.facebook.common.logging.FLog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.listener.RequestLoggingListener
import com.facebook.soloader.SoLoader
import com.mercadolibre.android.andesui.BuildConfig
import com.mercadolibre.android.andesui.textfield.AndesTextfieldCode
import com.mercadolibre.android.andesui.textfield.style.AndesTextfieldCodeStyle
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.times
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [Build.VERSION_CODES.LOLLIPOP])
class AndesTextfieldCodeTest {
    private var context = RuntimeEnvironment.application
    private lateinit var textfieldCode: AndesTextfieldCode

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            SoLoader.setInTestMode()
        }
    }

    @Before
    fun setUp() {
        val requestListeners = setOf<RequestListener>(RequestLoggingListener())
        val config = ImagePipelineConfig.newBuilder(context)
            // other setters
            .setRequestListeners(requestListeners)
            .build()
        Fresco.initialize(context, config)
        FLog.setMinimumLoggingLevel(FLog.VERBOSE)
        textfieldCode = AndesTextfieldCode(context)
    }

    @Test
    fun `set text with threesome style`() {
        val currentText = "123456"
        val expected = "123"
        textfieldCode.text = currentText
        assertEquals(expected, textfieldCode.text)
    }

    @Test
    fun `set text with foursome style`() {
        val currentText = "123456"
        val expected = "1234"
        textfieldCode.style = AndesTextfieldCodeStyle.FOURSOME
        textfieldCode.text = currentText
        assertEquals(expected, textfieldCode.text)
    }

    @Test
    fun `set text with three_by_three style`() {
        val currentText = "12345678"
        val expected = "123456"
        textfieldCode.style = AndesTextfieldCodeStyle.THREE_BY_THREE
        textfieldCode.text = currentText
        assertEquals(expected, textfieldCode.text)
    }

    @Test
    fun `set on complete listener`() {
        val callback = mock<AndesTextfieldCode.OnCompletionListener>()
        textfieldCode.setOnCompleteListener(callback)
        textfieldCode.text = "12"
        verify(callback, times(2)).onComplete(false)
        textfieldCode.text = "123"
        verify(callback, times(1)).onComplete(true)
    }

    @Test
    fun `set on text change listener`() {
        val callback1 = mock<AndesTextfieldCode.OnTextChangeListener>()
        val callback2 = mock<AndesTextfieldCode.OnTextChangeListener>()
        val captor = argumentCaptor<String>()
        textfieldCode.setOnTextChangeListener(callback1)
        textfieldCode.text = "12"
        verify(callback1, times(2)).onChange(captor.capture())
        assertEquals("12", captor.lastValue)
        textfieldCode.setOnTextChangeListener(callback2)
        textfieldCode.text = "123"
        verify(callback2, times(3)).onChange(captor.capture())
        assertEquals("123", captor.lastValue)
    }
}