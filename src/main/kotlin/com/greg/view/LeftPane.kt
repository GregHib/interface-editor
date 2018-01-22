package src.com.greg.view

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.CheckBoxTreeItem
import javafx.scene.control.SelectionMode
import javafx.scene.control.TabPane
import javafx.scene.control.TreeItem
import javafx.scene.paint.Color
import javafx.util.Callback
import org.controlsfx.control.CheckTreeView
import org.controlsfx.control.GridView
import org.controlsfx.control.cell.ColorGridCell
import tornadofx.View
import tornadofx.splitpane
import tornadofx.tab
import tornadofx.tabpane
import java.util.*

class LeftPane : View() {

    private var checkTreeView: CheckTreeView<String>? = null

    private val treeItem_Jonathan = CheckBoxTreeItem("Widget")
    private val treeItem_Eugene = CheckBoxTreeItem("Text Widget")
    private val treeItem_Henry = CheckBoxTreeItem("Rectangle Widget")
    private val treeItem_Samir = CheckBoxTreeItem("Rectangle Widget")

    override val root = splitpane(Orientation.VERTICAL) {
        minWidth = 290.0
        tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Components") {
                val list = FXCollections.observableArrayList<Color>()

                val colorGrid = GridView(list)

                colorGrid.cellFactory = Callback { ColorGridCell() }
                val r = Random(System.currentTimeMillis())
                for (i in 0..8) {
                    list.add(Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), 1.0))
                }
                add(colorGrid)
            }
            tab("Sprites") {
                disableDelete()
                val list = FXCollections.observableArrayList<Color>()

                val colorGrid = GridView(list)

                colorGrid.cellFactory = Callback { ColorGridCell() }
                val r = Random(System.currentTimeMillis())
                for (i in 0..800) {
                    list.add(Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), 1.0))
                }
                add(colorGrid)
            }
        }
        tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Hierarchy") {
                val root = CheckBoxTreeItem("Root")
                root.isExpanded = true
                root.children.addAll(
                        treeItem_Jonathan,
                        treeItem_Eugene,
                        treeItem_Henry,
                        treeItem_Samir)

                // lets check Eugene to make sure that it shows up in the tree
                treeItem_Eugene.isSelected = true

                val treeview = CheckTreeView(root)
                treeview.selectionModel.selectionMode = SelectionMode.MULTIPLE
//                treeview.selectionModel.selectedItems.addListener(ListChangeListener<TreeItem<String>> { c -> updateText(selectedItemsLabel, c.list) })

                treeview.checkModel.checkedItems.addListener(ListChangeListener<TreeItem<String>> { change ->
                    //                    updateText(checkedItemsLabel, change.list)

                    while (change.next()) {
                        println("============================================")
                        println("Change: " + change)
                        println("Added sublist " + change.addedSubList)
                        println("Removed sublist " + change.removed)
                        println("List " + change.list)
                        println("Added " + change.wasAdded() + " Permutated " + change.wasPermutated() + " Removed " + change.wasRemoved() + " Replaced "
                                + change.wasReplaced() + " Updated " + change.wasUpdated())
                        println("============================================")
                    }
                })
                checkTreeView = treeview
                add(treeview)
            }
        }
    }
}