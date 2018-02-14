package com.greg.view.sprites

import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView

class ImageTreeItem(string: String, val image: Image) : TreeItem<String>(string) {
    var imageView: ImageView? = null
}