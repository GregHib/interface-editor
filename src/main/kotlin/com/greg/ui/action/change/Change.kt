package com.greg.ui.action.change

import com.greg.ui.canvas.widget.memento.mementoes.Memento

data class Change(val type: ChangeType, val id: Int, val memento: Memento)