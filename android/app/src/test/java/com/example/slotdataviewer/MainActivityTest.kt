package com.example.slotdataviewer

import org.junit.Assert.assertEquals
import org.junit.Test

class MainActivityTest {
    @Test
    fun startUrl_pointsAtGithubPages() {
        assertEquals("https://makoto-design.github.io/slot-data-viewer/", MainActivity.START_URL)
    }
}
