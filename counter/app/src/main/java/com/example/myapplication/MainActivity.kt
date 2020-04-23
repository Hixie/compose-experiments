package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Column {
                    Greeting("Alice")
                    Greeting("Bob")
                    Greeting("Charley")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    var x = state { 0 }
    Card {
        Button(onClick = { x.value += 1 }) {
            Text(text = "Hello $name ${x.value}!")
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}