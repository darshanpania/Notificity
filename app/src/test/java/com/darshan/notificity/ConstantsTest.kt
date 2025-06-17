package com.darshan.notificity

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ConstantsTest {

    @Mock private lateinit var mockContext: Context

    @Before
    fun setUp() {
        // MockitoAnnotations.openMocks(this) // For older Mockito or if @RunWith is not used
        // No need to explicitly call openMocks with MockitoJUnitRunner
    }

    @Test
    fun `getLabel returns correct string for 7 days`() {
        `when`(mockContext.getString(R.string.setting_retention_7_days)).thenReturn("7 days")
        val label =
            Constants.RetentionPeriod.getLabel(mockContext, Constants.RetentionPeriod.DAYS_7)
        assertEquals("7 days", label)
    }

    @Test
    fun `getLabel returns correct string for 30 days`() {
        `when`(mockContext.getString(R.string.setting_retention_30_days)).thenReturn("30 days")
        val label =
            Constants.RetentionPeriod.getLabel(mockContext, Constants.RetentionPeriod.DAYS_30)
        assertEquals("30 days", label)
    }

    @Test
    fun `getLabel returns correct string for unlimited`() {
        `when`(mockContext.getString(R.string.setting_retention_unlimited)).thenReturn("Unlimited")
        val label =
            Constants.RetentionPeriod.getLabel(mockContext, Constants.RetentionPeriod.UNLIMITED)
        assertEquals("Unlimited", label)
    }

    @Test
    fun `getLabel returns unlimited string for unknown period`() {
        `when`(mockContext.getString(R.string.setting_retention_unlimited))
            .thenReturn("Unlimited") // Default fallback
        val label = Constants.RetentionPeriod.getLabel(mockContext, 999) // Unknown period
        assertEquals("Unlimited", label)
    }
}
