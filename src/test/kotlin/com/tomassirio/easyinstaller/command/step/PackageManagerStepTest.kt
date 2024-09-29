package com.tomassirio.easyinstaller.command.step

import com.tomassirio.easyinstaller.service.ApplicationInstallerService
import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
import com.tomassirio.easyinstaller.style.ShellFormatter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.anyList
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.shell.component.context.ComponentContext
import org.springframework.shell.component.flow.ComponentFlow
import org.springframework.shell.component.flow.ConfirmationInputSpec
import org.springframework.shell.component.flow.SingleItemSelectorSpec

@ExtendWith(MockitoExtension::class)
class PackageManagerStepTest {

    @Mock
    private lateinit var componentFlowBuilder: ComponentFlow.Builder
    @Mock
    private lateinit var applicationContext: ApplicationContext
    @Mock
    private lateinit var applicationInstallerService: ApplicationInstallerService
    @Mock
    private lateinit var downloadStrategyContext: DownloadStrategyContext
    @Mock
    private lateinit var shellFormatter: ShellFormatter
    @Mock
    private lateinit var environment: Environment
    @Mock
    private lateinit var componentFlow: ComponentFlow
    @Mock
    private lateinit var singleItemSelectorSpec: SingleItemSelectorSpec
    @Mock
    private lateinit var confirmationInputSpec: ConfirmationInputSpec
    @InjectMocks
    private lateinit var packageManagerStep: PackageManagerStep

    @BeforeEach
    fun setup() {
        `when`(componentFlowBuilder.clone()).thenReturn(componentFlowBuilder)
        `when`(componentFlowBuilder.reset()).thenReturn(componentFlowBuilder)
        `when`(componentFlowBuilder.withSingleItemSelector(any())).thenReturn(singleItemSelectorSpec)
        `when`(singleItemSelectorSpec.name(any())).thenReturn(singleItemSelectorSpec)
        `when`(singleItemSelectorSpec.selectItems(anyList())).thenReturn(singleItemSelectorSpec)
        `when`(singleItemSelectorSpec.and()).thenReturn(componentFlowBuilder)
        `when`(componentFlowBuilder.build()).thenReturn(componentFlow)
        `when`(componentFlowBuilder.withConfirmationInput(any())).thenReturn(confirmationInputSpec)
        `when`(confirmationInputSpec.name(any())).thenReturn(confirmationInputSpec)
        `when`(confirmationInputSpec.defaultValue(anyBoolean())).thenReturn(confirmationInputSpec)
        `when`(confirmationInputSpec.and()).thenReturn(componentFlowBuilder)

        // Mock environment to return non-null activeProfiles
        `when`(environment.activeProfiles).thenReturn(arrayOf("test"))
    }

    @Test
    fun `execute should install selected application`() {
        // Mock flow result
        val flowResult = mock(ComponentFlow.ComponentFlowResult::class.java)
        val flowContext = mock(ComponentContext::class.java)
        `when`(componentFlow.run()).thenReturn(flowResult)
        `when`(flowResult.context).thenReturn(flowContext)
        `when`(flowContext.get<String>("selectedApp")).thenReturn("MockInstaller")

        // Execute
        packageManagerStep.execute()

        // Verify
        verify(applicationInstallerService).installApplication("MockInstaller")
        verify(shellFormatter).printWarning("Application MockInstaller is going to be installed")
    }

    @Test
    fun `execute should handle application not found`() {
        // Mock flow result
        val flowResult = mock(ComponentFlow.ComponentFlowResult::class.java)
        val flowContext = mock(ComponentContext::class.java)
        `when`(componentFlow.run()).thenReturn(flowResult)
        `when`(flowResult.context).thenReturn(flowContext)
        `when`(flowContext.get<String>("selectedApp")).thenReturn("NonExistentApp")

        // Mock applicationInstallerService to throw exception
        `when`(applicationInstallerService.installApplication("NonExistentApp"))
            .thenThrow(IllegalArgumentException())

        // Execute
        packageManagerStep.execute()

        // Verify
        verify(shellFormatter).printError("Application NonExistentApp not found")
    }
}