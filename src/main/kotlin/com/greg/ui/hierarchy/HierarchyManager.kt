package com.greg.ui.hierarchy

import com.greg.controller.ControllerView
import javafx.scene.control.TreeItem
import tornadofx.*

class HierarchyManager(controller: ControllerView) {
    private val tree = controller.hierarchyTree

    val persons = mutableListOf(
            Person("Mary Hanes","Marketing"),
            Person("Steve Folley","Customer Service"),
            Person("John Ramsy","IT Help Desk"),
            Person("Erlick Foyes","Customer Service"),
            Person("Erin James","Marketing"),
            Person("Jacob Mays","IT Help Desk"),
            Person("Larry Cable","Customer Service")
    ).observable()

    val departments = persons
            .map { it.department }
            .distinct().map { Person(it, "") }.observable()
    init {
        tree.root = TreeItem(Person("Departments", ""))

        tree.cellFormat { text = it.name }
        tree.populate { parent ->
            if(parent == tree.root) departments else persons.filter { it.department == parent.value.name }
        }
        tree.contextmenu {
            item("Cut").action {
                println("Cut!")
            }
            item("Copy").action {
                println("Copied!")
            }
            item("Delete").action {
                println("Delete!")
            }
        }
    }
}
data class Person(val name: String, val department: String)