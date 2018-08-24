package com.greg.model.widgets

import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class JsonSerializerTest {

    class TestClass(val name: String) {

        val first = 1
        private val second = 2
        @Transient
        internal var third = 3
        var fourth = 5

        init {
            fourth = 4
        }

        override fun equals(other: Any?): Boolean {
            return first == first && second == second && fourth == fourth
        }
    }


    companion object {
        lateinit var test: TestClass

        @BeforeClass
        @JvmStatic
        fun classSetup() {
            test = TestClass("A Name!")
        }
    }

    @Test
    fun serialize() {
        val serialized = JsonSerializer.serialize(test)
        Assert.assertEquals(serialized, "{\"first\":1,\"second\":2,\"fourth\":4,\"name\":\"A Name!\"}")
    }

    @Test
    fun deserializer() {
        val serialized = "{\"first\":1,\"second\":2,\"fourth\":4,\"name\":\"A Name!\"}"
        val deserialized = JsonSerializer.deserializer(serialized, TestClass::class.java)
        Assert.assertTrue(deserialized is TestClass)
        Assert.assertTrue(deserialized == test)
    }
}