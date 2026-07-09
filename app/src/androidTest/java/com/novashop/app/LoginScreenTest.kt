package com.novashop.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun waitForLoginScreen() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("Welcome Back", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun goToRegisterScreen() {
        waitForLoginScreen()

        composeTestRule
            .onAllNodesWithText("Register", useUnmergedTree = true)
            .onFirst()
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule
                .onAllNodesWithText("Join Nova Shop", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun test_app_launches_successfully() {
        composeTestRule.waitForIdle()
    }

    @Test
    fun test_splash_screen_shows_nova_shop() {
        composeTestRule
            .onNodeWithText("Nova Shop", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun test_splash_shows_nepalese_wall_art() {
        composeTestRule
            .onNodeWithText("Nepalese Wall Art", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun test_login_screen_shows_after_splash() {
        waitForLoginScreen()

        composeTestRule
            .onNodeWithText("Welcome Back", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun test_login_screen_has_sign_in_button() {
        waitForLoginScreen()

        composeTestRule
            .onNodeWithText("Sign In", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun test_login_screen_has_register_text() {
        waitForLoginScreen()

        composeTestRule
            .onNodeWithText("Register", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun test_register_screen_shows_join_nova_shop() {
        goToRegisterScreen()

        composeTestRule
            .onNodeWithText("Join Nova Shop", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun test_register_screen_has_create_account() {
        goToRegisterScreen()

        composeTestRule
            .onNodeWithText("Create Account", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun test_register_screen_has_sign_in_link() {
        goToRegisterScreen()

        composeTestRule
            .onNodeWithText("Sign In", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun test_back_to_login_from_register() {
        goToRegisterScreen()

        composeTestRule
            .onAllNodesWithText("Sign In", useUnmergedTree = true)
            .onFirst()
            .performClick()

        waitForLoginScreen()

        composeTestRule
            .onNodeWithText("Welcome Back", useUnmergedTree = true)
            .assertExists()
    }
}