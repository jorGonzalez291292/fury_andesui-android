package com.mercadolibre.android.andesui.textfield

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.constraint.ConstraintLayout
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.button.AndesButton
import com.mercadolibre.android.andesui.color.AndesColor
import com.mercadolibre.android.andesui.color.toAndesColor
import com.mercadolibre.android.andesui.icons.IconProvider
import com.mercadolibre.android.andesui.textfield.content.AndesTextfieldLeftContent
import com.mercadolibre.android.andesui.textfield.content.AndesTextfieldRightContent
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldAttrs
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldAttrsParser
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldConfiguration
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldConfigurationFactory
import com.mercadolibre.android.andesui.textfield.maskTextField.TextFieldMaskWatcher
import com.mercadolibre.android.andesui.textfield.state.AndesTextfieldState
import com.mercadolibre.android.andesui.utils.buildColoredAndesBitmapDrawable

@Suppress("TooManyFunctions")
class AndesTextfield : ConstraintLayout {

    /**
     * Getter and setter for [text].
     */
    var text: String?
        get() = textComponent.text.toString()
        set(value) {
            textComponent.setText(maskWatcher?.getTextWithMask(value) ?: value)
            setupCounterComponent(createConfig())
        }

    /**
     * Getter and setter for [label].
     */
    var label: String?
        get() = andesTextfieldAttrs.label
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(label = value)
            setupLabelComponent(createConfig())
        }

    /**
     * Getter and setter for [helper].
     */
    var helper: String?
        get() = andesTextfieldAttrs.helper
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(helper = value)
            val config = createConfig()
            setupColorComponents(config)
            setupHelperComponent(config)
        }

    /**
     * Getter and setter for [placeholder].
     */
    var placeholder: String?
        get() = andesTextfieldAttrs.placeholder
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(placeholder = value)
            setupPlaceHolderComponent(createConfig())
        }

    /**
     * Getter and setter for [counter].
     */
    var counter: Int
        get() = andesTextfieldAttrs.counter
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(counter = value)
            setupCounterComponent(createConfig())
        }

    /**
     * Internal countFilter to limit field size
     */
    private var countFilter: InputFilter? = null
        set(value) {
            field = value
            updateFilters()
        }

    /**
     * Getter and setter for [showCounter].
     */
    var showCounter: Boolean
        get() = andesTextfieldAttrs.showCounter
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(showCounter = value)
            setupCounterComponent(createConfig())
        }

    /**
     * Getter and setter for the [state].
     */
    var state: AndesTextfieldState
        get() = andesTextfieldAttrs.state
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(state = value)
            val config = createConfig()
            setupEnabledView()
            setupColorComponents(config)
            setupHelperComponent(config)
            setupCounterComponent(config)
        }

    /**
     * Getter and setter for the [leftContent].
     */
    var leftContent: AndesTextfieldLeftContent?
        get() = andesTextfieldAttrs.leftContent
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(leftContent = value)
            setupLeftComponent(createConfig())
            setupEnabledView()
        }

    /**
     * Getter and setter for the [rightContent].
     */
    var rightContent: AndesTextfieldRightContent?
        get() = andesTextfieldAttrs.rightContent
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(rightContent = value)
            setupRightComponent(createConfig())
            setupEnabledView()
        }

    /**
     * Getter and setter for the [rightContent].
     */
    var inputType: Int
        get() = andesTextfieldAttrs.inputType
        set(value) {
            andesTextfieldAttrs = andesTextfieldAttrs.copy(inputType = value)
            setupInputType()
        }

    /**
     * Getter and setter for the [textWatcher].
     */
    var textWatcher: TextWatcher?
        get() = andesTextfieldAttrs.textWatcher
        set(value) {
            removeCurrentTextWatcher()
            andesTextfieldAttrs = andesTextfieldAttrs.copy(textWatcher = value)
            setupTextWatcher()
        }

    /**
     * This field applies the given filter to the EditText input.
     */
    var textFilter: InputFilter? = null
        set(value) {
            field = value
            updateFilters()
        }

    /**
     * This field applies the given digits string to the EditText
     */
    var textDigits: String? = null
        set(value) {
            if (value.isNullOrEmpty()) {
                field = null
                textComponent.keyListener = null
            } else {
                field = value
                textComponent.keyListener = DigitsKeyListener.getInstance(value)
            }
        }

    private lateinit var andesTextfieldAttrs: AndesTextfieldAttrs
    private lateinit var textfieldContainer: ConstraintLayout
    private lateinit var textContainer: ConstraintLayout
    private lateinit var labelComponent: TextView
    private lateinit var helperComponent: TextView
    private lateinit var counterComponent: TextView
    private lateinit var textComponent: AndesEditText
    private lateinit var iconComponent: SimpleDraweeView
    private lateinit var leftComponent: FrameLayout
    private lateinit var rightComponent: FrameLayout
    private var maskWatcher: TextFieldMaskWatcher? = null

    @Suppress("unused")
    constructor(context: Context) : super(context) {
        initAttrs(LABEL_DEFAULT, HELPER_DEFAULT, PLACEHOLDER_DEFAULT, COUNTER_DEFAULT,
            STATE_DEFAULT, LEFT_COMPONENT_DEFAULT, RIGHT_COMPONENT_DEFAULT, INPUT_TYPE_DEFAULT)
    }

    @Suppress("LongParameterList")
    constructor(
        context: Context,
        label: String? = LABEL_DEFAULT,
        helper: String? = HELPER_DEFAULT,
        placeholder: String? = PLACEHOLDER_DEFAULT,
        counter: Int = COUNTER_DEFAULT,
        state: AndesTextfieldState = STATE_DEFAULT,
        leftContent: AndesTextfieldLeftContent? = LEFT_COMPONENT_DEFAULT,
        rightContent: AndesTextfieldRightContent? = RIGHT_COMPONENT_DEFAULT,
        inputType: Int = INPUT_TYPE_DEFAULT
    ) : super(context) {
        initAttrs(label, helper, placeholder, counter, state, leftContent, rightContent, inputType)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs) {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        andesTextfieldAttrs = AndesTextfieldAttrsParser.parse(context, attrs)
        val config = AndesTextfieldConfigurationFactory.create(context, andesTextfieldAttrs)
        setupComponents(config)
    }

    @Suppress("LongParameterList")
    private fun initAttrs(
        label: String?,
        helper: String?,
        placeholder: String?,
        counter: Int,
        state: AndesTextfieldState,
        leftContent: AndesTextfieldLeftContent?,
        rightContent: AndesTextfieldRightContent?,
        inputType: Int
    ) {
        val showCounter = SHOW_COUNTER_DEFAULT
        andesTextfieldAttrs = AndesTextfieldAttrs(
            label, helper, placeholder, counter, showCounter, state, leftContent, rightContent, inputType
        )
        val config = AndesTextfieldConfigurationFactory.create(context, andesTextfieldAttrs)
        setupComponents(config)
    }

    private fun setupComponents(config: AndesTextfieldConfiguration) {
        initComponents()
        setupViewId()
        setupViewAsClickable()
        setupEnabledView()
        setupLabelComponent(config)
        setupHelperComponent(config)
        setupCounterComponent(config)
        setupPlaceHolderComponent(config)
        setupLeftComponent(config)
        setupRightComponent(config)
        setupColorComponents(config)
        setupInputType()
        setupTextWatcher()
        setupTextComponent(config)
    }

    /**
     * Creates all the views that are part of this textfield.
     * After a view is created then a view id is added to it.
     */
    private fun initComponents() {
        val container = LayoutInflater.from(context).inflate(R.layout.andes_layout_textfield, this, true)

        textfieldContainer = container.findViewById(R.id.andes_textfield_container)
        textContainer = container.findViewById(R.id.andes_textfield_text_container)
        labelComponent = container.findViewById(R.id.andes_textfield_label)
        helperComponent = container.findViewById(R.id.andes_textfield_helper)
        counterComponent = container.findViewById(R.id.andes_textfield_counter)
        iconComponent = container.findViewById(R.id.andes_textfield_icon)
        textComponent = container.findViewById(R.id.andes_textfield_edittext)
        leftComponent = container.findViewById(R.id.andes_textfield_left_component)
        rightComponent = container.findViewById(R.id.andes_textfield_right_component)
    }

    private fun setupViewId() {
        if (id == NO_ID) { // If this view has no id
            id = View.generateViewId()
        }
    }

    private fun setupViewAsClickable() {
        isFocusable = true
        textContainer.isClickable = true
        textContainer.isFocusable = true
    }

    private fun setupEnabledView() {
        if (state == AndesTextfieldState.DISABLED || state == AndesTextfieldState.READONLY) {
            isEnabled = false
            textComponent.isEnabled = isEnabled
            textContainer.isEnabled = isEnabled
            textfieldContainer.isEnabled = isEnabled
        } else {
            isEnabled = true
            textComponent.isEnabled = isEnabled
            textContainer.isEnabled = isEnabled
            textfieldContainer.isEnabled = isEnabled
        }
    }

    /**
     * Set up the text component.
     */
    private fun setupTextComponent(config: AndesTextfieldConfiguration) {
        textComponent.typeface = config.typeface
    }

    /**
     * Set the input type of the edit text.
     */
    private fun setupInputType() {
        textComponent.inputType = inputType
        textComponent.setSelection(textComponent.text?.length ?: 0)
    }

    /**
     * Set the TextWatcher of the edit text.
     */
    private fun setupTextWatcher() {
        andesTextfieldAttrs.textWatcher?.let {
            textComponent.addTextChangedListener(it)
        }
    }

    /**
     * Set the TextWatcher of the edit text.
     */
    private fun removeCurrentTextWatcher() {
        andesTextfieldAttrs.textWatcher?.let {
            textComponent.removeTextChangedListener(it)
        }
    }

    private fun updateFilters() {
        textComponent.filters = listOf(countFilter, textFilter).filterNotNull().toTypedArray()
    }

    /**
     * Gets data from the config to sets the color of the components regarding to the state.
     */
    private fun setupColorComponents(config: AndesTextfieldConfiguration) {
        textContainer.background = config.background

        iconComponent.setImageDrawable(config.icon)
        if (config.icon != null && state != AndesTextfieldState.READONLY) {
            iconComponent.visibility = if (!config.helperText.isNullOrEmpty()) View.VISIBLE else View.GONE
        } else {
            iconComponent.visibility = View.GONE
        }

        helperComponent.setTextColor(config.helperColor.colorInt(context))
        helperComponent.typeface = config.helperTypeface

        labelComponent.setTextColor(config.labelColor.colorInt(context))
        labelComponent.typeface = config.typeface

        counterComponent.setTextColor(config.counterColor.colorInt(context))
        counterComponent.typeface = config.typeface

        textComponent.setHintTextColor(config.placeHolderColor.colorInt(context))
    }

    /**
     * Gets data from the config and sets to the Label component.
     */
    private fun setupLabelComponent(config: AndesTextfieldConfiguration) {
        if (config.labelText == null || config.labelText.isEmpty()) {
            labelComponent.visibility = View.GONE
        } else {
            labelComponent.visibility = View.VISIBLE
            labelComponent.text = config.labelText
            labelComponent.setTextSize(TypedValue.COMPLEX_UNIT_PX, config.labelSize)
        }
    }

    /**
     * Gets data from the config and sets to the Helper component.
     */
    private fun setupHelperComponent(config: AndesTextfieldConfiguration) {
        if (config.helperText == null || config.helperText.isEmpty() || state == AndesTextfieldState.READONLY) {
            helperComponent.visibility = View.GONE
        } else {
            helperComponent.visibility = View.VISIBLE
            helperComponent.text = config.helperText
            helperComponent.setTextSize(TypedValue.COMPLEX_UNIT_PX, config.helperSize)
        }
    }

    /**
     * Gets data from the config and sets to the Counter component.
     */
    private fun setupCounterComponent(config: AndesTextfieldConfiguration) {
        if (config.counterLength != 0 && state != AndesTextfieldState.READONLY && showCounter) {
            counterComponent.visibility = View.VISIBLE
            counterComponent.setTextSize(TypedValue.COMPLEX_UNIT_PX, config.counterSize)
            counterComponent.text = resources.getString(
                R.string.andes_textfield_counter_text,
                getTextLength(),
                config.counterLength
            )
            configTextComponentLengthFilter(config)
        } else {
            counterComponent.visibility = View.GONE
        }

        textComponent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(charSequence: Editable?) {
                // Do nothing.
            }

            override fun beforeTextChanged(charSeqeuence: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

                val textWithoutMask = removeMaskCharsText(charSequence.toString())

                counterComponent.text = resources.getString(
                    R.string.andes_textfield_counter_text,
                    textWithoutMask.length,
                    config.counterLength
                )
            }
        })
    }

    /**
     * config text length text component
     */
    private fun configTextComponentLengthFilter(config: AndesTextfieldConfiguration) {
        val length = maskWatcher?.getMaxLength() ?: 0

        countFilter = if (length > 0) {
            InputFilter.LengthFilter(length)
        } else {
            InputFilter.LengthFilter(config.counterLength)
        }
    }

    private fun getTextLength(): Int {
        val text = text
        return if (text?.isNotEmpty() == true) {
            removeMaskCharsText(text).length
        } else {
            0
        }
    }

    /**
     * if use maskWatcher will remove masks chars
     */
    private fun removeMaskCharsText(text: String?): String {
        val textToClean = text ?: return EMPTY_STRING
        return maskWatcher?.cleanMask(textToClean) ?: textToClean
    }

    /**
     * Gets data from the config and sets to the Place Holder component.
     */
    private fun setupPlaceHolderComponent(config: AndesTextfieldConfiguration) {
        textComponent.hint = config.placeHolderText
        textComponent.typeface = config.typeface
    }

    /**
     * Gets data from the config and sets to the Left component.
     */
    private fun setupLeftComponent(config: AndesTextfieldConfiguration) {
        setupMarginStartTextComponent()

        if (config.leftComponent != null) {
            leftComponent.removeAllViews()
            leftComponent.addView(config.leftComponent)
            val params = leftComponent.layoutParams as LayoutParams
            params.marginStart = config.leftComponentLeftMargin!!
            params.marginEnd = config.leftComponentRightMargin!!
            leftComponent.layoutParams = params

            leftComponent.visibility = View.VISIBLE
        } else {
            leftComponent.visibility = View.GONE
        }
    }

    private fun setupMarginStartTextComponent() {
        val params = textComponent.layoutParams as LayoutParams
        if (state == AndesTextfieldState.READONLY) {
            params.goneStartMargin = context.resources.getDimension(R.dimen.andes_textfield_label_paddingLeft).toInt()
        } else {
            params.goneStartMargin = context.resources.getDimension(R.dimen.andes_textfield_margin).toInt()
        }
        textComponent.layoutParams = params
    }

    /**
     * Gets data from the config and sets to the right component.
     */
    private fun setupRightComponent(config: AndesTextfieldConfiguration) {
        if (config.rightComponent != null) {
            rightComponent.removeAllViews()
            rightComponent.addView(config.rightComponent)

            val params = rightComponent.layoutParams as LayoutParams
            params.marginStart = config.rightComponentLeftMargin!!
            params.marginEnd = config.rightComponentRightMargin!!
            rightComponent.layoutParams = params

            rightComponent.visibility = View.VISIBLE
            setupClear()
        } else {
            rightComponent.visibility = View.GONE
        }
    }

    /**
     * Set the clear action to erase the text.
     */
    private fun setupClear() {
        if (rightContent == AndesTextfieldRightContent.CLEAR) {
            rightComponent.visibility = View.GONE

            textComponent.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(text: Editable?) {
                    if (!text.isNullOrEmpty()) {
                        rightComponent.visibility = View.VISIBLE
                    } else {
                        rightComponent.visibility = View.GONE
                    }
                }

                override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, after: Int) {
                    // Do nothing.
                }

                override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, after: Int) {
                    // Do nothing.
                }
            })

            val clear: SimpleDraweeView = rightComponent.getChildAt(0) as SimpleDraweeView
            clear.setOnClickListener { textComponent.text?.clear() }
        }
    }

    /**
     * Set the right content to action and provides an interface to set button text and callback.
     */
    fun setAction(text: String, onClickListener: OnClickListener) {
        rightContent = AndesTextfieldRightContent.ACTION
        val action: AndesButton = rightComponent.getChildAt(0) as AndesButton
        action.text = text
        action.isEnabled = state != AndesTextfieldState.READONLY && state != AndesTextfieldState.DISABLED
        action.setOnClickListener(onClickListener)
    }

    /**
     * Set the right content to action and provides an interface to set button text.
     */
    fun setTextAction(text: String) {
        if (rightComponent.getChildAt(0) is AndesButton) {
            val action: AndesButton = rightComponent.getChildAt(0) as AndesButton
            action.text = text
        }
    }

    /**
     * Set the right content to icon and provides an interface to give the icon path.
     */
    fun setRightIcon(iconPath: String, listener: OnClickListener? = null, colorIcon: Int? = R.color.andes_gray_800) {
        rightContent = AndesTextfieldRightContent.ICON
        val rightIcon: SimpleDraweeView = rightComponent.getChildAt(0) as SimpleDraweeView

        var color: AndesColor? = null
        if (colorIcon != null) {
            color = colorIcon.toAndesColor()
        }

        rightIcon.setImageDrawable(buildColoredAndesBitmapDrawable(
            IconProvider(context).loadIcon(iconPath) as BitmapDrawable,
            context,
            color = color)
        )

        if (listener != null) {
            rightIcon.setOnClickListener(listener)
        }
    }

    /**
     * Set the left content to icon and provides an interface to give the icon path.
     */
    fun setLeftIcon(iconPath: String) {
        leftContent = AndesTextfieldLeftContent.ICON
        val leftIcon: SimpleDraweeView = leftComponent.getChildAt(0) as SimpleDraweeView
        leftIcon.setImageDrawable(buildColoredAndesBitmapDrawable(
            IconProvider(context).loadIcon(iconPath) as BitmapDrawable,
            context,
            color = R.color.andes_gray_450.toAndesColor())
        )
    }

    /**
     * Set the left content to prefix and provides an interface to give the prefix text.
     */
    fun setPrefix(text: String) {
        leftContent = AndesTextfieldLeftContent.PREFIX
        val prefix: TextView = leftComponent.getChildAt(0) as TextView
        prefix.text = text
    }

    /**
     * Set the right content to suffix and provides an interface to give the suffix text.
     */
    fun setSuffix(text: String) {
        rightContent = AndesTextfieldRightContent.SUFFIX
        val suffix: TextView = rightComponent.getChildAt(0) as TextView
        suffix.text = text
    }

    /**
     * Set mask in text field.
     * @param mask pattern ##-###-######-# digits sample 0123456789-
     * @param digits sample 0123456789-
     * @param listener is listener changeText
     */
    fun setTextFieldMask(
        mask: String = EMPTY_STRING,
        digits: String? = null,
        listener: TextFieldMaskWatcher.OnTextChange? = null
    ) {
        val watcher = maskWatcher ?: TextFieldMaskWatcher(mask, listener).also {
            maskWatcher = it
            textComponent.addTextChangedListener(it)
        }

        watcher.setMask(mask)

        val length = watcher.getLengthWithoutMask()
        if (length > 0) {
            counter = length
            configTextComponentLengthFilter(createConfig())
        }

        textDigits = digits
    }

    /**
     * remove mask in text field.
     */
    fun clearMask() {
        textComponent.removeTextChangedListener(maskWatcher)
        maskWatcher = null
    }

    fun requestFocusOnTextField() {
        textComponent.requestFocus()
    }

    /**
     * Set alignment for textComponent
     * Only visible for internal development
     */
    internal fun setAndesTextAlignment(textAlignment: Int) {
        textComponent.textAlignment = textAlignment
    }

    /**
     * Set selection for textComponent
     * Only visible for internal development
     */
    internal fun setSelection(selection: Int) {
        textComponent.setSelection(selection)
    }

    /**
     * Set isFocusableInTouchMode for textComponent
     * Only visible for internal development
     */
    internal fun setAndesFocusableInTouchMode(isFocusableInTouchMode: Boolean) {
        textComponent.isFocusableInTouchMode = isFocusableInTouchMode
    }

    /**
     * Set isLongClickable for textComponent
     * Only visible for internal development
     */
    internal fun setAndesIsLongClickable(isClickable: Boolean) {
        textComponent.isLongClickable = isClickable
    }

    /**
     * Set onFocusChangeListener for textComponent
     * Only visible for internal development
     */
    internal fun setAndesFocusChangeListener(onFocusChangeListener: OnFocusChangeListener) {
        textComponent.onFocusChangeListener = onFocusChangeListener
    }
    /**
     * Set contextMenuItemListener for textComponent
     * Only visible for internal development
     */
    internal fun setAndesTextContextMenuItemListener(
        contextMenuItemListener: AndesEditText.OnTextContextMenuItemListener) {
        textComponent.setOnTextContextMenuItemListener(contextMenuItemListener)
    }

    private fun createConfig() = AndesTextfieldConfigurationFactory.create(context, andesTextfieldAttrs)

    /**
     * Default values for AndesTextfield basic properties
     */
    companion object {
        private const val EMPTY_STRING = ""
        private val LABEL_DEFAULT = null
        private val HELPER_DEFAULT = null
        private val PLACEHOLDER_DEFAULT = null
        private const val COUNTER_DEFAULT = 0
        private const val SHOW_COUNTER_DEFAULT = true
        private val STATE_DEFAULT = AndesTextfieldState.IDLE
        private val LEFT_COMPONENT_DEFAULT = null
        private val RIGHT_COMPONENT_DEFAULT = null
        private const val INPUT_TYPE_DEFAULT = InputType.TYPE_CLASS_TEXT
    }
}
