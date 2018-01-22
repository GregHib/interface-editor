package com.greg.controller.actions

import com.greg.controller.ChangeType

data class Change(val type: ChangeType, val id: Int, val value: Any)