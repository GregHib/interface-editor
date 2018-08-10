package com.greg.model.widgets.memento

import java.util.regex.Pattern

object MementoString {

    private val string = "([A-Z]+\\s\\[[^\\[\\]]+])"
    private val regex = Regex(string)
    private val pattern = Pattern.compile(string)

    fun hasFormat(string: String): Boolean {
        return string.matches(regex)
    }

    /**
     * Checks if string contains a full array in the middle or at the end of the container values
     * @return whether a verified container contains an array
     */
    fun hasArray(string: String): Boolean {
        return (string.contains(", [") || string.contains("[[")) && (string.contains("]], ") || string.endsWith("]]]"))//TODO support array first in index
    }

    /**
     * Replaces last occurrence of a substring
     * Credit: https://stackoverflow.com/a/16665524/2871826
     */
    fun replaceLast(string: String, substring: String, replacement: String): String {
        val index = string.lastIndexOf(substring)
        return if (index == -1) string else string.substring(0, index) + replacement + string.substring(index + substring.length)
    }

    /**
     * Splits root container and returns string contents of it's array
     */
    fun extractArray(string: String): String {
        if(!hasArray(string))
            return ""

        //Replace square brackets of array with regular so it's easier to differentiate
        var input = string
        input = if(input.contains("[[")) input.replaceFirst("[[", "[(") else input.replaceFirst(", [", ", (")
        input = if(input.endsWith("]]]")) replaceLast(input, "]]]", "])]") else replaceLast(input, "]], ", "]), ")

        val arraySplit = input.split("(", ")")
        return arraySplit[1]//Always the second index
    }

    fun replaceArray(input: String, array: String): String {
        return input.replaceFirst(array, "\$array")
    }

    /**
     * Iterates pattern matching for the remainder containers
     * @return list of individual container strings
     */
    fun extractContainers(string: String): ArrayList<String> {
        var array = string
        val containers = arrayListOf<String>()
        var index = 0

        //Nature of pattern matches deepest level first then works it's way upwards
        while(array.contains("[")) {//while has more containers
            val matcher = pattern.matcher(array)

            while (matcher.find()) {
                val match = matcher.group()
                containers.add(match)
                array = array.replace(match, "\$${index++}")
            }
        }
        return containers
    }

    /**
     * Replaces container strings with placeholder indices
     */
    fun replaceContainers(string: String, containers: ArrayList<String>): String {
        var array = string
        containers.forEachIndexed { index, s -> array = array.replaceFirst(s, "\$$index") }
        return array
    }

    /**
     * Splits container variables
     * @return array of values
     */
    fun getVariables(string: String): List<String> {
        return string.substring(string.indexOf(" [") + 2, string.length - 1).split(", ")
    }

    fun getType(string: String): String {
        return string.substring(0, string.indexOf(" ["))
    }
}