package com.greg.controller.controller.canvas

import com.greg.controller.controller.input.KeyboardController
import com.greg.controller.controller.input.MouseController
import com.greg.controller.view.CanvasView

abstract class CanvasState(var canvas: CanvasView) : MouseController, KeyboardController