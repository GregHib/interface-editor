package com.greg.controller.controller.canvas

import com.greg.controller.controller.hierarchy.HierarchyController
import com.greg.controller.controller.panels.PanelController
import javafx.application.Platform
import java.util.*
import kotlin.concurrent.timerTask

class RefreshManager(val panels: PanelController, val hierarchy: HierarchyController) {

    private val timer = Timer()
    private var latest: Long = 0

    private val refresh = Runnable {
        Platform.runLater {
            panels.reload()
            hierarchy.reload()
        }
    }

    fun request() {
        //Record the time the refresh was requested
        val start = System.nanoTime()
        //Set as the latest time requested
        latest = start

        //Schedule the run
        timer.schedule(timerTask {
            //Cancel if another refresh was requested less than 10 milliseconds after this one
            if(start < latest) {
                cancel()
                return@timerTask
            }
            //Run refresh
            refresh.run()
        }, 10)//milliseconds
    }
}