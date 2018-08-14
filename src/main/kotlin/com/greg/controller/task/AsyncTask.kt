package com.greg.controller.task

import javafx.concurrent.Task

class AsyncTask : Task<Boolean>() {
    override fun call(): Boolean {
        return true
    }

}