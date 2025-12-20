package com.agrodiary.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrodiary.ui.components.AgroDiaryButton
import com.agrodiary.ui.components.AgroDiaryTextField
import com.agrodiary.ui.theme.AgroDiaryTheme

/**
 * Registration screen for new user creation.
 *
 * @param onRegisterSuccess Callback when registration is successful
 * @param onNavigateToLogin Callback to navigate back to login
 * @param viewModel AuthViewModel instance
 */
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val username by viewModel.registerUsername.collectAsState()
    val password by viewModel.registerPassword.collectAsState()
    val confirmPassword by viewModel.registerConfirmPassword.collectAsState()
    val displayName by viewModel.registerDisplayName.collectAsState()
    val farmName by viewModel.registerFarmName.collectAsState()
    val authState by viewModel.authState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onRegisterSuccess()
                viewModel.resetState()
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Logo
                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "AgroDiary",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Регистрация",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Создайте аккаунт для доступа к журналу фермы",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Display name field
                AgroDiaryTextField(
                    value = displayName,
                    onValueChange = { viewModel.setRegisterDisplayName(it) },
                    label = "Ваше имя *",
                    placeholder = "Как вас зовут?",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Farm name field (optional)
                AgroDiaryTextField(
                    value = farmName,
                    onValueChange = { viewModel.setRegisterFarmName(it) },
                    label = "Название фермы",
                    placeholder = "Введите название фермы (необязательно)",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Данные для входа",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Username field
                AgroDiaryTextField(
                    value = username,
                    onValueChange = { viewModel.setRegisterUsername(it) },
                    label = "Имя пользователя *",
                    placeholder = "Минимум 3 символа",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                AgroDiaryTextField(
                    value = password,
                    onValueChange = { viewModel.setRegisterPassword(it) },
                    label = "Пароль *",
                    placeholder = "Минимум 4 символа",
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (passwordVisible) {
                                    "Скрыть пароль"
                                } else {
                                    "Показать пароль"
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm password field
                AgroDiaryTextField(
                    value = confirmPassword,
                    onValueChange = { viewModel.setRegisterConfirmPassword(it) },
                    label = "Подтвердите пароль *",
                    placeholder = "Введите пароль ещё раз",
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (authState !is AuthState.Loading) {
                                viewModel.register()
                            }
                        }
                    ),
                    visualTransformation = if (confirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (confirmPasswordVisible) {
                                    "Скрыть пароль"
                                } else {
                                    "Показать пароль"
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Register button
                AgroDiaryButton(
                    text = "Зарегистрироваться",
                    onClick = { viewModel.register() },
                    enabled = authState !is AuthState.Loading,
                    modifier = Modifier.fillMaxWidth()
                )

                if (authState is AuthState.Loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login link
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Уже есть аккаунт? Войти",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    AgroDiaryTheme {
        RegisterScreenContent(
            username = "",
            password = "",
            confirmPassword = "",
            displayName = "",
            farmName = "",
            isLoading = false,
            onUsernameChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onDisplayNameChange = {},
            onFarmNameChange = {},
            onRegisterClick = {},
            onNavigateToLogin = {}
        )
    }
}

@Composable
private fun RegisterScreenContent(
    username: String,
    password: String,
    confirmPassword: String,
    displayName: String,
    farmName: String,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onFarmNameChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AgroDiary",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            AgroDiaryTextField(
                value = displayName,
                onValueChange = onDisplayNameChange,
                label = "Ваше имя *",
                placeholder = "Как вас зовут?",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AgroDiaryTextField(
                value = farmName,
                onValueChange = onFarmNameChange,
                label = "Название фермы",
                placeholder = "Необязательно",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AgroDiaryTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = "Имя пользователя *",
                placeholder = "Минимум 3 символа",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AgroDiaryTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Пароль *",
                placeholder = "Минимум 4 символа",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AgroDiaryTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Подтвердите пароль *",
                placeholder = "Введите пароль ещё раз",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            AgroDiaryButton(
                text = "Зарегистрироваться",
                onClick = onRegisterClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Уже есть аккаунт? Войти",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
