package com.prof18.feedflow

import com.prof18.feedflow.domain.getDateMillisFromString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlatformKtTest {

    @Test
    fun `getDateMillisFromString returns correct values`() {
        val dateString = "Wed, 15 May 2019 20:48:02 +0000"

        val millis = getDateMillisFromString(dateString)
        println(millis)
        assertEquals(1557953282000, millis)
    }

    @Test
    fun `getDateMillisFromString returns correct values with timezone`() {
        val dateString = "Fri, 7 May 2021 10:44:02 EST"

        val millis = getDateMillisFromString(dateString)
        assertEquals(1620402242000, millis)
    }

    @Test
    fun `getDateMillisFromString returns null with a wrong value`() {
        val dateString = "Fri, 7 May 2021 10:44:0"

        val millis = getDateMillisFromString(dateString)
        assertNull(millis)
    }
}
