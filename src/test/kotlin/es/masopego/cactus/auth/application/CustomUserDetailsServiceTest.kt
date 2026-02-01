package es.masopego.cactus.auth.application

import es.masopego.cactus.auth.domain.UserRepository
import es.masopego.cactus.fixtures.UserFixtures.createUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.userdetails.UsernameNotFoundException

class CustomUserDetailsServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var service: CustomUserDetailsService

    private val testEmail = "test@example.com"

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        service = CustomUserDetailsService(userRepository)
    }

    @Test
    fun `should load user by username successfully when user exists`() {
        val user = createUser(email = testEmail)

        every { userRepository.findByEmail(testEmail) } returns user

        val userDetails = service.loadUserByUsername(testEmail)

        assertNotNull(userDetails)
        assertEquals(testEmail, userDetails.username)
        assertEquals("", userDetails.password)
        assertTrue(userDetails.authorities.any { it.authority == "ROLE_CUSTOMER" })
        assertTrue(userDetails.isEnabled)
        assertTrue(userDetails.isAccountNonExpired)
        assertTrue(userDetails.isAccountNonLocked)
        assertTrue(userDetails.isCredentialsNonExpired)
        verify { userRepository.findByEmail(testEmail) }
    }

    @Test
    fun `should throw UsernameNotFoundException when user does not exist`() {
        every { userRepository.findByEmail(testEmail) } returns null

        val exception = assertThrows<UsernameNotFoundException> {
            service.loadUserByUsername(testEmail)
        }

        assertEquals("User not found: $testEmail", exception.message)
        verify { userRepository.findByEmail(testEmail) }
    }


    @Test
    fun `should return UserDetails with ROLE_CUSTOMER authority`() {
        val user = createUser(email = testEmail)

        every { userRepository.findByEmail(testEmail) } returns user

        val userDetails = service.loadUserByUsername(testEmail)

        assertEquals(1, userDetails.authorities.size)
        assertTrue(userDetails.authorities.any { it.authority == "ROLE_CUSTOMER" })
    }

    @Test
    fun `should handle different email formats correctly`() {
        val emails = listOf(
            "simple@example.com",
            "user.name@example.com",
            "user+tag@example.es",
            "user_name@subdomain.example.com"
        )

        emails.forEach { email ->
            val user = createUser(email = email, nickname = "user")
            every { userRepository.findByEmail(email) } returns user
        }

        emails.forEach { email ->
            val userDetails = service.loadUserByUsername(email)
            assertEquals(email, userDetails.username)
        }
    }

    @Test
    fun `should throw exception with correct message when user not found`() {
        val nonExistentEmail = "nonexistent@example.com"
        every { userRepository.findByEmail(nonExistentEmail) } returns null
        
        val exception = assertThrows<UsernameNotFoundException> {
            service.loadUserByUsername(nonExistentEmail)
        }

        assertTrue(exception.message!!.contains(nonExistentEmail))
        assertEquals("User not found: $nonExistentEmail", exception.message)
    }
}

