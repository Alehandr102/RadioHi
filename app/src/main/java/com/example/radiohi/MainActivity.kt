package com.example.radiohi

import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.radiohi.ui.theme.RadioHiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RadioHiTheme {
                RadioScreen()
            }
        }
    }
}
@kotlinx.serialization.Serializable
data class Station(
    val name: String,
    val genre: String,
    val url: String
)

// Изменяемый список станций — можно добавлять и удалять
val stations = mutableStateListOf(
    Station("Европа Плюс", "Поп / Танцевальная", "http://ep256.hostingradio.ru:8052/europaplus256.mp3"),
    Station("Шансон", "Шансон", "http://chanson.hostingradio.ru:8041/chanson256.mp3"),
    Station("Вести FM", "Новости / Разговорное", "http://icecast.vgtrk.cdnvideo.ru/vestifm_mp3_192kbps"),
    Station("Маяк", "Новости / Разговорное", "http://icecast.vgtrk.cdnvideo.ru/mayakfm_mp3_192kbps"),
    Station("Русское Радио", "Поп / Хиты", "https://rusradio.hostingradio.ru/rusradio128.mp3"),
    Station("Record", "Танцевальная / Электронная", "https://radiorecord.hostingradio.ru/rr_main96.aacp"),
    Station("Record Rock", "Рок", "https://radiorecord.hostingradio.ru/rock96.aacp"),
    Station("Record Trance", "Транс", "https://radiorecord.hostingradio.ru/tm96.aacp"),
    Station("Record 90-х", "Ретро танцы", "https://radiorecord.hostingradio.ru/sd9096.aacp"),
    Station("Наше Радио", "Русский Рок", "http://nashe1.hostingradio.ru/nashe-128.mp3"),
    Station("Jazz 89.1", "Джаз", "http://nashe1.hostingradio.ru/jazz-128.mp3"),
    Station("Ретро Хит", "Ретро", "http://air.volna.top/Retro"),
    Station("Radio Relax", "Спокойная музыка", "https://radiorecord.hostingradio.ru/chil96.aacp"),
    Station("Deep", "Deep House", "https://radiorecord.hostingradio.ru/deep96.aacp"),
    Station("Chill-Out", "Чилаут", "https://radiorecord.hostingradio.ru/chil96.aacp"),
    Station("Мегаполис FM", "Электронная", "http://109.239.129.43:8010/megapolis-48.aac"),
    Station("Radio Record Gold", "Золотые хиты", "https://radiorecord.hostingradio.ru/gold96.aacp")
)

fun playStation(player: Player, station: Station) {
    val item = MediaItem.Builder()
        .setUri(station.url)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(station.name)
                .setArtist(station.genre)
                .build()
        )
        .build()
    player.setMediaItem(item)
    player.prepare()
    player.playWhenReady = true
}

@Composable
fun RadioScreen() {
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        val saved = StationStorage.load(context)
        if (saved.isNotEmpty()) {
            stations.clear()
            stations.addAll(saved)
        }
    }

    var selectedStation by remember { mutableStateOf<Station?>(stations.firstOrNull()) }
    var isPlaying by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newUrl by remember { mutableStateOf("") }

    var controller by remember { mutableStateOf<MediaController?>(null) }

    DisposableEffect(context) {
        val future = MediaController.Builder(
            context,
            SessionToken(context, ComponentName(context, RadioService::class.java))
        ).buildAsync()
        future.addListener({ controller = future.get() }, ContextCompat.getMainExecutor(context))
        onDispose {
            controller?.release()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Text(text = "+", fontSize = 28.sp)
            }
        },
        bottomBar = {
            selectedStation?.let { station ->
                PlayerPanel(
                    station = station,
                    isPlaying = isPlaying,
                    onPlayPauseClick = {
                        isPlaying = !isPlaying
                        controller?.playWhenReady = isPlaying
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "RadioHi 📻",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(stations) { station ->
                    StationCard(
                        station = station,
                        isSelected = station == selectedStation,
                        onClick = {
                            selectedStation = station
                            controller?.let { playStation(it, station) }
                            isPlaying = true
                        },
                        onDelete = {
                            stations.remove(station)
                            if (selectedStation == station) {
                                selectedStation = stations.firstOrNull()
                            }

                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                StationStorage.save(context, stations)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(text = "Новая станция") },
            text = {
                Column {
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text(text = "Название") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    TextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        label = { Text(text = "Ссылка на поток") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = " Где взять ссылку: зайди на radiopotok.ru или официальный сайт станции. Ищи прямую ссылку, обычно она заканчивается на .mp3 или .aacp",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank() && newUrl.isNotBlank()) {
                            stations.add(Station(newName, "Моя станция", newUrl))

                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                StationStorage.save(context, stations)
                            }
                            newName = ""
                            newUrl = ""
                            showAddDialog = false
                        }
                    }
                ) { Text(text = "Добавить") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(text = "Отмена")
                }
            }
        )
    }
}

@Composable
fun PlayerPanel(
    station: Station,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                .clickable { onPlayPauseClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isPlaying) "⏸" else "▶",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = station.name, fontWeight = FontWeight.SemiBold)
            Text(
                text = station.genre,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun StationCard(
    station: Station,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { showDeleteConfirm = true }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = station.name, fontWeight = FontWeight.SemiBold)
            Text(
                text = station.genre,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(text = "Удалить станцию?") },
            text = { Text(text = "Вы уверены, что хотите удалить \"${station.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    }
                ) { Text(text = "Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(text = "Отмена")
                }
            }
        )
    }
}