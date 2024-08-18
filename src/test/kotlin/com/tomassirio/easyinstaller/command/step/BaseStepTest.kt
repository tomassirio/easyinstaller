package com.tomassirio.easyinstaller.command.step

import com.tomassirio.easyinstaller.service.InstallableApplication
import com.tomassirio.easyinstaller.service.annotation.PackageManager
import com.tomassirio.easyinstaller.service.annotation.ShellAndTerminalManager
import com.tomassirio.easyinstaller.service.annotation.VersionControlSystem
import com.tomassirio.easyinstaller.service.impl.BrewInstaller
import com.tomassirio.easyinstaller.service.impl.GitInstaller
import com.tomassirio.easyinstaller.service.impl.ZshInstaller
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import java.util.stream.Stream


private const val MAC_PROFILE = "mac"
private const val WINDOWS_PROFILE = "windows"
private const val LINUX_PROFILE = "linux"

@ExtendWith(MockitoExtension::class)
class BaseStepTest {
    @Mock
    private lateinit var applicationContext: ApplicationContext

    @Mock
    private lateinit var environment: Environment

    private lateinit var baseStep: BaseStep

    @BeforeEach
    fun setup() {
        baseStep = object : BaseStep(applicationContext, environment) {}
    }

    @ParameterizedTest
    @MethodSource("provideProfilesAndAnnotations")
    fun `getInstallers should return filtered installers based on annotation and profile`(
        activeProfile: String,
        annotationClass: Class<out Annotation>,
        expectedInstallers: List<String>
    ) {
        `when`(environment.activeProfiles).thenReturn(arrayOf(activeProfile))

        val mockInstallers = mockInstallers(annotationClass)

        `when`(applicationContext.getBeansWithAnnotation(annotationClass))
            .thenReturn(mockInstallers)

        val result = baseStep.getInstallers(annotationClass)

        assertEquals(expectedInstallers.size, result.size)
        assertTrue(result.map { it.name() }.containsAll(expectedInstallers))
    }

    companion object {
        @JvmStatic
        fun provideProfilesAndAnnotations(): Stream<Arguments> {
            return Stream.of(
                // MAC_PROFILE
                Arguments.of(MAC_PROFILE, PackageManager::class.java, listOf("Brew")),
                Arguments.of(MAC_PROFILE, ShellAndTerminalManager::class.java, listOf("Zsh")),
                Arguments.of(MAC_PROFILE, VersionControlSystem::class.java, listOf("Git")),

                // WINDOWS_PROFILE
                Arguments.of(WINDOWS_PROFILE, ShellAndTerminalManager::class.java, listOf("Zsh")),

                // LINUX_PROFILE
                Arguments.of(LINUX_PROFILE, ShellAndTerminalManager::class.java, listOf("Zsh")),
                Arguments.of(LINUX_PROFILE, VersionControlSystem::class.java, listOf("Git")),
            )
        }
    }

    private fun mockInstallers(annotationClass: Class<out Annotation>): Map<String, InstallableApplication> {
        val mockInstallers = mapOf(
            "brewInstaller" to mock(BrewInstaller::class.java),
            "gitInstaller" to mock(GitInstaller::class.java),
            "zshInstaller" to mock(ZshInstaller::class.java)
        ).filter { it.value::class.java.isAnnotationPresent(annotationClass) }
            .onEach { (_, mock) ->
                `when`(mock.name()).thenCallRealMethod()
            }
        return mockInstallers
    }
}