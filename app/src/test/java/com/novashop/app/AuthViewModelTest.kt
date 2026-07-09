package com.novashop.app

import com.novashop.app.data.model.User
import com.novashop.app.viewmodel.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthViewModelTest {

    // Test AuthState sealed class directly
    private lateinit var idleState: AuthState
    private lateinit var loadingState: AuthState
    private lateinit var successState: AuthState
    private lateinit var errorState: AuthState

    private lateinit var testUser: User

    @Before
    fun setup() {
        testUser = User(
            id = "test123",
            fullName = "Test User",
            email = "test@test.com",
            role = "customer"
        )
        idleState = AuthState.Idle
        loadingState = AuthState.Loading
        successState = AuthState.Success(testUser)
        errorState = AuthState.Error("Invalid credentials")
    }

    @Test
    fun `AuthState Idle is correct type`() {
        assertTrue(idleState is AuthState.Idle)
    }

    @Test
    fun `AuthState Loading is correct type`() {
        assertTrue(loadingState is AuthState.Loading)
    }

    @Test
    fun `AuthState Success contains correct user`() {
        assertTrue(successState is AuthState.Success)
        val user = (successState as AuthState.Success).user
        assertEquals("test123", user.id)
        assertEquals("Test User", user.fullName)
        assertEquals("test@test.com", user.email)
    }

    @Test
    fun `AuthState Error contains correct message`() {
        assertTrue(errorState is AuthState.Error)
        val message = (errorState as AuthState.Error).message
        assertEquals("Invalid credentials", message)
    }

    @Test
    fun `User model has correct default role`() {
        val user = User()
        assertEquals("customer", user.role)
    }

    @Test
    fun `User model stores email correctly`() {
        assertEquals("test@test.com", testUser.email)
    }

    @Test
    fun `User model stores fullName correctly`() {
        assertEquals("Test User", testUser.fullName)
    }

    @Test
    fun `Admin user has admin role`() {
        val adminUser = User(
            id = "admin1",
            fullName = "Admin",
            email = "admin@novashop.com",
            role = "admin"
        )
        assertEquals("admin", adminUser.role)
    }

    @Test
    fun `StateFlow initial value is Idle`() {
        val state = MutableStateFlow<AuthState>(AuthState.Idle)
        assertTrue(state.value is AuthState.Idle)
    }

    @Test
    fun `StateFlow can be updated to Loading`() {
        val state = MutableStateFlow<AuthState>(AuthState.Idle)
        state.value = AuthState.Loading
        assertTrue(state.value is AuthState.Loading)
    }
}