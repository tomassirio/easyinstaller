package com.tomassirio.easyinstaller.command

import com.tomassirio.easyinstaller.command.step.PackageManagerStep
import com.tomassirio.easyinstaller.command.step.ShellAndTerminalManagerStep
import com.tomassirio.easyinstaller.command.step.VersionControlSystemStep
import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class InstallerCommandTest {

    @Mock
    private lateinit var installerService: ApplicationInstallerService

    @Mock
    private lateinit var packageManagerStep: PackageManagerStep

    @Mock
    private lateinit var versionControlSystemStep: VersionControlSystemStep

    @Mock
    private lateinit var shellAndTerminalManagerStep: ShellAndTerminalManagerStep

    @Mock
    private lateinit var shellFormatter: ShellFormatter

    @InjectMocks
    private lateinit var installerCommand: InstallerCommand


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
        verify(packageManagerStep).execute()
    }
}