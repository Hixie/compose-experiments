package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var connection: CommsConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Column {
                    Greeting("Alice", connection)
                    Greeting("Bob", connection)
                    Greeting("Charley", connection)
                }
            }
        }

        connection = CommsConnection();
        connection!!.connect()
    }
}

private enum class CommsStatus { idle, connecting, connected, disconnecting }

private class CommsConnection : WebSocketListener() {
    var socket: WebSocket? = null
    var status: CommsStatus = CommsStatus.idle

    fun connect() {
        if (status == CommsStatus.idle) {
            OkHttpClient.Builder().build().newWebSocket(
                Request.Builder().url("ws://treeplate.damowmow.com:8001").build(),
                this
            )
            status = CommsStatus.connecting
        }
    }

    fun disconnect() {
        if (status == CommsStatus.connected) {
            socket!!.close(NORMAL_CLOSURE_STATUS, null)
            socket = null
            status = CommsStatus.disconnecting
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        socket = webSocket
        status = CommsStatus.connected
    }

    fun send(message: String) {
        if (status == CommsStatus.connected) {
            socket!!.send(message);
        }
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        output("Websocket received: \"" + text!! + "\"")
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        output("Websocket received: 0x" + bytes!!.hex())
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        output("WebSocket closing: $code $reason")
        disconnect()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String?) {
        output("WebSocket closed: $code $reason")
        status = CommsStatus.idle
        socket = null
        connect()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable?, response: Response?) {
        output("WebSocket error: " + t?.message)
        status = CommsStatus.idle
        socket = null
        connect()
    }

    companion object {
        private val NORMAL_CLOSURE_STATUS = 1000
    }

    private fun output(txt: String) {
        Log.v("WSS", txt)
    }
}

@Composable
private fun Greeting(name: String, connection: CommsConnection) {
    var x = state { 0 }
    Card {
        Button(onClick = {
            x.value += 1
            connection.send(name)
        }) {
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