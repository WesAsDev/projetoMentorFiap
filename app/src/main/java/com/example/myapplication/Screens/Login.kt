package com.example.myapplication.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.Services.AuthenticationService
import com.example.myapplication.Services.ToastService
import com.example.myapplication.Services.UserServices
import com.example.myapplication.Util.Validators
import com.example.myapplication.ui.theme.Pink40
import com.example.myapplication.ui.theme.Pink80
import com.example.myapplication.ui.theme.Purple40
import com.example.myapplication.ui.theme.Purple80
import com.example.myapplication.ui.theme.PurpleGrey40
import com.google.firebase.messaging.FirebaseMessaging

@Composable
fun Login(OnNavigateToSignUp: () -> Unit, OnNavigateToHome: () -> Unit){
    val AuthService = AuthenticationService()
    var emailState = remember {
        mutableStateOf("")
    }

    var senhaState = remember {
        mutableStateOf("")
    }

    var isValidEmail = remember{
        mutableStateOf(false)
    }

    if(UserServices().isUserLogged()){

        OnNavigateToHome()
    }

    var loading = remember {
        mutableStateOf(false)
    }

    var passErrorMessage = remember{
        mutableStateOf("")
    }


    Column(modifier = Modifier
        .padding(32.dp)
        .fillMaxSize()){
        Text(text = "Login", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 30.sp)


        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {

            Spacer(modifier = Modifier.size(30.dp))

            Column() {

                OutlinedTextField(label = { Text(text="Email", fontSize = 20.sp) }, value = emailState.value, onValueChange = {
                    emailState.value = it
                    isValidEmail.value = !Validators.isValidEmail(it)
                    Log.i("testeEmail", it)
                },
                    isError = isValidEmail.value, singleLine = true)

            }

            Spacer(modifier = Modifier.size(30.dp))

            Column() {
                OutlinedTextField(label = { Text(text="Senha", fontSize = 20.sp) }, value = senhaState.value, onValueChange = {
                    senhaState.value = it
                    Log.i("testeSenha", it)
                }, visualTransformation = PasswordVisualTransformation(), singleLine = true, isError = passErrorMessage.value != "")

                Text(text=passErrorMessage.value, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.size(30.dp))

        }


        Button(onClick = {

            if(emailState.value == ""){
                ToastService.makeToastMessage("Por favor insira um email", Toast.LENGTH_SHORT)
                return@Button
            }

            if(senhaState.value == ""){
                ToastService.makeToastMessage("Por favor insira sua senha", Toast.LENGTH_SHORT)
                return@Button
            }
            loading.value = true
            Log.i("teste", "${emailState.value} - ${senhaState.value}")
            AuthService.login(emailState.value, senhaState.value) {
                if(!it){
                    loading.value = false
                    return@login
                }

                OnNavigateToHome()
                FirebaseMessaging.getInstance().getToken().addOnSuccessListener {token->
                    Log.d("tokenTalvez", "${token}")
                    UserServices().saveUserNotificationToken(token)
                }

            }

        }, modifier = Modifier.fillMaxWidth()) {
            Text(text="login", modifier = Modifier.padding(20.dp), fontSize = 20.sp)

        }
        Spacer(modifier = Modifier.size(30.dp))
        Button(onClick = {
            OnNavigateToSignUp()

        }, modifier = Modifier.fillMaxWidth(), colors = ButtonColors(Purple40, Purple80, Purple80, PurpleGrey40)) {
            Text(text="Cadastre-se", modifier = Modifier.padding(20.dp), fontSize = 20.sp)

        }

        if(!loading.value) return

        Dialog(onDismissRequest = { }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp).align(Alignment.CenterHorizontally),

                shape = RoundedCornerShape(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp).align(Alignment.CenterVertically)
                    )
                }
            }
        }

    }
}
