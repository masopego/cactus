package es.masopego.cactus.auth.application

import es.masopego.cactus.auth.domain.User
import es.masopego.cactus.auth.domain.UserRepository
import es.masopego.cactus.fixtures.UserFixtures.createUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class UpdateProfileUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var useCase: UpdateProfileUseCase

    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        useCase = UpdateProfileUseCase(userRepository)
    }

    @Test
    fun `should update user profile successfully when user exists`() {
        val existingUser = createUser(
            id = userId,
            nickname = "oldnickname",
            email = "test@example.com",
            avatar = "https://example.com/old-avatar.jpg"
        )
        val newNickname = "newnickname"
        val newAvatar = "https://example.com/new-avatar.jpg"
        val userSlot = slot<User>()

        every { userRepository.findById(userId) } returns existingUser
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        val result = useCase.execute(userId, newNickname, newAvatar)

        assertTrue(result.isSuccess)
        val updatedUser = result.getOrNull()
        assertNotNull(updatedUser)
        assertEquals(userId, updatedUser!!.id)
        assertEquals(newNickname, updatedUser.nickname)
        assertEquals(newAvatar, updatedUser.avatar)
        assertEquals(existingUser.email, updatedUser.email) // Email should not change
        assertEquals(existingUser.createDate, updatedUser.createDate) // createDate should not change

        verify { userRepository.findById(userId) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `should fail when user does not exist`() {
        val nonExistentUserId = UUID.randomUUID()
        val newNickname = "newnickname"
        val newAvatar = "https://example.com/new-avatar.jpg"

        every { userRepository.findById(nonExistentUserId) } returns null

        val result = useCase.execute(nonExistentUserId, newNickname, newAvatar)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is UserNotFoundException)
        assertEquals("User not found: $nonExistentUserId", exception!!.message)
        verify { userRepository.findById(nonExistentUserId) }
    }

    @Test
    fun `should preserve email and createDate when updating profile`() {
        val email = "preserve@example.com"
        val createDate = LocalDateTime.of(2024, 6, 15, 10, 30)
        val existingUser = createUser(
            id = userId,
            nickname = "oldnickname",
            email = email,
            avatar = "https://example.com/old-avatar.jpg",
            createDate = createDate
        )
        val userSlot = slot<User>()

        every { userRepository.findById(userId) } returns existingUser
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        val result = useCase.execute(userId, "newnickname", "https://example.com/new-avatar.jpg")

        assertTrue(result.isSuccess)
        val savedUser = userSlot.captured
        assertEquals(email, savedUser.email)
        assertEquals(createDate, savedUser.createDate)
    }

    @Test
    fun `should update only nickname when avatar remains the same`() {
        val avatar = "https://example.com/avatar.jpg"
        val existingUser = createUser(
            id = userId,
            nickname = "oldnickname",
            email = "test@example.com",
            avatar = avatar
        )
        val userSlot = slot<User>()

        every { userRepository.findById(userId) } returns existingUser
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        val result = useCase.execute(userId, "newnickname", avatar)

        assertTrue(result.isSuccess)
        val savedUser = userSlot.captured
        assertEquals("newnickname", savedUser.nickname)
        assertEquals(avatar, savedUser.avatar)
    }

    @Test
    fun `should update only avatar when nickname remains the same`() {
        val nickname = "unchangednickname"
        val existingUser = createUser(
            id = userId,
            nickname = nickname,
            email = "test@example.com",
            avatar = "https://example.com/old-avatar.jpg"
        )
        val userSlot = slot<User>()

        every { userRepository.findById(userId) } returns existingUser
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

        val result = useCase.execute(userId, nickname, "https://example.com/new-avatar.jpg")

        assertTrue(result.isSuccess)
        val savedUser = userSlot.captured
        assertEquals(nickname, savedUser.nickname)
        assertEquals("https://example.com/new-avatar.jpg", savedUser.avatar)
    }
}
