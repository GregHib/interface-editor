package com.greg.model.widgets.memento

import com.greg.model.widgets.memento.MementoString.getVariables
import org.junit.Assert
import org.junit.Test

class MementoStringTest {
    @Test
    fun all() {
        val input = "CONTAINER [1, \"one\", false, [CONTAINER [2, \"two\", CONTAINER [3, \"three\"], CONTAINER [false], true], CONTAINER [2, \"string\", false], CONTAINER [4]], 42, true]"//"""CONTAINER [1, "one", false, [CONTAINER [2, "two", false]], 42, true]"""

        if(MementoString.hasArray(input)) {
            val array = MementoString.extractArray(input)

            val first = MementoString.replaceArray(input, array)

            val containers = MementoString.extractContainers(array)

            val final = MementoString.replaceContainers(array, containers)

            println("$first ${getVariables(first)}")

            println("\$array = $final")

            containers.forEachIndexed { index, s -> println("\$$index = $s ${getVariables(s)}") }
        }
    }

    private val emptyLast = "CONTAINER [1, \"one\", false, []]"
    private val oneLast = "CONTAINER [1, \"one\", false, [CONTAINER [2, \"two\"]]]"
    private val twoLast = "CONTAINER [1, \"one\", false, [CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"]]]"
    private val threeLast = "CONTAINER [1, \"one\", false, [CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"], CONTAINER [5, \"five\"]]]"

    private val empty = "CONTAINER [1, \"one\", false, [], 42]"
    private val one = "CONTAINER [1, \"one\", false, [CONTAINER [2, \"two\"]], 42]"
    private val two = "CONTAINER [1, \"one\", false, [CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"]], 42]"
    private val three = "CONTAINER [1, \"one\", false, [CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"], CONTAINER [5, \"five\"]], 42]"

    private val emptyFirst = "CONTAINER [[], 1, \"one\", false]"
    private val oneFirst = "CONTAINER [[CONTAINER [2, \"two\"]], 1, \"one\", false]"
    private val twoFirst = "CONTAINER [[CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"]], 1, \"one\", false]"
    private val threeFirst = "CONTAINER [[CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"], CONTAINER [5, \"five\"]], 1, \"one\", false]"

    @Test
    fun hasArray() {
        Assert.assertFalse(MementoString.hasArray(emptyLast))
        Assert.assertTrue(MementoString.hasArray(oneLast))
        Assert.assertTrue(MementoString.hasArray(twoLast))
        Assert.assertTrue(MementoString.hasArray(threeLast))

        Assert.assertFalse(MementoString.hasArray(empty))
        Assert.assertTrue(MementoString.hasArray(one))
        Assert.assertTrue(MementoString.hasArray(two))
        Assert.assertTrue(MementoString.hasArray(three))

        Assert.assertFalse(MementoString.hasArray(emptyFirst))
        Assert.assertTrue(MementoString.hasArray(oneFirst))
        Assert.assertTrue(MementoString.hasArray(twoFirst))
        Assert.assertTrue(MementoString.hasArray(threeFirst))
    }

    @Test
    fun extractArray() {
        Assert.assertTrue(MementoString.extractArray(empty).isEmpty())
        Assert.assertTrue(MementoString.extractArray(emptyLast).isEmpty())
        Assert.assertTrue(MementoString.extractArray(emptyFirst).isEmpty())

        Assert.assertEquals(MementoString.extractArray(oneLast), "CONTAINER [2, \"two\"]")
        Assert.assertEquals(MementoString.extractArray(twoLast), "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"]")
        Assert.assertEquals(MementoString.extractArray(threeLast), "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"], CONTAINER [5, \"five\"]")
        Assert.assertEquals(MementoString.extractArray(one), "CONTAINER [2, \"two\"]")
        Assert.assertEquals(MementoString.extractArray(two), "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"]")
        Assert.assertEquals(MementoString.extractArray(three), "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"], CONTAINER [5, \"five\"]")
        Assert.assertEquals(MementoString.extractArray(oneFirst), "CONTAINER [2, \"two\"]")
        Assert.assertEquals(MementoString.extractArray(twoFirst), "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"]")
        Assert.assertEquals(MementoString.extractArray(threeFirst), "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"], CONTAINER [5, \"five\"]")
    }

    @Test
    fun replaceArray() {
        val last = "CONTAINER [1, \"one\", false, [\$array]]"
        val middle = "CONTAINER [1, \"one\", false, [\$array], 42]"
        val first = "CONTAINER [[\$array], 1, \"one\", false]"
        Assert.assertEquals(MementoString.replaceArray(oneLast, "CONTAINER [2, \"two\"]"), last)
        Assert.assertEquals(MementoString.replaceArray(twoLast, "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"]"), last)
        Assert.assertEquals(MementoString.replaceArray(threeLast, "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"], CONTAINER [5, \"five\"]"), last)

        Assert.assertEquals(MementoString.replaceArray(one, "CONTAINER [2, \"two\"]"), middle)
        Assert.assertEquals(MementoString.replaceArray(two, "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"]"), middle)
        Assert.assertEquals(MementoString.replaceArray(three, "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"], CONTAINER [5, \"five\"]"), middle)

        Assert.assertEquals(MementoString.replaceArray(oneFirst, "CONTAINER [2, \"two\"]"), first)
        Assert.assertEquals(MementoString.replaceArray(twoFirst, "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"]"), first)
        Assert.assertEquals(MementoString.replaceArray(threeFirst, "CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"], CONTAINER [5, \"five\"]"), first)
    }

    @Test
    fun extractContainers() {
        Assert.assertEquals(MementoString.extractContainers("CONTAINER [2, \"two\"]").size, 1)
        Assert.assertEquals(MementoString.extractContainers("CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"]").size, 3)
        Assert.assertEquals(MementoString.extractContainers("CONTAINER [2, \"two\", CONTAINER [3, \"three\"]], CONTAINER [4, \"four\"], CONTAINER [5, \"five\"]").size, 4)
    }
}