package com.greg.view.sprites.tree

import io.nshusa.rsam.binary.sprite.Sprite
import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.awt.image.BufferedImage

class ImageTreeItem(string: String, val sprite: BufferedImage?, var imageView: ImageView? = null) : TreeItem<String>(string)