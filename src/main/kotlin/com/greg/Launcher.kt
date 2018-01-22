package com.greg

import javafx.application.Application
import com.greg.view.MainView
import tornadofx.App

class Launcher: App(MainView::class)

fun main(args: Array<String>) {
    Application.launch(Launcher::class.java, *args)
}