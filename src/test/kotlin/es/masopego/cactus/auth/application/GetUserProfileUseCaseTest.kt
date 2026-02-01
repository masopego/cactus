package es.masopego.cactus.auth.application

import es.masopego.cactus.auth.domain.UserRepository
import es.masopego.cactus.fixtures.UserFixtures.createUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class GetUserProfileUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var useCase: GetUserProfileUseCase

    private val testEmail = "test@example.com"

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        useCase = GetUserProfileUseCase(userRepository)
    }

    @Test
    fun `should return user profile successfully when user exists`() {
        val userId = UUID.randomUUID()
        val expectedUser = createUser(
            id = userId,
            nickname = "testuser",
            email = testEmail,
            avatar = "https://example.com/avatar.jpg"
        )

        every { userRepository.findByEmail(testEmail) } returns expectedUser

        val result = useCase.execute(testEmail)

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals(userId, user!!.id)
        assertEquals("testuser", user.nickname)
        assertEquals(testEmail, user.email)
        assertEquals("https://example.com/avatar.jpg", user.avatar)
        verify { userRepository.findByEmail(testEmail) }
    }

    @Test
    fun `should fail when user does not exist`() {
        every { userRepository.findByEmail(testEmail) } returns null

        val result = useCase.execute(testEmail)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is IllegalArgumentException)
        assertEquals("User not found", exception!!.message)
        verify { userRepository.findByEmail(testEmail) }
    }
}
