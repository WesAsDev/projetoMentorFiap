package com.example.myapplication.Screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.Components.CardUsuario
import com.example.myapplication.Components.Filters
import com.example.myapplication.Model.UserModel
import com.example.myapplication.Services.MatchServices
import com.example.myapplication.Services.UserServices
import com.example.myapplication.Util.Tags

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun Home(){
    var uss = remember{
        mutableStateOf(mutableListOf(UserModel()))
    }

    var actualUser = remember{
        mutableStateOf(UserModel())
    }

    var hasUser = remember{
        mutableStateOf(false)
    }

    var reject = remember{
        mutableStateOf(false)
    }



    LaunchedEffect(true){
        UserServices().getUserList(){ uList ->

            uss.value = uList.toMutableList()
            Log.d("ENTROU VALUE", "${uss.value}")
            if(uss.value.size >0) {
                actualUser.value = uss.value[0]
                hasUser.value = true
                Log.d("ENTROU ANTESLISCE", "${uss.value[0]}")
            }

        }
    }

    fun match(){
        Log.d("INICIOU", "${uss}")
        reject.value = false
        if (uss != null) {
            actualUser.value.id?.let { MatchServices().findMatch(it) };
            uss.value.removeAt(0)
        }

        if (uss.value.isNotEmpty()) {
            actualUser.value = uss.value[0]
        } else {
            hasUser.value = false
        }
    }

    fun reject(){
        reject.value = true
        if (uss != null) {
            actualUser.value.id?.let { MatchServices().reject(it) };
            uss.value.removeAt(0)
        }

        if (uss.value.isNotEmpty()) {
            actualUser.value = uss.value[0]
        } else {
            hasUser.value = false
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()){
        if(hasUser.value){
            AnimatedContent(
                targetState = actualUser.value,
                transitionSpec = {
                    // Compare the incoming number with the previous number.
                    if (reject.value) {
                        // If the target number is larger, it slides up and fades in
                        // while the initial (smaller) number slides up and fades out.
                        slideInHorizontally { height -> -height } + fadeIn() with
                                slideOutHorizontally { height -> height } + fadeOut()
                    } else {
                        // If the target number is smaller, it slides down and fades in
                        // while the initial number slides down and fades out.
                        slideInHorizontally { height -> height } + fadeIn() with
                                slideOutHorizontally { height -> -height } + fadeOut()
                    }.using(
                        // Disable clipping since the faded slide-in/out should
                        // be displayed out of bounds.
                        SizeTransform(clip = false)
                    )
                }
            ) { acc ->
                CardUsuario(acc,{match()},acc.mentor,{reject()})
            }


//            UserSelectionScreen( actualUser.value.id,  actualUser.value.nome, actualUser.value.email, actualUser.value.mentor) {
//
//            }
        }



        /*       if(uss.value.size > 0){
                   Log.d("ENTROU USER5", "${uss}")
                   LazyRow(userScrollEnabled = true, modifier = Modifier.width(500.dp).height(900.dp)) {
                       items(uss.value){
                               val user = it
                               user.id?.let { user.nome?.let { it1 ->
                                   user.email?.let { it2 ->
                                       UserSelectionScreen(it,
                                           it1, it2
                                       )
                                   }
                               } }
                       }
                   }
               }*/
    }
}


@Composable
fun UserSelectionScreen(
    uid: String?,
    nome: String?,
    email: String?,
    mentor: Boolean,
    onMatch: () -> Unit
){
    Column(
        modifier = Modifier
            .padding(32.dp)
            .background(Color.Blue)
            .width(300.dp)
    ) {
        Text(text = "Nome:")
        if (nome != null) {
            Text(text = nome)
        }
        Text(text = "email:")
        if (email != null) {
            Text(text = email)
        }
        
        Button(onClick = { onMatch()}) {
            Text(text = if(mentor) "Seja meu Mentor" else "Seja meu Aprendiz", fontSize = 10.sp)
        }
    }
}
