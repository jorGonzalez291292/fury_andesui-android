package com.mercadolibre.android.andesui.textfield.state

/**
 * Utility class that does two things: Defines the possible states an [AndesTextfieldCode] can take.
 * But as a bonus it gives you the proper implementation so you don't have to make any mapping.
 *
 * You ask me with, let's say 'IDLE', and then I'll give you a proper implementation of that state.
 *
 * @property state Possible states that an [AndesTextfieldCode] may take.
 */
enum class AndesTextfieldCodeState {
    IDLE,
    DISABLED,
    ERROR;

    internal val state get() = getAndesTextfieldCodeState()

    private fun getAndesTextfieldCodeState(): AndesTextfieldStateInterface {
        return when (this) {
            IDLE -> AndesIdleTextfieldState
            ERROR -> AndesErrorTextfieldState
            DISABLED -> AndesDisabledTextfieldState
        }
    }

    companion object {
        fun fromString(value: String): AndesTextfieldCodeState = valueOf(value.toUpperCase())
    }
}