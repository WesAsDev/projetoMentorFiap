package com.example.myapplication.Components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.Util.Tags


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Filters(onListChange: (SnapshotStateList<String>)->Unit, tagList: List<String>, isShowUser: Boolean = false,isInUpdateUser: Boolean = false, commonInterest: Set<String> = setOf<String>()){

    var checkedList =
        mutableStateListOf<String>()

    LaunchedEffect("teste2") {
        if (isInUpdateUser) {
            checkedList.addAll(commonInterest)
        }
    }
    Column {


        FilterItem(tagList, checkedList, isShowUser,isInUpdateUser= isInUpdateUser, {
            if(it in checkedList){
                checkedList.remove(it)
            }else{
                checkedList.add(it)
            }

            onListChange(checkedList)
        },  commonInterest)


    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterItem(tags: List<String>, selectedList: SnapshotStateList<String>,isShowUser:Boolean, isInUpdateUser:Boolean, onOptselect:(String)-> Unit, commonInterest: Set<String> = setOf<String>()){
    var theme = MaterialTheme.colorScheme
    FlowRow(
        horizontalArrangement = Arrangement.Start,
        maxItemsInEachRow = 4,
    ) {
        if(isShowUser){
            tags.forEachIndexed(){index, value->
                FilterChip(colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(1f,1f,1f,0.25f)),selected = commonInterest.contains(value),onClick = {
                    onOptselect(value)
                }, label = { Text(text = value) })
            }
        }else if(isInUpdateUser){
            Spacer(modifier = Modifier.size(10.dp))
            tags.forEachIndexed(){index, value->
                FilterChip(selected = value in selectedList, onClick = {
                    onOptselect(value)
                    Log.d("clicado", "${selectedList}")
                }, label = { Text(text = value) })
                Spacer(modifier = Modifier.size(20.dp, 40.dp))


            }
        }

        else{
                Spacer(modifier = Modifier.size(10.dp))
            tags.forEachIndexed(){index, value->
                FilterChip(selected = value in selectedList, onClick = {
                    onOptselect(value)
                    Log.d("clicado", "${selectedList}")
                }, label = { Text(text = value) })
                Spacer(modifier = Modifier.size(20.dp, 40.dp))


            }
        }
    }

}

