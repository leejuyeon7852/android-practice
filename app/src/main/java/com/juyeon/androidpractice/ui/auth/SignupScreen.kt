package com.juyeon.androidpractice.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juyeon.androidpractice.model.User
import com.juyeon.androidpractice.ui.theme.GradientStart

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val draft = viewModel.userDraft
    var passwordConfirm by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordConfirmVisible by remember { mutableStateOf(false) }

    var nationalityExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }
    val nationalities = listOf("대한민국", "미국", "일본", "중국", "기타")
    val genders = listOf("남성", "여성", "선택 안함")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "회원가입",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = GradientStart
        )
        Text(
            text = "정보를 입력하고 시작하세요",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // 닉네임
        AuthTextField(
            value = draft.nickname,
            onValueChange = { viewModel.updateDraft { copy(nickname = it) } },
            placeholder = "닉네임 *"
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 이메일
        AuthTextField(
            value = draft.email,
            onValueChange = { viewModel.updateDraft { copy(email = it) } },
            placeholder = "이메일 *",
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 아이디
        AuthTextField(
            value = draft.userId,
            onValueChange = { viewModel.updateDraft { copy(userId = it) } },
            placeholder = "아이디 * (영문과 숫자 4자 이상)"
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 비밀번호
        OutlinedTextField(
            value = draft.password,
            onValueChange = { viewModel.updateDraft { copy(password = it) } },
            placeholder = { Text("비밀번호* (영문과 숫자 8자 이상)") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 비밀번호 확인
        OutlinedTextField(
            value = passwordConfirm,
            onValueChange = { passwordConfirm = it; viewModel.clearSignupError() },
            placeholder = { Text("비밀번호 확인 *") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordConfirmVisible = !passwordConfirmVisible }) {
                    Icon(
                        if (passwordConfirmVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = if (passwordConfirm.isNotEmpty() && draft.password != passwordConfirm)
                    MaterialTheme.colorScheme.error else Color.LightGray
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordConfirm.isNotEmpty() && draft.password != passwordConfirm) {
            Text(
                "비밀번호가 일치하지 않습니다",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 국적 드롭다운
        DropdownField(
            value = draft.nationality,
            placeholder = "국적 (선택)",
            expanded = nationalityExpanded,
            options = nationalities,
            onExpandedChange = { nationalityExpanded = it },
            onOptionSelected = { viewModel.updateDraft { copy(nationality = it) }; nationalityExpanded = false }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 성별 드롭다운
        DropdownField(
            value = draft.gender,
            placeholder = "성별 (선택)",
            expanded = genderExpanded,
            options = genders,
            onExpandedChange = { genderExpanded = it },
            onOptionSelected = { viewModel.updateDraft { copy(gender = it) }; genderExpanded = false }
        )

        // 에러 메시지
        viewModel.signupError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                viewModel.signup(
                    user = draft,
                    password = draft.password,
                    passwordConfirm = passwordConfirm,
                    onSuccess = onSignupSuccess
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("회원가입", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("이미 계정이 있으신가요?  ", fontSize = 14.sp, color = Color.Gray)
            TextButton(onClick = onNavigateToLogin, contentPadding = PaddingValues(0.dp)) {
                Text("로그인", fontSize = 14.sp, color = GradientStart, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    value: String,
    placeholder: String,
    expanded: Boolean,
    options: List<String>,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder) },
            trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignupScreenPreview() {
    SignupScreen(onSignupSuccess = {}, onNavigateToLogin = {})
}
