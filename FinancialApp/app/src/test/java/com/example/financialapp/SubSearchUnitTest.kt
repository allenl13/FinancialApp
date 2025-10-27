package com.example.financialapp

import com.example.financialapp.subscriptions.SubEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class SubSearchUnitTest {
    @Test
    fun searchFunction() {
        // hardcoded data
        val subs = listOf(
            SubEntity(name = "Youtube", amount = 1700, dueDate = "23 Oct 2025", recurrence = "month", category = "Entertainment"),
            SubEntity(name = "Netflix", amount = 1799, dueDate = "24 Oct 2025", recurrence = "month", category = "Entertainment"),
            SubEntity(name = "City Fitness", amount = 999, dueDate = "25 Oct 2025", recurrence = "week", category = "Fitness & Health")
        )

        // searching text
        val searchText = "net"

        // checks search text with subscriptions
        val result = subs.filter { sub ->
            sub.name.contains(searchText, ignoreCase = true)
                    || sub.category.contains(searchText, ignoreCase = true)
                    || sub.dueDate.contains(searchText, ignoreCase = true)
        }


        // unit test
        assertEquals("Netflix", result[0].name)


    }
}