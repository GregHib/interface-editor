package com.greg.view.sprites.tree

import io.nshusa.rsam.binary.sprite.Sprite
import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView

class ImageTreeItem(string: String, val sprite: Sprite?, var imageView: ImageView? = null) : TreeItem<String>(string)