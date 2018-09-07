package com.greg.view

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior
import com.sun.javafx.scene.control.behavior.KeyBinding
import com.sun.javafx.scene.control.skin.ComboBoxPopupControl
import javafx.application.Application
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import javafx.util.StringConverter
import tornadofx.App
import tornadofx.View
import tornadofx.vbox
import java.util.*

class ArrayEditorTest : View() {

    class Picker : ComboBoxBase<String> {

        private val customColors = FXCollections.observableArrayList<String>()

        fun getCustomColors(): ObservableList<String> {
            return customColors
        }

        constructor() : this("White")

        constructor(color: String) {
            value = color
        }

        override fun createDefaultSkin(): Skin<*> {
            return TestSkin(this)
        }

        //        menuButton..addAll(new MenuItem("Really"), new MenuItem("Do not"));
        init {

        }
    }

    override val root = vbox {
        val menuButton = MenuButton("Don't touch this")
        menuButton.items.addAll(MenuItem("Really"), MenuItem("Do not"))
        add(menuButton)
        stylesheets.add("stylesheet.css")
        setPrefSize(150.0, 150.0)
        add(Picker())
    }

    class TestBehavior(datePicker: Picker) : ComboBoxBaseBehavior<String>(datePicker, DATE_PICKER_BINDINGS) {

        override fun onAutoHide() {
            // when we click on some non-interactive part of the
            // calendar - we do not want to hide.
            val datePicker = control as Picker
            val cpSkin = datePicker.skin as TestSkin
            cpSkin.syncWithAutoUpdate()
            // if the DatePicker is no longer showing, then invoke the super method
            // to keep its show/hide state in sync.
            if (!datePicker.isShowing) super.onAutoHide()
        }

        companion object {

            /***************************************************************************
             * *
             * Key event handling                                                      *
             * *
             */

            protected val DATE_PICKER_BINDINGS: MutableList<KeyBinding> = ArrayList()

            init {
                DATE_PICKER_BINDINGS.addAll(ComboBoxBaseBehavior.COMBO_BOX_BASE_BINDINGS)
            }
        }

    }
    class TestSkin(private val datePicker: Picker) : ComboBoxPopupControl<String>(datePicker, TestBehavior(datePicker)) {
        private var displayNode: TextField? = null
        private var datePickerContent: VBox? = null

        init {

            // The "arrow" is actually a rectangular svg icon resembling a calendar.
            // Round the size of the icon to whole integers to get sharp edges.
            arrow.paddingProperty().addListener(object : InvalidationListener {
                // This boolean protects against unwanted recursion.
                private var rounding = false

                override fun invalidated(observable: Observable) {
                    if (!rounding) {
                        val padding = arrow.padding
                        val rounded = Insets(Math.round(padding.top).toDouble(), Math.round(padding.right).toDouble(),
                                Math.round(padding.bottom).toDouble(), Math.round(padding.left).toDouble())
                        if (rounded != padding) {
                            rounding = true
                            arrow.padding = rounded
                            rounding = false
                        }
                    }
                }
            })

//            registerChangeListener(datePicker.chronologyProperty(), "CHRONOLOGY")
//            registerChangeListener(datePicker.converterProperty(), "CONVERTER")
//            registerChangeListener(datePicker.dayCellFactoryProperty(), "DAY_CELL_FACTORY")
//            registerChangeListener(datePicker.showWeekNumbersProperty(), "SHOW_WEEK_NUMBERS")
            registerChangeListener(datePicker.valueProperty(), "VALUE")
        }

        public override fun getPopupContent(): Node {
            if (datePickerContent == null) {
//                if (datePicker.chronology is HijrahChronology) {
//                    datePickerContent = DatePickerHijrahContent(datePicker)
//                } else {
//                    datePickerContent = DatePickerContent(datePicker)
//                }
            }

            return Rectangle()//datePickerContent
        }

        override fun computeMinWidth(height: Double,
                                     topInset: Double, rightInset: Double,
                                     bottomInset: Double, leftInset: Double): Double {
            return 50.0
        }

        override fun focusLost() {
            // do nothing
        }


        override fun show() {
            super.show()
//            datePickerContent!!.clearFocus()
        }

        override fun handleControlPropertyChanged(p: String?) {

            if ("CHRONOLOGY" == p || "DAY_CELL_FACTORY" == p) {

                updateDisplayNode()
                //             if (datePickerContent != null) {
                //                 datePickerContent.refresh();
                //             }
                datePickerContent = null
                popup = null
            } else if ("CONVERTER" == p) {
                updateDisplayNode()
            } else if ("EDITOR" == p) {
                editableInputNode
            } else if ("SHOWING" == p) {
                if (datePicker.isShowing) {
                    if (datePickerContent != null) {
                        val date = datePicker.value
//                        datePickerContent!!.displayedYearMonthProperty().set(if (date != null) YearMonth.from(date) else YearMonth.now())
//                        datePickerContent!!.updateValues()
                    }
                    show()
                } else {
                    hide()
                }
            } else if ("SHOW_WEEK_NUMBERS" == p) {
                if (datePickerContent != null) {
//                    datePickerContent!!.updateGrid()
//                    datePickerContent!!.updateWeeknumberDateCells()
                }
            } else if ("VALUE" == p) {
                updateDisplayNode()
                if (datePickerContent != null) {
                    val date = datePicker.value
//                    datePickerContent!!.displayedYearMonthProperty().set(if (date != null) YearMonth.from(date) else YearMonth.now())
//                    datePickerContent!!.updateValues()
                }
                datePicker.fireEvent(ActionEvent())
            } else {
                super.handleControlPropertyChanged(p)
            }
        }

        override fun getEditor(): TextField {
            // Use getSkinnable() here because this method is called from
            // the super constructor before datePicker is initialized.
            return FakeFocusTextField()//(skinnable as Picker).editor
        }

        override fun getConverter(): StringConverter<String> {
            return object : StringConverter<String>() {
                override fun toString(`object`: String?): String {
                    return `object`!!
                }

                override fun fromString(string: String?): String {
                    return string!!
                }

            }
        }

        override fun getDisplayNode(): Node {
            if (displayNode == null) {
                displayNode = editableInputNode
                displayNode!!.styleClass.add("date-picker-display-node")
                updateDisplayNode()
            }
            displayNode!!.isEditable = datePicker.isEditable

            return displayNode!!
        }

        fun syncWithAutoUpdate() {
            if (!getPopup().isShowing && datePicker.isShowing) {
                // Popup was dismissed. Maybe user clicked outside or typed ESCAPE.
                // Make sure DatePicker button is in sync.
                datePicker.hide()
            }
        }
    }
}

class ArrayEditorTestApp: App(ArrayEditorTest::class)

fun main(args: Array<String>) {
    Application.launch(ArrayEditorTestApp::class.java, *args)
}