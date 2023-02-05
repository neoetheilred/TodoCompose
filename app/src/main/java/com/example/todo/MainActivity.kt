package com.example.todo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo.ui.theme.ToDoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = TodoApp()
        setContent {
            //ToDoTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                app.App()
            }
            // }
        }
    }
}

enum class AppState {
    ToDoList, Edit
}

class TodoApp {
    private lateinit var state: MutableState<AppState>
    private var editable: TodoTask? = null
    private val storageFileName = "todoAppData.txt"
    //   private lateinit var list: SnapshotStateList<TodoTask>

    @SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
    @Composable
    fun App() {
        state = remember { mutableStateOf(AppState.ToDoList) }
        val context = LocalContext.current
        val list by remember { mutableStateOf(mutableStateListOf<TodoTask>()) }
        //list.add(TodoTask("TEST", "TEST"))
        when (state.value) {
            AppState.ToDoList -> {
                this.ToDoList(list)
            }
            AppState.Edit -> {
                this.EditTask(list, editable)
            }
        }
    }

    @Composable
    private fun ToDoList(list: SnapshotStateList<TodoTask>) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { state.value = AppState.Edit }) {
                    Icon(Icons.Default.Add, "add")
                }
            },
            topBar = {
                TopAppBar {
//                    IconButton(onClick = { /*TODO*/ }) {
//                        Icon(Icons.Default.List, "list")
//                    }
                    Row(modifier = Modifier.fillMaxSize()) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "TODO LIST",
                            fontSize = 30.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                list.forEach { ToDoTile(todoTask = it, list = list) }
            }
        }
    }

    @Composable
    private fun EditTask(list: SnapshotStateList<TodoTask>, editable: TodoTask?) {
        var title by remember { mutableStateOf(TextFieldValue(editable?.title ?: "")) }
        var description by remember { mutableStateOf(TextFieldValue(editable?.description ?: "")) }
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    if (title.text.trim().isEmpty()) {

                    } else {
                        state.value = AppState.ToDoList
                        if (editable != null) {
                            editable.update(title.text.trim(), description.text.trim())
                        } else {
                            list.add(TodoTask(title.text.trim(), description.text.trim()))
                        }
                        this.editable = null
                    }
                    // list.add(TodoTask("H", "B"))
                }) {
                    Icon(Icons.Default.Share, "add")
                }
            },
            topBar = {
                TopAppBar {
                    IconButton(onClick = { state.value = AppState.ToDoList }) {
                        Icon(Icons.Default.ArrowBack, "back")
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize()
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    maxLines = 1,
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                )
                TextField(
                    label = { Text("Description") },
                    value = description, onValueChange = { description = it }, modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            rememberScrollState()
                        )
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ToDoTile(todoTask: TodoTask, list: SnapshotStateList<TodoTask>) {
        var openDialog by remember { mutableStateOf(false) }
        if (openDialog) {
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(10))
                    .background(color = Color(0xfff4f4f4))
                    .padding(4.dp)
                    .combinedClickable(
                        //enabled = true,
                        onClick = { state.value = AppState.Edit; this.editable = todoTask },
                        onLongClick = { openDialog = true }
                    )
                // .clickable { state.value = AppState.Edit; this.editable = todoTask }
            ) {
                Text(
                    text = todoTask.title,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = todoTask.description
                )
            }
            AlertDialog(
                onDismissRequest = { openDialog = false },
                title = { Text("Submit") },
                text = { Text("Are you sure you want to delete task `${todoTask.title}`?") },
                buttons = {
//                    Button(onClick = { list.remove(todoTask) }) {
//                        Text("Ok", fontSize = 22.sp)
//                    }
//
                    Row(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { list.remove(todoTask); openDialog = false }) {
                            Text("Ok", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.fillMaxWidth(0.5f))
                        Button(onClick = { openDialog = false }) {
                            Text("Cancel", fontSize = 22.sp)
                        }
                    }
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(10))
                    .background(color = Color(0xfff4f4f4))
                    .padding(4.dp)
                    .combinedClickable(
                        //enabled = true,
                        onClick = { state.value = AppState.Edit; this.editable = todoTask },
                        onLongClick = { openDialog = true }
                    )
                // .clickable { state.value = AppState.Edit; this.editable = todoTask }
            ) {
                Text(
                    text = todoTask.title,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = todoTask.description
                )
            }
        }
    }

}

class TodoTask(var title: String, var description: String) {
    fun update(title: String, description: String) {
        this.title = title
        this.description = description
    }
}