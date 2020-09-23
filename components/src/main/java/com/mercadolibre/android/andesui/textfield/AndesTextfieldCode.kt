package com.mercadolibre.android.andesui.textfield

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.textfield.factory.*
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldCodeAttrs
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldCodeAttrsParser
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldCodeConfigurationFactory
import com.mercadolibre.android.andesui.textfield.state.AndesTextfieldCodeState
import com.mercadolibre.android.andesui.textfield.style.AndesTextfieldCodeStyle

class AndesTextfieldCode : ConstraintLayout {

    /**
     * Getter and setter for [text].
     */
    var text: String?
        get() = currentText
        set(value) {
            setupTextComponent(value)
        }

    /**
     * Getter and setter for [label].
     */
    var label: String?
        get() = andesTextfieldCodeAttrs.label
        set(value) {
            andesTextfieldCodeAttrs = andesTextfieldCodeAttrs.copy(label = value)
            setupLabelComponent(createConfig())
        }

    /**
     * Getter and setter for [helper].
     */
    var helper: String?
        get() = andesTextfieldCodeAttrs.helper
        set(value) {
            andesTextfieldCodeAttrs = andesTextfieldCodeAttrs.copy(helper = value)
            val config = createConfig()
            setupHelperComponent(config)
        }

    /**
     * Getter and setter for the [state].
     */
    var state: AndesTextfieldCodeState
        get() = andesTextfieldCodeAttrs.state
        set(value) {
            andesTextfieldCodeAttrs = andesTextfieldCodeAttrs.copy(state = value)
            val config = createConfig()
            setupEnabledView()
            setupColorComponents(config)
            setupBoxStateComponent(config)
        }

    /**
     * Getter and setter for the [style].
     */
    var style: AndesTextfieldCodeStyle
        get() = andesTextfieldCodeAttrs.style
        set(value) {
            andesTextfieldCodeAttrs = andesTextfieldCodeAttrs.copy(style = value)
            setupBoxStyleComponent(createConfig())
        }

    private lateinit var andesTextfieldCodeAttrs: AndesTextfieldCodeAttrs
    private lateinit var textfieldCodeContainer: ConstraintLayout
    private lateinit var textfieldBoxCodeContainer: LinearLayout
    private lateinit var labelComponent: TextView
    private lateinit var helperComponent: TextView
    private lateinit var iconComponent: SimpleDraweeView
    private var currentText: String? = null

    constructor(context: Context?) : super(context) {
        initAttrs(LABEL_DEFAULT, HELP_LABEL_DEFAULT, STYLE_DEFAULT, STATE_DEFAULT)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(attrs)
    }

    constructor(
        context: Context,
        label: String?, helpLabel: String?,
        style: AndesTextfieldCodeStyle,
        state: AndesTextfieldCodeState) : super(context) {

        initAttrs(label, helpLabel, style, state)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        andesTextfieldCodeAttrs = AndesTextfieldCodeAttrsParser.parse(context, attrs)
        val config = AndesTextfieldCodeConfigurationFactory.create(context, andesTextfieldCodeAttrs)
        setupComponents(config)
    }

    private fun initAttrs(
        label: String?,
        helpLabel: String?,
        style: AndesTextfieldCodeStyle,
        state: AndesTextfieldCodeState) {
        andesTextfieldCodeAttrs = AndesTextfieldCodeAttrs(label, helpLabel, style, state)
        val config = AndesTextfieldCodeConfigurationFactory.create(context, andesTextfieldCodeAttrs)
        setupColorComponents(config)
    }

    private fun setupComponents(config: AndesTextfieldCodeConfiguration) {
        initComponents()
        setupViewId()
        setupEnabledView()
        setupViewAsClickable()
        setupLabelComponent(config)
        setupHelperComponent(config)
        setupBoxStyleComponent(config)
        setupColorComponents(config)
    }

    /**
     * Creates all the views that are part of this textfieldcode.
     * After a view is created then a view id is added to it.
     */
    private fun initComponents() {
        val container = LayoutInflater.from(context).inflate(R.layout.andes_layout_code_textfield, this, true)

        textfieldCodeContainer = container as ConstraintLayout
        textfieldBoxCodeContainer = container.findViewById(R.id.andes_textfield_box_code_container)
        labelComponent = container.findViewById(R.id.andes_textfield_code_label)
        helperComponent = container.findViewById(R.id.andes_textfield_code_helper)
        iconComponent = container.findViewById(R.id.andes_textfield_code_icon)
    }

    private fun setupViewId() {
        if (id == NO_ID) { // If this view has no id
            id = View.generateViewId()
        }
    }

    private fun setupViewAsClickable() {
        isFocusable = true
        textfieldBoxCodeContainer.isClickable = true
        textfieldBoxCodeContainer.isFocusable = true
    }

    private fun setupEnabledView() {
        if (state == AndesTextfieldCodeState.DISABLED) {
            isEnabled = false
            textfieldBoxCodeContainer.isEnabled = isEnabled
            textfieldCodeContainer.isEnabled = isEnabled
        } else {
            isEnabled = true
            textfieldBoxCodeContainer.isEnabled = isEnabled
            textfieldCodeContainer.isEnabled = isEnabled
        }
    }

    /**
     * Gets data from the config and sets to the Label component.
     */
    private fun setupLabelComponent(config: AndesTextfieldCodeConfiguration) {
        if (config.labelText.isNullOrEmpty()) {
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
    private fun setupHelperComponent(config: AndesTextfieldCodeConfiguration) {
        if (config.helperText.isNullOrEmpty()) {
            helperComponent.visibility = View.GONE
        } else {
            helperComponent.visibility = View.VISIBLE
            helperComponent.text = config.helperText
            helperComponent.setTextSize(TypedValue.COMPLEX_UNIT_PX, config.helperSize)
        }
    }

    /**
     * Gets data from the config and set Style for boxes.
     */
    private fun setupBoxStyleComponent(config: AndesTextfieldCodeConfiguration) {
        textfieldBoxCodeContainer.removeAllViews()
        val boxesIterator = config.boxesPattern.iterator()
        while (boxesIterator.hasNext()) {
            var boxes = boxesIterator.next()
            while (boxes-- > 0) {
                setUpBoxView(config)
                setupMarginBetweenBoxes(config, boxes == 0)
            }
        }
        setupTextComponent(currentText)
    }

    /**
     * Gets data from the config to create an instance of AndesTextfield and add it to the container.
     */
    private fun setUpBoxView(config: AndesTextfieldCodeConfiguration) {
        val boxView = AndesTextfield(
            context = context,
            state = config.boxState,
            counter = 1,
            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL).also {
            it.setAndesTextAlignment(View.TEXT_ALIGNMENT_CENTER)
            it.showCounter = false
        }
        textfieldBoxCodeContainer.addView(boxView)
        boxView.layoutParams = (boxView.layoutParams as LinearLayout.LayoutParams).also { it.width = config.boxWidth }
    }

    /**
     * Gets margin data from the config and sets them in the boxes.
     */
    private fun setupMarginBetweenBoxes(config: AndesTextfieldCodeConfiguration, isGroup: Boolean) {
        val childIndex = textfieldBoxCodeContainer.childCount - 1
        val boxView = textfieldBoxCodeContainer.getChildAt(childIndex)
        boxView.layoutParams = (boxView.layoutParams as LinearLayout.LayoutParams).also { params ->
            params.marginEnd = if (isGroup) {
                config.marginBetweenGroups
            } else {
                config.marginBetweenBoxes
            }
        }
    }

    /**
     * Sets each character of newText in the boxes.
     */
    private fun setupTextComponent(newText: String?) {
        var childCount = textfieldBoxCodeContainer.childCount
        if (!newText.isNullOrEmpty()) {
            val chars = newText.replace("\\D+".toRegex(), "").also {
                if (it.length >= childCount) {
                    it.substring(0, childCount)
                }
                currentText = it
            }.toCharArray()

            foreachBox(chars.indices) { index, boxView ->
                boxView.text = chars[index].toString()
            }
        } else {
            currentText = null
            foreachBox(childCount--..0) { _, boxView ->
                boxView.text = currentText
            }
        }
    }

    /**
     * Gets data from the config to sets the color of the components regarding to the state.
     */
    private fun setupColorComponents(config: AndesTextfieldCodeConfiguration) {
        iconComponent.setImageDrawable(config.icon)
        if (config.icon != null) {
            iconComponent.visibility = if (!config.helperText.isNullOrEmpty()) View.VISIBLE else View.GONE
        } else {
            iconComponent.visibility = View.GONE
        }

        helperComponent.setTextColor(config.helperColor.colorInt(context))
        helperComponent.typeface = config.helperTypeface

        labelComponent.setTextColor(config.labelColor.colorInt(context))
        labelComponent.typeface = config.typeface
    }

    /**
     * Gets data from the config to sets the state in the boxes.
     */
    private fun setupBoxStateComponent(config: AndesTextfieldCodeConfiguration) {
        val childCount = textfieldBoxCodeContainer.childCount - 1
        foreachBox(childCount..0) { _, boxView ->
            boxView.state = config.boxState
        }
    }

    private fun foreachBox(range: IntRange, action: (Int, AndesTextfield) -> Unit) {
        for (index in range) {
            getBoxAt(index)?.let {
                action(index, it)
            }
        }
    }

    private fun getBoxAt(index: Int): AndesTextfield? {
        return textfieldBoxCodeContainer.getChildAt(index) as AndesTextfield?
    }

    private fun createConfig() = AndesTextfieldCodeConfigurationFactory.create(context, andesTextfieldCodeAttrs)

    /**
     * Default values for AndesTextfieldCode basic properties
     */
    companion object {
        private val LABEL_DEFAULT = null
        private val HELP_LABEL_DEFAULT = null
        private val STATE_DEFAULT = AndesTextfieldCodeState.IDLE
        private val STYLE_DEFAULT = AndesTextfieldCodeStyle.THREESOME
    }
}