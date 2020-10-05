package com.mercadolibre.android.andesui.textfield

import android.os.Build
import com.facebook.soloader.SoLoader
import com.mercadolibre.android.andesui.BuildConfig
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [Build.VERSION_CODES.LOLLIPOP])
class AndesEditTextTest {

    private var context = RuntimeEnvironment.application
    private lateinit var andesEditText: AndesEditText
    @Mock private lateinit var textContextMenuItemListener: AndesEditText.OnTextContextMenuItemListener

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
        andesEditText = AndesEditText(context)
        andesEditText.setOnTextContextMenuItemListener(textContextMenuItemListener)
    }

    @Test
    fun `when paste text`() {
        `when`(textContextMenuItemListener.onPaste()).thenReturn(true)
        val expected = true
        val actual = andesEditText.onTextContextMenuItem(android.R.id.paste)

        assertEquals(expected, actual)
        verify(textContextMenuItemListener).onPaste()
    }

    @Test
    fun `when copy text`() {
        `when`(textContextMenuItemListener.onCopy()).thenReturn(true)
        val expected = true
        val actual = andesEditText.onTextContextMenuItem(android.R.id.copy)

        assertEquals(expected, actual)
        verify(textContextMenuItemListener).onCopy()
    }

    @Test
    fun `when cut text`() {
        `when`(textContextMenuItemListener.onCut()).thenReturn(true)
        val expected = true
        val actual = andesEditText.onTextContextMenuItem(android.R.id.cut)

        assertEquals(expected, actual)
        verify(textContextMenuItemListener).onCut()
    }

    @Test
    fun `when OnTextContextMenuItemListener not implemented`() {
        val mock = mock<AndesEditText.OnTextContextMenuItemListener>()
        andesEditText.setOnTextContextMenuItemListener(mock)
        var actual = andesEditText.onTextContextMenuItem(android.R.id.paste)

        assertEquals(true, actual)
        actual = verify(mock).onPaste()
        assertEquals(false, actual)

        actual = andesEditText.onTextContextMenuItem(android.R.id.copy)

        assertEquals(true, actual)
        actual = verify(mock).onCopy()
        assertEquals(false, actual)

        actual = andesEditText.onTextContextMenuItem(android.R.id.cut)

        assertEquals(true, actual)
        actual = verify(mock).onCut()
        assertEquals(false, actual)
    }
}