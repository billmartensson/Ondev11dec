package se.magictechnology.ondev11dec

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import se.magictechnology.ondev11dec.ui.theme.Ondev11decTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ondev11decTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatView()
                }
            }
        }
    }
}

fun getSuggestions(messages : List<Message>, suggestCallback: (result: List<String>?) -> Unit) {
    val smartReply = SmartReply.getClient()

    val chatHistory = mutableListOf<TextMessage>()

    for (mess in messages) {
        if(mess.isLocalUser) {
            chatHistory.add(TextMessage.createForLocalUser(mess.text, System.currentTimeMillis()))
        } else {
            chatHistory.add(TextMessage.createForRemoteUser(mess.text, System.currentTimeMillis(), "remote"))
        }
    }



    smartReply.suggestReplies(chatHistory).continueWith { task ->
        val result = task.result
        when (result.status) {
            SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE ->
                // Not supported
                suggestCallback(null)
            SmartReplySuggestionResult.STATUS_NO_REPLY ->
                // No reply
                suggestCallback(null)
            SmartReplySuggestionResult.STATUS_SUCCESS -> {
            }

            else -> {
                // Do nothing.
            }
        }
        var stexts = mutableListOf<String>()
        for(suggestion in result.suggestions) {
            stexts.add(suggestion.text)
        }
        suggestCallback(stexts)

    }

}

@Composable
fun ChatView() {

    var remoteText by remember { mutableStateOf("") }
    var localText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>()}
    var suggestions = remember { mutableStateListOf<String>()}


    LaunchedEffect(key1 = false) {
        //messages.add(Message("Hello", true, System.currentTimeMillis()))
        //messages.add(Message("How are you", false, System.currentTimeMillis()))


    }

    Column {
        Text("Remote")
        TextField(
            value = remoteText,
            onValueChange = { remoteText = it },
            label = { Text("Remote text") }
        )

        Button(onClick = {
            messages.add(Message(remoteText, false, System.currentTimeMillis()))
            getSuggestions(messages) {
                if(it == null) {
                    suggestions.clear()
                } else {
                    suggestions.clear()
                    suggestions.add(it[0])
                    suggestions.add(it[1])
                    suggestions.add(it[2])
                }
            }
        }) {
            Text("Send")
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
            items(messages) { message ->
                Row {
                    if(message.isLocalUser) {
                        Spacer(Modifier.weight(1f))
                    }
                    Text(message.text)
                }
            }
        }


        Text("Local")
        TextField(
            value = localText,
            onValueChange = { localText = it },
            label = { Text("Local text") }
        )

        Button(onClick = {
            messages.add(Message(localText, true, System.currentTimeMillis()))
            getSuggestions(messages) {
                if(it == null) {
                    suggestions.clear()
                } else {
                    suggestions.clear()
                    suggestions.add(it[0])
                    suggestions.add(it[1])
                    suggestions.add(it[2])
                }
            }
        }) {
            Text("Send")
        }

        if(suggestions.size > 0) {
            Column {
                Button(onClick = {
                    messages.add(Message(suggestions[0], true, System.currentTimeMillis()))
                }) {
                    Text(suggestions[0])
                }
                Button(onClick = {
                    messages.add(Message(suggestions[1], true, System.currentTimeMillis()))
                }) {
                    Text(suggestions[1])
                }
                Button(onClick = {
                    messages.add(Message(suggestions[2], true, System.currentTimeMillis()))
                }) {
                    Text(suggestions[2])
                }
            }
        }


    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Ondev11decTheme {
        ChatView()
    }
}


class Message(val text: String, val isLocalUser: Boolean, val timestamp: Long) {

}