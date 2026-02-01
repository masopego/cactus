package es.masopego.cactus.auth.application

import es.masopego.cactus.auth.domain.AuthConstants
import es.masopego.cactus.auth.domain.TokenGenerator
import es.masopego.cactus.auth.domain.User
import es.masopego.cactus.auth.domain.UserRepository
import es.masopego.cactus.auth.infrastructure.supabase.SupabaseValidationService
import es.masopego.cactus.fixtures.SupabaseFixtures.createSupabaseUserResponse
import es.masopego.cactus.fixtures.SupabaseFixtures.createSupabaseUserWithMetadata
import es.masopego.cactus.fixtures.SupabaseFixtures.createUserMetadata
import es.masopego.cactus.fixtures.UserFixtures.createUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class AuthenticateWithSupabaseUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var tokenGenerator: TokenGenerator
    private lateinit var supabaseValidationService: SupabaseValidationService
    private lateinit var useCase: AuthenticateWithSupabaseUseCase

    private val testEmail = "test@example.com"
    private val testSupabaseId = UUID.randomUUID().toString()
    private val testJwtToken = "jwt-token-12345"

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        tokenGenerator = mockk()
        supabaseValidationService = mockk()
        useCase = AuthenticateWithSupabaseUseCase(userRepository, tokenGenerator, supabaseValidationService)
    }

    @Test
    fun `should authenticate existing user successfully with complete metadata`() {
        val supabaseToken = "valid-supabase-token"
        val supabaseUser = createSupabaseUserWithMetadata(
            id = testSupabaseId,
            email = testEmail,
            nickname = "testuser",
            avatar = "https://example.com/avatar.jpg"
        )
        val existingUser = createUser(
            id = UUID.fromString(testSupabaseId),
            nickname = "testuser",
            email = testEmail,
            avatar = "https://example.com/avatar.jpg"
        )

        every { supabaseValidationService.validateTokenAndGetUser(supabaseToken) } returns supabaseUser
        every { userRepository.findByEmail(testEmail) } returns existingUser
        every { tokenGenerator.generateToken(any<UserDetails>()) } returns testJwtToken

        val result = useCase.authenticateWithSupabase(supabaseToken)

        assertNotNull(result)
        assertEquals(testJwtToken, result.token)
        assertEquals("testuser", result.nickname)
        assertEquals(testEmail, result.email)
        verify { supabaseValidationService.validateTokenAndGetUser(supabaseToken) }
        verify { userRepository.findByEmail(testEmail) }
        verify { tokenGenerator.generateToken(any<UserDetails>()) }
    }

    @Test
    fun `should create new user with nickname from email when metadata is null`() {
        val supabaseToken = "valid-supabase-token"
        val supabaseUser = createSupabaseUserResponse(
            id = testSupabaseId,
            email = testEmail,
            userMetadata = null
        )
        val userSlot = slot<User>()

        every { supabaseValidationService.validateTokenAndGetUser(supabaseToken) } returns supabaseUser
        every { userRepository.findByEmail(testEmail) } returns null
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        every { tokenGenerator.generateToken(any<UserDetails>()) } returns testJwtToken

        val result = useCase.authenticateWithSupabase(supabaseToken)

        assertNotNull(result)
        assertEquals("test", result.nickname) // Email prefix before @
        assertEquals(testEmail, result.email)

        val savedUser = userSlot.captured
        assertEquals("test", savedUser.nickname)
        assertEquals(AuthConstants.DEFAULT_AVATAR_URL, savedUser.avatar)
    }

    @Test
    fun `should use default avatar when metadata avatar is null`() {
        val supabaseToken = "valid-supabase-token"
        val userMetadata = createUserMetadata(
            nickname = "testuser",
            avatar = null,
            avatarUrl = null
        )
        val supabaseUser = createSupabaseUserResponse(
            id = testSupabaseId,
            email = testEmail,
            userMetadata = userMetadata
        )
        val userSlot = slot<User>()

        every { supabaseValidationService.validateTokenAndGetUser(supabaseToken) } returns supabaseUser
        every { userRepository.findByEmail(testEmail) } returns null
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        every { tokenGenerator.generateToken(any<UserDetails>()) } returns testJwtToken

        val result = useCase.authenticateWithSupabase(supabaseToken)

        assertNotNull(result)
        val savedUser = userSlot.captured
        assertEquals(AuthConstants.DEFAULT_AVATAR_URL, savedUser.avatar)
    }

    @Test
    fun `should throw exception when supabase token is invalid`() {
        val invalidToken = "invalid-token"

        every { supabaseValidationService.validateTokenAndGetUser(invalidToken) } returns null

        val exception = assertThrows<IllegalArgumentException> {
            useCase.authenticateWithSupabase(invalidToken)
        }

        assertEquals("Invalid Supabase token", exception.message)
        verify { supabaseValidationService.validateTokenAndGetUser(invalidToken) }
    }

    @Test
    fun `should generate JWT token with correct user details`() {
        val supabaseToken = "valid-supabase-token"
        val supabaseUser = createSupabaseUserResponse(
            id = testSupabaseId,
            email = testEmail,
            userMetadata = null
        )
        val existingUser = createUser(
            id = UUID.fromString(testSupabaseId),
            nickname = "testuser",
            email = testEmail,
            avatar = AuthConstants.DEFAULT_AVATAR_URL
        )
        val userDetailsSlot = slot<UserDetails>()

        every { supabaseValidationService.validateTokenAndGetUser(supabaseToken) } returns supabaseUser
        every { userRepository.findByEmail(testEmail) } returns existingUser
        every { tokenGenerator.generateToken(capture(userDetailsSlot)) } returns testJwtToken

        useCase.authenticateWithSupabase(supabaseToken)
        
        val capturedUserDetails = userDetailsSlot.captured
        assertEquals(testEmail, capturedUserDetails.username)
        assertEquals("", capturedUserDetails.password)
        assertTrue(capturedUserDetails.authorities.any { it.authority == "ROLE_CUSTOMER" })
    }
}
