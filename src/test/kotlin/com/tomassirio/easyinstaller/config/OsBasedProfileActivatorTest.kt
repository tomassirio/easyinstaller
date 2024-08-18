import com.tomassirio.easyinstaller.config.OsBasedProfileActivator
import com.tomassirio.easyinstaller.config.helper.FileChecker
import com.tomassirio.easyinstaller.config.helper.SystemPropertyWrapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.SpringApplication
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.mock.env.MockEnvironment

@ExtendWith(MockitoExtension::class)
class OsBasedProfileActivatorTest {

    @Mock
    private lateinit var systemPropertyWrapper: SystemPropertyWrapper

    @Mock
    private lateinit var fileChecker: FileChecker

    @Mock
    private lateinit var application: SpringApplication

    private lateinit var activator: OsBasedProfileActivator
    private lateinit var environment: ConfigurableEnvironment

    @BeforeEach
    fun setup() {
        activator = OsBasedProfileActivator(systemPropertyWrapper, fileChecker)
        environment = MockEnvironment()
    }

    @Test
    fun `test Windows profile activation`() {
        `when`(systemPropertyWrapper.getProperty("os.name")).thenReturn("Windows 10")

        activator.postProcessEnvironment(environment, application)

        assert(environment.activeProfiles.contains("windows"))
    }

    @Test
    fun `test Mac profile activation`() {
        `when`(systemPropertyWrapper.getProperty("os.name")).thenReturn("Mac OS X")

        activator.postProcessEnvironment(environment, application)

        assert(environment.activeProfiles.contains("mac"))
    }

    @Test
    fun `test Linux profile activation`() {
        `when`(systemPropertyWrapper.getProperty("os.name")).thenReturn("Linux")

        // Simulate that no specific Linux distribution files are found
        `when`(fileChecker.fileExists(anyString())).thenReturn(false)

        activator.postProcessEnvironment(environment, application)

        assert(environment.activeProfiles.contains("linux"))
    }

    @Test
    fun `test Debian-based Linux profile activation`() {
        `when`(systemPropertyWrapper.getProperty("os.name")).thenReturn("Linux")
        `when`(fileChecker.fileExists("/etc/debian_version")).thenReturn(true)

        activator.postProcessEnvironment(environment, application)

        assert(environment.activeProfiles.contains("debian"))
    }

    @Test
    fun `test Fedora-based Linux profile activation`() {
        `when`(systemPropertyWrapper.getProperty("os.name")).thenReturn("Linux")

        // Use lenient stubbing for this particular check
        lenient().`when`(fileChecker.fileExists("/etc/debian_version")).thenReturn(false)
        lenient().`when`(fileChecker.fileExists("/etc/arch-release")).thenReturn(false)
        `when`(fileChecker.fileExists("/etc/fedora-release")).thenReturn(true)

        activator.postProcessEnvironment(environment, application)

        assert(environment.activeProfiles.contains("fedora"))
    }

    @Test
    fun `test Arch-based Linux profile activation`() {
        `when`(systemPropertyWrapper.getProperty("os.name")).thenReturn("Linux")

        lenient().`when`(fileChecker.fileExists("/etc/debian_version")).thenReturn(false)
        lenient().`when`(fileChecker.fileExists("/etc/fedora-release")).thenReturn(false)
        `when`(fileChecker.fileExists("/etc/arch-release")).thenReturn(true)

        activator.postProcessEnvironment(environment, application)

        assert(environment.activeProfiles.contains("arch"))
    }

    @Test
    fun `test default profile activation for unknown OS`() {
        `when`(systemPropertyWrapper.getProperty("os.name")).thenReturn("SomeUnknownOS")

        activator.postProcessEnvironment(environment, application)

        assert(environment.activeProfiles.contains("default"))
    }
}
