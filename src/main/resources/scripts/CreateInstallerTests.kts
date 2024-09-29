#!/usr/bin/env kotlin

import java.io.File
import java.util.*

// List of installers
val installers = listOf(
    "AgeInstaller", "CondaInstaller",
    "AgInstaller", "AlacrittyInstaller", "AptInstaller", "AtomInstaller", "AwsCliInstaller",
    "AzureCliInstaller", "BitwardenCliInstaller", "BorgBackupInstaller", "DiscordInstaller",
    "DjangoInstaller", "DockerComposeInstaller", "DockerInstaller", "EclipseInstaller",
    "FishInstaller", "FzfInstaller", "GCloudInstaller", "GithubCliInstaller",
    "GitKrakenInstaller", "GitInstaller", "GoInstaller", "GradleInstaller", "GpgInstaller",
    "HttpieInstaller", "HugoInstaller", "ItermInstaller", "JenkinsInstaller", "JqInstaller",
    "KubectlInstaller", "MavenInstaller", "MkDocsInstaller", "MongoDbInstaller",
    "MySqlInstaller", "NixInstaller", "NodeInstaller", "PlantumlInstaller",
    "PostgreSqlInstaller", "PyCharmInstaller", "PythonInstaller", "ReactInstaller",
    "RedisInstaller", "RipGrepInstaller", "RubyInstaller", "RustInstaller", "ScreenInstaller",
    "SdkManagerInstaller", "SonarQubeInstaller", "StarshipInstaller", "SublimeInstaller",
    "SynchtingInstaller", "TerraformInstaller", "TmuxInstaller", "VsCodeInstaller",
    "ZoomInstaller"
)

// Base path for the test files
val testDirectory = "src/test/kotlin/com/tomassirio/easyinstaller/service/impl/installer"

val commandDefaults = mapOf(
    // Package Managers
    "brew" to "curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh | /bin/bash",
    "apt" to "curl -fsSL http://us.archive.ubuntu.com/ubuntu/pool/main/a/apt/apt_1.9.3_amd64.deb -o apt_1.9.3_amd64.deb && sudo dpkg -i apt_1.9.3_amd64.deb && sudo apt-get install -f",
    "nix" to "curl -fsSL https://nixos.org/nix/install | sh",
    "conda" to "curl -fsSL https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh -o miniconda.sh && bash miniconda.sh",

    // Shell and Terminal Enhancements
    "zsh" to "curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh | /bin/bash",
    "fish" to "curl -fsSL https://raw.githubusercontent.com/oh-my-fish/oh-my-fish/master/bin/install | fish",
    "iterm" to "curl -fsSL https://iterm2.com/shell_integration/bash -o ~/.iterm2_shell_integration.bash",
    "alacritty" to "curl -fsSL https://github.com/alacritty/alacritty/releases/download/v0.13.0/Alacritty-v0.13.0-x86_64.tar.gz | tar -xz",
    "starship" to "curl -fsSL https://starship.rs/install.sh | sh",

    // IDEs and Text Editors
    "vscode" to "curl -fsSL https://code.visualstudio.com/sha/download?build=stable&os=linux-x64 | tar -xz",
    "sublime" to "curl -fsSL https://download.sublimetext.com/sublime_text_build_4152_x64.tar.gz | tar -xz",
    "atom" to "curl -fsSL https://atom.io/download/deb -o atom.deb && sudo dpkg -i atom.deb",
    "pycharm" to "curl -fsSL https://download.jetbrains.com/python/pycharm-community-2024.1.tar.gz | tar -xz -C /opt/",
    "eclipse" to "curl -fsSL https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/2024-03/R/eclipse-java-2024-03-R-linux-gtk-x86_64.tar.gz | tar -xz -C /opt/",

    // Version Control Systems
    "git" to "curl -fsSL https://mirrors.edge.kernel.org/pub/software/scm/git/git-2.42.0.tar.gz | tar -xz && cd git-2.42.0 && make prefix=/usr/local all && sudo make prefix=/usr/local install",
    "github_cli" to "curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg && sudo chmod go+r /usr/share/keyrings/githubcli-archive-keyring.gpg && echo deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main | sudo tee /etc/apt/sources.list.d/github-cli.list > /dev/null && sudo apt update && sudo apt install gh",
    "gitkraken" to "curl -fsSL https://release.gitkraken.com/linux/gitkraken-amd64.deb -o gitkraken.deb && sudo dpkg -i gitkraken.deb",

    // Command-Line Tools
    "tmux" to "curl -fsSL https://github.com/tmux/tmux/releases/download/3.4/tmux-3.4.tar.gz | tar -xz && cd tmux-3.4 && ./configure && make && sudo make install",
    "fzf" to "curl -fsSL https://github.com/junegunn/fzf/archive/master.tar.gz | tar -xz && ./fzf-master/install",
    "ripgrep" to "curl -fsSL https://github.com/BurntSushi/ripgrep/releases/download/14.0.3/ripgrep-14.0.3-x86_64-unknown-linux-musl.tar.gz | tar -xz && sudo cp ripgrep-14.0.3-x86_64-unknown-linux-musl/rg /usr/local/bin/",
    "screen" to "curl -fsSL https://ftp.gnu.org/gnu/screen/screen-4.9.1.tar.gz | tar -xz && cd screen-4.9.1 && ./configure && make && sudo make install",
    "ag" to "curl -fsSL https://github.com/ggreer/the_silver_searcher/archive/refs/tags/2.2.0.tar.gz | tar -xz && cd the_silver_searcher-2.2.0 && ./build.sh && sudo make install",
    "httpie" to "curl -fsSL https://packages.httpie.io/deb/KEY.gpg | sudo gpg --dearmor -o /usr/share/keyrings/httpie-archive-keyring.gpg && echo deb [arch=amd64 signed-by=/usr/share/keyrings/httpie-archive-keyring.gpg] https://packages.httpie.io/deb ./ | sudo tee /etc/apt/sources.list.d/httpie.list > /dev/null && sudo apt update && sudo apt install httpie",
    "jq" to "curl -fsSL https://github.com/stedolan/jq/releases/download/jq-1.7/jq-linux64 -o jq && chmod +x jq && sudo mv jq /usr/local/bin/",

    // Programming Languages and Frameworks
    "python" to "curl -fsSL https://www.python.org/ftp/python/3.12.1/Python-3.12.1.tgz | tar -xz && cd Python-3.12.1 && ./configure --enable-optimizations && make -j 8 && sudo make altinstall",
    "node" to "curl -fsSL https://nodejs.org/dist/v20.11.0/node-v20.11.0-linux-x64.tar.xz | tar -xJ -C /usr/local --strip-components=1",
    "rust" to "curl -fsSL https://sh.rustup.rs | sh",
    "go" to "curl -fsSL https://golang.org/dl/go1.22.0.linux-amd64.tar.gz | sudo tar -C /usr/local -xz && echo export PATH=\$PATH:/usr/local/go/bin >> ~/.bashrc",
    "ruby" to "curl -fsSL https://get.rvm.io | bash -s stable --ruby",
    "sdkman" to "curl -fsSL https://get.sdkman.io | bash",
    "django" to "pip install django",
    "react" to "npx create-react-app my-app",

    // Databases
    "postgresql" to "curl -fsSL https://ftp.postgresql.org/pub/source/v16.2/postgresql-16.2.tar.gz | tar -xz && cd postgresql-16.2 && ./configure && make && sudo make install",
    "mongodb" to "curl -fsSL https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-ubuntu2204-7.0.5.tgz | tar -xz && sudo mv mongodb-linux-x86_64-ubuntu2204-7.0.5 /usr/local/mongodb",
    "mysql" to "curl -fsSL https://dev.mysql.com/get/mysql-apt-config_0.8.28-1_all.deb -o mysql-apt-config.deb && sudo dpkg -i mysql-apt-config.deb && sudo apt-get update && sudo apt-get install mysql-server",
    "redis" to "curl -fsSL http://download.redis.io/redis-stable.tar.gz | tar -xz && cd redis-stable && make && sudo make install",

    // Containers and Virtualization
    "docker" to "curl -fsSL https://get.docker.com | sh",
    "docker_compose" to "sudo curl -fsSL https://github.com/docker/compose/releases/download/v2.24.2/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose && sudo chmod +x /usr/local/bin/docker-compose",
    "vagrant" to "curl -fsSL https://releases.hashicorp.com/vagrant/2.4.1/vagrant_2.4.1_linux_amd64.deb -o vagrant.deb && sudo dpkg -i vagrant.deb",
    "minikube" to "curl -fsSL https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 -o minikube && sudo install minikube /usr/local/bin/",

    // Cloud CLI Tools
    "aws" to "curl -fsSL https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o awscliv2.zip && unzip awscliv2.zip && sudo ./aws/install",
    "azure" to "curl -fsSL https://aka.ms/InstallAzureCLIDeb | sudo bash",
    "gcloud" to "curl -fsSL https://sdk.cloud.google.com | bash && exec -l \$SHELL",

    // Networking and Security
    "curl" to "curl -fsSL https://curl.se/download/curl-8.6.0.tar.gz | tar -xz && cd curl-8.6.0 && ./configure && make && sudo make install",
    "wget" to "curl -fsSL https://ftp.gnu.org/gnu/wget/wget-1.21.4.tar.gz | tar -xz && cd wget-1.21.4 && ./configure && make && sudo make install",
    "openssl" to "curl -fsSL https://www.openssl.org/source/openssl-3.2.1.tar.gz | tar -xz && cd openssl-3.2.1 && ./config && make && sudo make install",
    "nmap" to "curl -fsSL https://nmap.org/dist/nmap-7.94.tar.bz2 | tar -xj && cd nmap-7.94 && ./configure && make && sudo make install",
    "kubectl" to "curl -fsSL https://dl.k8s.io/release/$(curl -fsSL https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl -o kubectl && sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl",
    "terraform" to "curl -fsSL https://releases.hashicorp.com/terraform/1.7.2/terraform_1.7.2_linux_amd64.zip -o terraform.zip && unzip terraform.zip && sudo mv terraform /usr/local/bin/"
)

// Template for the test file content
val testTemplate = """
    package com.tomassirio.easyinstaller.service.impl.installer
    
    import com.tomassirio.easyinstaller.service.impl.installer.strategy.BrewStrategy
    import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategy
    import com.tomassirio.easyinstaller.service.impl.installer.strategy.DownloadStrategyContext
    import com.tomassirio.easyinstaller.style.ShellFormatter
    import org.junit.jupiter.api.Assertions.assertEquals
    import org.junit.jupiter.api.Assertions.assertTrue
    import org.junit.jupiter.api.BeforeEach
    import org.junit.jupiter.api.Test
    import org.junit.jupiter.api.assertThrows
    import org.junit.jupiter.api.extension.ExtendWith
    import org.mockito.InjectMocks
    import org.mockito.Mock
    import org.mockito.Mockito.*
    import org.mockito.junit.jupiter.MockitoExtension
    import org.springframework.test.util.ReflectionTestUtils
    import java.util.concurrent.TimeUnit
    
    @ExtendWith(MockitoExtension::class)
    class %sTest {
    
        @Mock
        private lateinit var shellFormatter: ShellFormatter
    
        @Mock
        private lateinit var downloadStrategyContext: DownloadStrategyContext
    
        @InjectMocks
        private lateinit var %s: %s
    
        private val defaultCommandField = "DEFAULT_URL"
    
        @BeforeEach
        fun setUp() {
            ReflectionTestUtils.setField(%s, defaultCommandField, "%s")
        }
    
        @Test
        fun `test successful installation with default command`() {
            val strategy: (String?) -> Unit = mock()
            `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
            `when`(downloadStrategyContext.isDefault()).thenReturn(true)
    
            %s.install()
    
            verify(shellFormatter).printInfo("Installing %s...")
            verify(strategy).invoke(%s.DEFAULT_URL)
        }
    
        @Test
        fun `test successful installation with provided package manager`() {
            val strategy: DownloadStrategy = mock(BrewStrategy::class.java)
    
            `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy::install)
    
            %s.install()
    
            verify(shellFormatter).printInfo("Installing %s...")
            verify(strategy).install("%s")
        }
    
        @Test
        fun `test exception handling during installation`() {
            val strategy: (String?) -> Unit = mock()
            `when`(downloadStrategyContext.getCurrentStrategy()).thenReturn(strategy)
            doThrow(RuntimeException("Test exception")).`when`(strategy).invoke(anyString())
    
            assertThrows<RuntimeException> {
                %s.install()
            }
    
            verify(shellFormatter).printInfo("Installing %s...")
        }
    
        @Test
        fun `test default curl URL is valid`() {
            // Retrieve the default command automatically
            val command = arrayOf("/bin/bash", "-c", %s.DEFAULT_URL)
    
            // Start the process
            val process = ProcessBuilder(*command).start()
    
            // Add a timeout to avoid hanging indefinitely
            val exitCode = if (process.waitFor(3, TimeUnit.SECONDS)) {
                process.exitValue()
            } else {
                process.destroy()
                throw RuntimeException("Process timed out")
            }
    
            // Capture both output and error streams
            val output = process.inputStream.bufferedReader().readText()
            val errorOutput = process.errorStream.bufferedReader().readText()
    
            // Assert the process exited successfully and produced expected output
            assertEquals(0, exitCode, "Process failed with exit code {$}exitCode and error: {$}errorOutput")
            assertTrue(output.contains("%s"), "Expected output to contain '%s'. Output was: {$}output")
        }
    }
""".trimIndent()

// Generate test files
for (installer in installers) {
    val installerFileName = installer.replace("Installer", "")
    val defaultCommand = commandDefaults[installerFileName.toLowerCase()] ?: "//TODO: Add default command for $installer"

    val testContent = testTemplate.format(
        installer,
        installer.replaceFirstChar { it.lowercase(Locale.getDefault()) },
        installer,
        installer.replaceFirstChar { it.lowercase(Locale.getDefault()) },
        defaultCommand,
        installer.replaceFirstChar { it.lowercase(Locale.getDefault()) },
        installerFileName,
        installer.replaceFirstChar { it.lowercase(Locale.getDefault()) },
        installer.replaceFirstChar { it.lowercase(Locale.getDefault()) },
        installerFileName,
        installerFileName.lowercase(),
        installer.replaceFirstChar { it.lowercase(Locale.getDefault()) },
        installerFileName,
        installer.replaceFirstChar { it.lowercase(Locale.getDefault()) },
        installerFileName.lowercase(Locale.getDefault()),
        installerFileName.lowercase(Locale.getDefault())
    )

    val testFile = File("$testDirectory/${installer}Test.kt")
    testFile.writeText(testContent)
    println("Generated test file for: $installer")
}

println("Test file generation completed.")