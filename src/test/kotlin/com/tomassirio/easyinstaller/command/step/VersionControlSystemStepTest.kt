package com.tomassirio.easyinstaller.command.step

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyList
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.shell.component.context.ComponentContext
import org.springframework.shell.component.flow.ComponentFlow
import org.springframework.shell.component.flow.MultiItemSelectorSpec

@ExtendWith(MockitoExtension::class)
class VersionControlSystemStepTest {

    @Mock
    private lateinit var componentFlowBuilder: ComponentFlow.Builder
    @Mock
    private lateinit var applicationContext: ApplicationContext
    @Mock
    private lateinit var environment: Environment
    @Mock
    private lateinit var componentFlow: ComponentFlow
    @Mock
    private lateinit var multiItemSelectorSpec: MultiItemSelectorSpec
    @InjectMocks
    private lateinit var versionControlSystemStep: VersionControlSystemStep

    @BeforeEach
    fun setup() {
        `when`(componentFlowBuilder.clone()).thenReturn(componentFlowBuilder)
        `when`(componentFlowBuilder.reset()).thenReturn(componentFlowBuilder)
        `when`(componentFlowBuilder.withMultiItemSelector(any())).thenReturn(multiItemSelectorSpec)
        `when`(multiItemSelectorSpec.name(any())).thenReturn(multiItemSelectorSpec)
        `when`(multiItemSelectorSpec.selectItems(anyList())).thenReturn(multiItemSelectorSpec)
        `when`(multiItemSelectorSpec.and()).thenReturn(componentFlowBuilder)
        `when`(componentFlowBuilder.build()).thenReturn(componentFlow)

        // Mock environment to return non-null activeProfiles
        `when`(environment.activeProfiles).thenReturn(arrayOf("test"))
    }

    @Test
    fun `execute should return selected version control systems`() {
        // Mock flow result
        val flowResult = mock(ComponentFlow.ComponentFlowResult::class.java)
        val flowContext = mock(ComponentContext::class.java)
        `when`(componentFlow.run()).thenReturn(flowResult)
        `when`(flowResult.context).thenReturn(flowContext)
        `when`(flowContext.get<List<String>>("selectedApps")).thenReturn(listOf("Git", "SVN"))

        // Execute
        val result = versionControlSystemStep.execute()

        // Verify
        assertEquals(listOf("Git", "SVN"), result)
    }

    @Test
    fun `execute should return empty list when no version control system is selected`() {
        // Mock flow result
        val flowResult = mock(ComponentFlow.ComponentFlowResult::class.java)
        val flowContext = mock(ComponentContext::class.java)
        `when`(componentFlow.run()).thenReturn(flowResult)
        `when`(flowResult.context).thenReturn(flowContext)
        `when`(flowContext.get<List<String>>("selectedApps")).thenReturn(null)

        // Execute
        val result = versionControlSystemStep.execute()

        // Verify
        assertTrue(result.isEmpty())
    }
}
