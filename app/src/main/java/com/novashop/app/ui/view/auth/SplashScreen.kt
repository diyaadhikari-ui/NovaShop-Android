package com.novashop.app.ui.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novashop.app.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreenUI() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2A1F14)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = "Nova Shop Logo",
            tint = Color(0xFFE07B39),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nova Shop",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Nepalese Wall Art",
            color = Color(0xFFE07B39),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        authViewModel.checkCurrentUser()
        delay(500)

        val user = authViewModel.currentUser.value
        if (user != null) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    SplashScreenUI()
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun SplashScreenPreview() {
    SplashScreenUI()
}