package com.tomassirio.easyinstaller.service

import com.tomassirio.easyinstaller.style.ShellFormatter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ApplicationInstallerServiceTest {

    @Mock
    private lateinit var shellFormatter: ShellFormatter

    @Mock(lenient = true)
    private lateinit var installableApp1: InstallableApplication

    @Mock(lenient = true)
    private lateinit var installableApp2: InstallableApplication

    private lateinit var appList: List<InstallableApplication>

    private lateinit var applicationInstallerService: ApplicationInstallerService

    @BeforeEach
    fun setUp() {
        `when`(installableApp1.name()).thenReturn("App1")
        `when`(installableApp2.name()).thenReturn("App2")
        appList = listOf(installableApp1, installableApp2)
        applicationInstallerService = ApplicationInstallerService(appList)
    }

    @Test
    fun `installAllInOrder should install all applications in order`() {
        // When
        applicationInstallerService.installAllInOrder()

        // Then
        verify(installableApp1).install()
        verify(installableApp2).install()
    }

    @Test
    fun `listApplications should return list of application names`() {
        // When
        val result = applicationInstallerService.listApplications()

        // Then
        assert(result == listOf("App1", "App2"))
    }

    @Test
    fun `installApplication should install the application by name`() {
        // When
        applicationInstallerService.installApplication("App1")

        // Then
        verify(installableApp1).install()
    }

    @Test
    fun `installApplication should throw error if application not found`() {
        // When / Then
        val exception = assertThrows<IllegalArgumentException> {
            applicationInstallerService.installApplication("NonExistentApp")
        }
        assert(exception.message == "Application NonExistentApp not found")
        verify(installableApp1, never()).install()
        verify(installableApp2, never()).install()
    }
}