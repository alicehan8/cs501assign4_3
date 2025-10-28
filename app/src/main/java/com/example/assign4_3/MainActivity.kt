package com.example.assign4_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assign4_3.ui.theme.Assign4_3Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


data class TempReading(
    val temperature: Float,
    val time: String
)

data class Temperatures(
    val readings: List<TempReading>
)

class MainActivity : ComponentActivity() {
    private val viewModel: TemperatureViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assign4_3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TempScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

class TemperatureViewModel : ViewModel(){
    private val _tempStateFlow = MutableStateFlow(Temperatures(emptyList()))
    val tempStateFlow: StateFlow<Temperatures> = _tempStateFlow.asStateFlow()
    var generate by mutableStateOf(true)

    fun addTempReading(tempReading: TempReading){
        val currentReadings = _tempStateFlow.value.readings.toMutableList()
        currentReadings.add(tempReading)
        //restrict list to 20 items
        if(currentReadings.size > 20){
            currentReadings.removeAt(0)
        }
        _tempStateFlow.value = Temperatures(currentReadings)
    }
}

@Composable
fun TempScreen(viewModel: TemperatureViewModel, modifier: Modifier = Modifier){
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.generate) {
        coroutineScope.launch() {
            getReading(viewModel)
        }
    }

    val tempState by viewModel.tempStateFlow.collectAsStateWithLifecycle()

    LazyColumn {
        items(tempState.readings.size) { index ->
            Text(text = "Temperature: ${tempState.readings[index].temperature} at ${tempState.readings[index].time}")
        }
    }

}

suspend fun getReading(viewModel: TemperatureViewModel){
    while(viewModel.generate) {
        delay(2000)
        val reading = Random.nextFloat() * 20 + 65
        val timestampMillis = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat(
            "HH:mm:ss.SSS",
            Locale.getDefault()
        )
        val formattedTimestamp = simpleDateFormat.format(Date(timestampMillis))
        viewModel.addTempReading(TempReading(reading, formattedTimestamp))
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Assign4_3Theme {
        Greeting("Android")
    }
}