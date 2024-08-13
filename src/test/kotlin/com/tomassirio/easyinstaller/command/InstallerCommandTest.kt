package com.tomassirio.easyinstaller.command

import com.tomassirio.easyinstaller.command.step.PacketManagerStep
import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class InstallerCommandTest {

    private lateinit var installerService: ApplicationInstallerService
    private lateinit var packetManagerStep: PacketManagerStep
    private lateinit var installerCommand: InstallerCommand

    @BeforeEach
    fun setUp() {
        installerService = mock(ApplicationInstallerService::class.java)
        packetManagerStep = mock(PacketManagerStep::class.java)
        installerCommand = InstallerCommand(installerService, packetManagerStep)
    }

    @Test
    fun `quickInstall should call installAllInOrder`() {
        // Act
        installerCommand.quickInstall()

        // Assert
        verify(installerService).installAllInOrder()
    }

    @Test
    fun `installManually should execute PacketManagerStep`() {
        // Act
        installerCommand.installManually()

        // Assert
        verify(packetManagerStep).execute()
    }
}