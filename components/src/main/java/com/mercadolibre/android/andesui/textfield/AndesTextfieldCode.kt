package com.mercadolibre.android.andesui.textfield

import android.content.ClipboardManager
import android.content.Context
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.text.InputType
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldCodeAttrs
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldCodeAttrsParser
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldCodeConfiguration
import com.mercadolibre.android.andesui.textfield.factory.AndesTextfieldCodeConfigurationFactory
import com.mercadolibre.android.andesui.textfield.state.AndesTextfieldCodeState
import com.mercadolibre.android.andesui.textfield.style.AndesTextfieldCodeStyle
import com.mercadolibre.android.andesui.textfield.textwatcher.AndesCodeFocusManagement
import com.mercadolibre.android.andesui.textfield.textwatcher.AndesCodeTextChangedHandler
import com.mercadolibre.android.andesui.textfield.textwatcher.AndesTextfieldBoxWatcher
import com.mercadolibre.android.andesui.textfield.textwatcher.AndesTextfieldBoxWatcher.Companion.DIRTY_CHARACTER
import kotlinx.android.parcel.Parcelize
import kotlin.math.min


@Suppress("TooManyFunctions")
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
            setupEnabledView(config)
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
            val config = createConfig()
            setUpFocusManagement(config)
            setUpAndesTextfieldCodeWatcher(config)
            setupBoxStyleComponent(config)
        }

    private lateinit var andesTextfieldCodeAttrs: AndesTextfieldCodeAttrs
    private lateinit var textfieldCodeContainer: ConstraintLayout
    private lateinit var textfieldBoxCodeContainer: LinearLayout
    private lateinit var labelComponent: TextView
    private lateinit var helperComponent: TextView
    private lateinit var iconComponent: SimpleDraweeView
    private lateinit var textChangedHandler: AndesCodeTextChangedHandler
    private lateinit var focusManagement: AndesCodeFocusManagement
    private var currentText: String? = null
    private var onCompletionListener: OnCompletionListener? = null
    private var onTextChangeListener: OnTextChangeListener? = null

    constructor(
        context: Context,
        label: String? = LABEL_DEFAULT,
        helpLabel: String? = HELP_LABEL_DEFAULT,
        style: AndesTextfieldCodeStyle = STYLE_DEFAULT,
        state: AndesTextfieldCodeState = STATE_DEFAULT) : super(context) {

        initAttrs(label, helpLabel, style, state)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(attrs)
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
        setupComponents(config)
    }

    private fun setupComponents(config: AndesTextfieldCodeConfiguration) {
        initComponents()
        setupViewId()
        setupEnabledView(config)
        setupViewAsClickable()
        setupLabelComponent(config)
        setupHelperComponent(config)
        setUpFocusManagement(config)
        setUpAndesTextfieldCodeWatcher(config)
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

    private fun setupEnabledView(config: AndesTextfieldCodeConfiguration) {
        isEnabled = config.isEnable
        textfieldBoxCodeContainer.isEnabled = isEnabled
        textfieldCodeContainer.isEnabled = isEnabled
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
        currentText?.takeIf { it.isNotEmpty() }?.let { setupTextComponent(it) }
    }

    /**
     * Gets data from the config and create an instance of AndesCodeTextChangedHandler
     */
    private fun setUpAndesTextfieldCodeWatcher(config: AndesTextfieldCodeConfiguration) {
        textChangedHandler = AndesCodeTextChangedHandler(config.boxesPattern.sum(),
            onChange = { text ->
                text.takeIf { it != currentText }?.let {
                    currentText = it
                    onTextChangeListener?.onChange(it)
                }
            },
            onComplete = { isFull -> onCompletionListener?.onComplete(isFull) })
    }

    /**
     * Gets data from the config and create an instance of AndesCodeFocusManagement
     */
    private fun setUpFocusManagement(config: AndesTextfieldCodeConfiguration) {
        focusManagement = AndesCodeFocusManagement(config.boxesPattern.sum() - 1) { nextFocus, previousFocus ->
            getBoxAt(previousFocus)?.also {
                it.setAndesFocusableInTouchMode(false)
            }
            getBoxAt(nextFocus)?.also {
                it.setAndesFocusableInTouchMode(true)
                it.requestFocusOnTextField()
                if (nextFocus < previousFocus) {
                    it.text = DIRTY_CHARACTER
                    it.setSelection(DIRTY_CHARACTER.length)
                }
            }
        }
    }

    /**
     * Gets data from the config to create an instance of AndesTextfield and add it to the container.
     */
    private fun setUpBoxView(config: AndesTextfieldCodeConfiguration) {
        val boxView = AndesTextfield(
            context = context,
            state = config.boxState,
            counter = 2,
            inputType = InputType.TYPE_CLASS_NUMBER).also {
            it.setAndesTextAlignment(View.TEXT_ALIGNMENT_CENTER)
            it.showCounter = false
        }
        textfieldBoxCodeContainer.addView(boxView)
        boxView.layoutParams = (boxView.layoutParams as LinearLayout.LayoutParams).also { it.width = config.boxWidth }

        val indexOfBox = textfieldBoxCodeContainer.indexOfChild(boxView)

        setOnFocusChangeListener(boxView)
        setTextWatcher(boxView, indexOfBox)
        setOnCreateContextMenuListenerTextField(boxView, indexOfBox)
        if (indexOfBox > 0) {
            boxView.setAndesFocusableInTouchMode(false)
        }
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
    private fun setupTextComponent(newText: String?, startIndex: Int = 0) {
        val cleanText = cleanText(newText)
        if (cleanText != null) {
            textChangedHandler.reset(startIndex)
        }
        when {
            cleanText == null -> Unit
            cleanText.isEmpty() -> cleanBoxes()
            cleanText.isNotEmpty() -> setTextInBoxes(cleanText, startIndex)
        }
    }


    private fun setTextInBoxes(cleanText: String, startIndex: Int = 0) {
        val childCount = textfieldBoxCodeContainer.childCount
        val textArray = Array(childCount) { DIRTY_CHARACTER }
        IntRange(0, min(textArray.lastIndex, cleanText.lastIndex)).forEach { textArray[it] = "${cleanText[it]}" }

        if (startIndex == 0) {
            focusManagement.reset()
        }
        var charIndex = 0
        var lastIsDirty = false
        val emptyBoxes = childCount - startIndex
        val endIndex = (startIndex + min(emptyBoxes - 1, textArray.lastIndex))
        val indices = IntRange(startIndex, endIndex)

        foreachBox(indices) { _, boxView ->
            var boxText = textArray[charIndex++]
            boxText = boxText.takeIf { it == DIRTY_CHARACTER } ?: "$DIRTY_CHARACTER$boxText"

            if (boxText == DIRTY_CHARACTER) {
                boxView.setAndesFocusableInTouchMode(!lastIsDirty)
                lastIsDirty = true
            }

            with(boxView) {
                text = boxText
                setSelection(boxText.length)
            }
        }
    }

    private fun cleanBoxes() {
        var childCount = textfieldBoxCodeContainer.childCount
        focusManagement.reset(childCount - 1)
        for (index in --childCount downTo 0) {
            getBoxAt(index)?.let { boxView ->
                boxView.text = ""
            }
        }
    }

    private fun cleanText(newText: String?): String? {
        if (newText.isNullOrEmpty()) {
            return ""
        }

        return newText.replace("\\D+".toRegex(), "").takeIf { it.isNotEmpty() }
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
        val childCount = textfieldBoxCodeContainer.childCount
        foreachBox(0 until childCount) { _, boxView ->
            boxView.state = config.boxState
        }
    }

    private fun setOnCreateContextMenuListenerTextField(textfield: AndesTextfield, indexView: Int) {
        textfield.setAndesTextContextMenuItemListener(object : AndesEditText.OnTextContextMenuItemListener {
            override fun onPaste(): Boolean {
                val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager?
                val textToPaste = clipboard?.primaryClip?.getItemAt(0)?.text
                textToPaste?.let { setupTextComponent("$it", indexView) }
                return true
            }
        })
    }

    private fun setOnFocusChangeListener(textfield: AndesTextfield) {
        textfield.setAndesFocusChangeListener(OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && textfield.text.isNullOrEmpty()) {
                with(textfield) {
                    text = DIRTY_CHARACTER
                    setSelection(DIRTY_CHARACTER.length)
                }

            }
        })
    }

    private fun setTextWatcher(textfield: AndesTextfield, indexView: Int) {
        textfield.textWatcher = AndesTextfieldBoxWatcher(textChangedHandler, focusManagement, indexView)
    }

    /**
     * Set a handler to listen when the boxes are full.
     */
    fun setOnCompleteListener(handler: OnCompletionListener) {
        onCompletionListener = handler
    }

    /**
     * Set a handler to listen when there is a change of text in the boxes.
     */
    fun setOnTextChangeListener(handler: OnTextChangeListener) {
        onTextChangeListener = handler
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

    interface OnTextChangeListener {
        fun onChange(text: String)
    }

    interface OnCompletionListener {
        fun onComplete(isFull: Boolean)
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable?>?) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable?>?) {
        dispatchThawSelfOnly(container)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(currentText.orEmpty(),
            state.name,
            style.name,
            label.orEmpty(),
            helper.orEmpty(),
            super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(savedState: Parcelable) {
        if(savedState is SavedState) {
            super.onRestoreInstanceState(savedState.superState)
            currentText = savedState.currentText
            state = AndesTextfieldCodeState.valueOf(savedState.state)
            style = AndesTextfieldCodeStyle.valueOf(savedState.style)
            label = savedState.label
            helper = savedState.helper
        } else {
            super.onRestoreInstanceState(savedState)
        }
    }

    @Parcelize
    internal data class SavedState (
        val currentText: String,
        val state: String,
        val style: String,
        val label: String,
        val helper: String,
        val superState: Parcelable?
    ): Parcelable

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