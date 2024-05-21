package com.example.myapplication.Screens

import android.location.Location
import android.provider.Settings
import android.util.Log
import android.util.Size
import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.Model.UserModel
import com.example.myapplication.Services.MatchServices
import com.example.myapplication.Services.UserServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(OnNavigateToLogin: ()-> Unit, OnNavigateToUpdate: ()-> Unit){
    var uss = remember{
        mutableStateOf(mutableListOf(UserModel()))
    }

    var actualUser = remember{
        mutableStateOf(UserModel())
    }

    var hasUser = remember{
        mutableStateOf(false)
    }

    var distanciaBusca = remember{
        mutableStateOf(UserServices.searchDistance)
    }

    var distanciaString = remember{
        mutableStateOf("")
    }


    LaunchedEffect(true){
        UserServices().getUserList(){ uList ->

            uss.value = uList.toMutableList()

            if(uss.value.size >0) {
                actualUser.value = uss.value[0]
                hasUser.value = true
                Log.d("ENTROU ANTESLISCE", "${uss.value[0]}")
            }

        }
    }




        Column (modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){


            Text(text = "Filtros", modifier = Modifier.align(Alignment.Start))

            Spacer(modifier = Modifier.size(20.dp))
            Text(text = "Distancia de busca")

            Spacer(modifier = Modifier.size(20.dp))

            Slider(
                valueRange = 0f..10000f,
                value = distanciaBusca.value,
                onValueChangeFinished = {
                    UserServices.setDitanciaBusca(distanciaBusca.value)
                },
                onValueChange = {
                    Log.i("Distancia:", "${it}")
                    distanciaBusca.value = it
                }
            )
            distanciaString.value = "%.0f KM".format(distanciaBusca.value)
            Text(text = "${distanciaString.value}")
            Spacer(modifier = Modifier.size(20.dp))

            Button(modifier = Modifier.align(Alignment.CenterHorizontally),onClick = {
                OnNavigateToUpdate()
            }, colors = ButtonColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.secondary)) {
                Text(text = "Atualizar informações", fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }

            Spacer(modifier = Modifier.size(20.dp))

            Button(modifier = Modifier.align(Alignment.CenterHorizontally),onClick = {
                UserServices().logOut().let{
                    if(!UserServices().isUserLogged()){
                        OnNavigateToLogin()
                    }
                }

            }, colors = ButtonColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.secondary)) {
                Text(text = "Logout", fontSize = 20.sp, color = MaterialTheme.colorScheme.onErrorContainer)
            }

        }





}

