import com.tomassirio.easyinstaller.config.OsBasedProfileActivator
import com.tomassirio.easyinstaller.config.helper.OSArchUtil
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.SpringApplication
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.mock.env.MockEnvironment

@ExtendWith(MockitoExtension::class)
class OsBasedProfileActivatorTest {

    @Mock
    private lateinit var application: SpringApplication

    @Mock
    private lateinit var osArchUtil: OSArchUtil

    private lateinit var activator: OsBasedProfileActivator
    private lateinit var environment: ConfigurableEnvironment

    @BeforeEach
    fun setup() {
        activator = OsBasedProfileActivator(osArchUtil)
        environment = MockEnvironment()
    }

    @Test
    fun `test Windows profile activation`() {
        `when`(osArchUtil.getOS()).thenReturn("Windows")

        activator.postProcessEnvironment(environment, application)

        assertThat(environment.activeProfiles).contains("Windows")
    }

    @Test
    fun `test Mac profile activation`() {
        `when`(osArchUtil.getOS()).thenReturn("MacOSX")

        activator.postProcessEnvironment(environment, application)

        assertThat(environment.activeProfiles).contains("MacOSX")
    }

    @Test
    fun `test Linux profile activation`() {
        `when`(osArchUtil.getOS()).thenReturn("Linux")

        activator.postProcessEnvironment(environment, application)

        assertThat(environment.activeProfiles).contains("Linux")
    }


    @Test
    fun `test default profile activation for unknown OS`() {
        `when`(osArchUtil.getOS()).thenReturn("default")

        activator.postProcessEnvironment(environment, application)

        assertThat(environment.activeProfiles).contains("default")
    }
}
