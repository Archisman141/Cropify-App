package com.tech.cropify.screens

import android.R
import android.widget.Toast
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.tech.cropify.navigation.Routes
import com.tech.cropify.ui.theme.CropifyColors
import com.tech.cropify.util.GoogleSignInUtils
import com.tech.cropify.util.SharedPreferenceManager
import com.tech.cropify.viewModel.AuthUiState
import com.tech.cropify.viewModel.LoginViewModel
import com.tech.cropify.viewModel.StateHolder

enum class AuthTab { LOGIN, REGISTER }


@Composable
fun AuthScreen(navController: NavHostController) {
    var activeTab by remember { mutableStateOf(AuthTab.LOGIN) }
    val viewModel: LoginViewModel = hiltViewModel()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val accessToken by viewModel.token.collectAsState()

    LaunchedEffect(accessToken) {
        Log.d("AuthScreen", "accessToken changed: $accessToken")
        if (accessToken != null) {
            navController.navigate(Routes.MainScreen)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CropifyColors.Surface)
                .verticalScroll(rememberScrollState())
        ) {
            HeroSection()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-20).dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(CropifyColors.White)
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                AuthToggle(
                    activeTab = activeTab,
                    onTabChange = { activeTab = it }
                )

                Spacer(Modifier.height(20.dp))

                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        fadeIn(tween(220)) togetherWith fadeOut(tween(180))
                    },
                    label = "auth_tab_content"
                ) { tab ->
                    when (tab) {
                        AuthTab.LOGIN    -> LoginForm(viewModel, context, navController)
                        AuthTab.REGISTER -> RegisterForm(viewModel, context, navController)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // ── Full screen loading overlay ─────────────────────────────
        if (uiState is AuthUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CropifyColors.White),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = CropifyColors.ForestGreen,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // ── Error toast/snackbar ─────────────────────────────────────
        if (uiState is AuthUiState.Error) {
            LaunchedEffect(uiState) {
                Toast.makeText(
                    context,
                    (uiState as AuthUiState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
//                viewModel.resetState()
            }
        }
    }
}

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(CropifyColors.ForestGreen)
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(x = 220.dp, y = (-30).dp)
                .rotate(15f)
                .clip(RoundedCornerShape(50))
                .background(CropifyColors.LeafDark.copy(alpha = 0.7f))
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(x = 20.dp, y = 170.dp)
                .rotate(-25f)
                .clip(RoundedCornerShape(50))
                .background(CropifyColors.LeafMid.copy(alpha = 0.5f))
        )
        Box(
            modifier = Modifier
                .size(48.dp)
                .offset(x = 280.dp, y = 120.dp)
                .rotate(45f)
                .clip(RoundedCornerShape(50))
                .background(CropifyColors.LeafLight.copy(alpha = 0.4f))
        )
        Box(
            modifier = Modifier
                .size(30.dp)
                .offset(x = 160.dp, y = 60.dp)
                .rotate(30f)
                .clip(RoundedCornerShape(50))
                .background(CropifyColors.SageGreen.copy(alpha = 0.3f))
        )

        // Brand content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 28.dp, bottom = 16.dp)
        ) {
            // Leaf icon — pure shapes
            CropifyLeafIcon()

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Cropify",
                style = TextStyle(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF5F0E8),
                    letterSpacing = (-0.5).sp
                )
            )
            Text(
                text = "SMART CROP INTELLIGENCE",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CropifyColors.SageGreen,
                    letterSpacing = 2.sp
                )
            )
        }
    }
}

@Composable
fun CropifyLeafIcon() {
    Box(modifier = Modifier.size(36.dp)) {
        listOf(0f, 60f, -60f).forEach { angle ->
            Box(
                modifier = Modifier
                    .size(width = 14.dp, height = 22.dp)
                    .align(Alignment.Center)
                    .rotate(angle)
                    .clip(RoundedCornerShape(50))
                    .background(CropifyColors.LeafLight)
            )
        }
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(14.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(2.dp))
                .background(CropifyColors.ForestGreen)
        )
    }
}

@Composable
fun AuthToggle(
    activeTab: AuthTab,
    onTabChange: (AuthTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CropifyColors.ToggleBg)
            .padding(3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AuthTab.values().forEach { tab ->
                val isActive = tab == activeTab
                val bgColor by animateColorAsState(
                    targetValue = if (isActive) CropifyColors.ForestGreen else Color.Transparent,
                    animationSpec = tween(200),
                    label = "tab_bg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isActive) Color(0xFFF5F0E8) else CropifyColors.TextMuted,
                    animationSpec = tween(200),
                    label = "tab_text"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(bgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabChange(tab) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (tab == AuthTab.LOGIN) "Sign in" else "Register",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LoginForm(viewModel: LoginViewModel, context: Context, navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val accessToken by viewModel.token.collectAsState()

    Column {
        // Badge
        StatusBadge(text = "Welcome back, farmer")
        Spacer(Modifier.height(6.dp))

        Text(
            text = "Good to see you",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CropifyColors.TextPrimary,
                letterSpacing = (-0.3).sp
            )
        )
        Text(
            text = "Sign in to monitor your crops",
            style = TextStyle(
                fontSize = 13.sp,
                color = CropifyColors.TextMuted
            )
        )

        Spacer(Modifier.height(22.dp))

        CropifyLabel("Email address")
        CropifyTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Enter your email",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )

        Spacer(Modifier.height(4.dp))
        CropifyLabel("Password")
        CropifyTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Enter your password",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            isPassword = true,
            showPassword = showPassword,
            onTogglePassword = { showPassword = !showPassword }
        )

        // Forgot password
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Text(
                text = "Forgot password?",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CropifyColors.ForestGreen
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { }
            )
        }

        Spacer(Modifier.height(4.dp))
        CropifyPrimaryButton(text = "Sign In", onClick = {
            viewModel.login(emailId = email, password = password, context = context)

            if(accessToken != null ||
                SharedPreferenceManager.getToken(context) !== null ||
                StateHolder.accessToken?.text != null){
                navController.navigate(Routes.MainScreen)
            }
        })

        DividerWithText("or")

        GoogleSignInButton(viewModel)

        Spacer(Modifier.height(16.dp))
    }
}

// ── Register form ─────────────────────────────────────────────────────────────
@Composable
fun RegisterForm(viewModel: LoginViewModel, context: Context, navController: NavHostController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val passwordStrength = remember(password) {
        when {
            password.length >= 12 && password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() } -> 4
            password.length >= 10 && password.any { it.isDigit() } -> 3
            password.length >= 8 -> 2
            password.isNotEmpty() -> 1
            else -> 0
        }
    }

    Column {
        StatusBadge(text = "Join 50,000+ farmers")
        Spacer(Modifier.height(6.dp))

        Text(
            text = "Create account",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CropifyColors.TextPrimary,
                letterSpacing = (-0.3).sp
            )
        )
        Text(
            text = "Start your crop intelligence journey",
            style = TextStyle(
                fontSize = 13.sp,
                color = CropifyColors.TextMuted
            )
        )

        Spacer(Modifier.height(22.dp))

        // Name row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                CropifyLabel("First name")
                CropifyTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = "Ravi",
                    imeAction = ImeAction.Next
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                CropifyLabel("Last name")
                CropifyTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = "Kumar",
                    imeAction = ImeAction.Next
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        CropifyLabel("Email address")
        CropifyTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "you@farm.com",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )

        Spacer(Modifier.height(4.dp))
        CropifyLabel("Password")
        CropifyTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "min. 8 characters",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            isPassword = true,
            showPassword = showPassword,
            onTogglePassword = { showPassword = !showPassword }
        )

        // Password strength bar
        if (password.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            PasswordStrengthBar(strength = passwordStrength)
            Spacer(Modifier.height(4.dp))
            Text(
                text = when (passwordStrength) {
                    1 -> "Weak — add numbers and symbols"
                    2 -> "Fair — make it longer"
                    3 -> "Good — add a symbol to make it strong"
                    4 -> "Strong password"
                    else -> ""
                },
                style = TextStyle(fontSize = 11.sp, color = CropifyColors.TextHint)
            )
        }

        Spacer(Modifier.height(18.dp))
        CropifyPrimaryButton(text = "Create Account", onClick = {
            viewModel.register(username = "$firstName $lastName", password = password)

            navController.navigate(Routes.LoginScreen)
        })

        Spacer(Modifier.height(12.dp))
        Text(
            text = "By registering you agree to our Terms & Privacy Policy",
            style = TextStyle(
                fontSize = 11.sp,
                color = CropifyColors.TextHint,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
    }
}

// ── Reusable components ───────────────────────────────────────────────────────

@Composable
fun StatusBadge(text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CropifyColors.BadgeBg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(CropifyColors.BadgeDot)
        )
        Text(
            text = text,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = CropifyColors.BadgeText
            )
        )
    }
}

@Composable
fun CropifyLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = CropifyColors.TextLabel,
            letterSpacing = 0.8.sp
        ),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun CropifyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) CropifyColors.InputBorderFocus else CropifyColors.InputBorder,
        animationSpec = tween(150),
        label = "border"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(fontSize = 14.sp, color = CropifyColors.TextHint)
            )
        },
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = CropifyColors.TextPrimary
        ),
        visualTransformation = if (isPassword && !showPassword)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        painter = painterResource(
                            if (showPassword) R.drawable.ic_menu_view
                            else R.drawable.ic_secure
                        ),
                        contentDescription = if (showPassword) "Hide" else "Show",
                        tint = CropifyColors.TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = CropifyColors.InputBorder,
            focusedContainerColor = CropifyColors.White,
            unfocusedContainerColor = CropifyColors.White,
            cursorColor = CropifyColors.ForestGreen
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

@Composable
fun CropifyPrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CropifyColors.Amber,
            contentColor = CropifyColors.ForestGreen
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp
            )
        )
    }
}

@Composable
fun DividerWithText(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Divider(modifier = Modifier.weight(1f), color = CropifyColors.DividerColor, thickness = 1.dp)
        Text(text = text, style = TextStyle(fontSize = 12.sp, color = CropifyColors.TextHint))
        Divider(modifier = Modifier.weight(1f), color = CropifyColors.DividerColor, thickness = 1.dp)
    }
}

@Composable
fun GoogleSignInButton(viewModel: LoginViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is AuthUiState.Loading

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        GoogleSignInUtils.doGoogleSignIn(context, scope, null) { idToken, _, _ ->
            viewModel.loginWithGoogle(idToken, context)
        }
    }

    OutlinedButton(
        onClick = {
            GoogleSignInUtils.doGoogleSignIn(context, scope, launcher) { idToken, _, _ ->
                viewModel.loginWithGoogle(idToken, context)
            }
        },
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, CropifyColors.GoogleBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CropifyColors.White,
            contentColor = Color(0xFF3A3020)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = CropifyColors.ForestGreen,
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GoogleIcon()
                Text(
                    text = "Continue with Google",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}

@Composable
fun GoogleIcon() {
    // Simple G shape using Canvas
    Canvas(modifier = Modifier.size(18.dp)) {
        val s = size.minDimension
        // Blue arc
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -10f,
            sweepAngle = 100f,
            useCenter = false,
            style = Stroke(width = s * 0.18f)
        )
        // Green arc
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 90f,
            sweepAngle = 100f,
            useCenter = false,
            style = Stroke(width = s * 0.18f)
        )
        // Yellow arc
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 190f,
            sweepAngle = 80f,
            useCenter = false,
            style = Stroke(width = s * 0.18f)
        )
        // Red arc
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 270f,
            sweepAngle = 80f,
            useCenter = false,
            style = Stroke(width = s * 0.18f)
        )
    }
}

@Composable
fun PasswordStrengthBar(strength: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(4) { index ->
            val filled = index < strength
            val barColor by animateColorAsState(
                targetValue = when {
                    !filled -> CropifyColors.StrengthBg
                    strength <= 1 -> Color(0xFFEF4444)
                    strength == 2 -> Color(0xFFC8A535)
                    strength == 3 -> Color(0xFF7AB87A)
                    else -> Color(0xFF1B3A2D)
                },
                animationSpec = tween(300),
                label = "strength_bar"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}