package com.greg.controller.widgets

import com.greg.model.widgets.type.Widget

object WidgetScripts {
    fun scriptStateChanged(widget: Widget): Boolean {
        if (widget.getScriptOperators().isEmpty()) {
            return false
        }

        for (id in 0 until widget.getScriptOperators().size) {
            val result = executeScript(widget, id)
            val defaultValue = widget.getScriptDefaults()[id]
            val operator = widget.getScriptOperators()[id]

            if (operator == 1) {
                if (result != defaultValue) {
                    return false
                }
            } else if (operator == 2) {
                if (result >= defaultValue) {
                    return false
                }
            } else if (operator == 3) {
                if (result <= defaultValue) {
                    return false
                }
            } else if (operator == 4) {
                if (result == defaultValue) {
                    return false
                }
            }
        }

        return true
    }

    private fun executeScript(widget: Widget, id: Int): Int {
        if (widget.getScripts().isEmpty() || id >= widget.getScripts().size) {
            return -2
        }
        try {
            val script = widget.getScripts()[id]
            var accumulator = 0
            var counter = 0
            var operator = 0

            do {
                val instruction = script!![counter++]
                var value = 0
                var next: Byte = 0

                when (instruction) {
                    0 -> return accumulator
                    1 -> {
                        //                    value = currentLevels[script[counter++]]
                    }
                    2 -> {
                        //                    value = maximumLevels[script[counter++]]
                    }
                    3 -> {
                        //                    value = experiences[script[counter++]]
                    }
                    4 -> {
                        /*val other = Widget.widgets[script[counter++]]
                            val item = script[counter++]

                            if (item >= 0 && item < ItemDefinition.getCount()
                                    && (!ItemDefinition.lookup(item).isMembers() || membersServer)) {
                                for (slot in 0 until other.inventoryIds.length) {
                                    if (other.inventoryIds[slot] === item + 1) {
                                        value += other.inventoryAmounts[slot]
                                    }
                                }
                            }*/
                    }
                    5 -> {
                        //                    value = settings[script[counter++]]
                    }
                    6 -> {
                        //                    value = SKILL_EXPERIENCE[maximumLevels[script[counter++]] - 1]
                    }
                    7 -> {
                        //                    value = settings[script[counter++]] * 100 / 46875
                    }
                    8 -> {
                        //                    value = localPlayer.combat
                    }
                    9 -> {
                        /*for (skill in 0 until SkillConstants.SKILL_COUNT) {
                                if (SkillConstants.ENABLED_SKILLS[skill]) {
                                    value += maximumLevels[skill]
                                }
                            }*/
                    }
                    10 -> {
                        /*val other = Widget.widgets[script[counter++]]
                            val item = script[counter++] + 1

                            if (item >= 0 && item < ItemDefinition.getCount()
                                    && (!ItemDefinition.lookup(item).isMembers() || membersServer)) {
                                for (stored in other.inventoryIds) {
                                    if (stored == item) {
                                        value = 999999999
                                        break
                                    }
                                }
                            }*/
                    }
                    11 -> {
                        //                    value = runEnergy
                    }
                    12 -> {
                        //                    value = weight
                    }
                    13 -> {
                        /*val bool = settings[script[counter++]]
                            val shift = script[counter++]
                            value = if (bool and (1 shl shift) == 0) 0 else 1*/
                    }
                    14 -> {
                        /*val index = script[counter++]
                            val bits = VariableBits.bits[index]
                            val setting = bits.getSetting()
                            val low = bits.getLow()
                            val high = bits.getHigh()
                            val mask = BIT_MASKS[high - low]
                            value = settings[setting] shr low and mask*/
                    }
                    15 -> next = 1
                    16 -> next = 2
                    17 -> next = 3
//                    18 -> value = (localPlayer.worldX shr 7) + regionBaseX
//                    19 -> value = (localPlayer.worldY shr 7) + regionBaseY
                    20 -> value = script[counter++]
                }

                if (next.toInt() == 0) {
                    if (operator == 0) {
                        accumulator += value
                    } else if (operator == 1) {
                        accumulator -= value
                    } else if (operator == 2 && value != 0) {
                        accumulator /= value
                    } else if (operator == 3) {
                        accumulator *= value
                    }

                    operator = 0
                } else {
                    operator = next.toInt()
                }
            } while (true)
        } catch (ex: Exception) {
            return -1
        }
    }
}