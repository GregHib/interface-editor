package src.com.greg.controller.actions

import src.com.greg.controller.ChangeType

data class Change(val type: ChangeType, val id: Int, val value: Any)