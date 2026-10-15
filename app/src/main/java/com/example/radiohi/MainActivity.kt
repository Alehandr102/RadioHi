package com.example.radiohi

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

val defaultStations = listOf(
    Station("Европа Плюс", "Поп / Хиты", "http://ep256.hostingradio.ru:8052/europaplus256.mp3"),
    Station("Русское Радио", "Русский Поп", "https://rusradio.hostingradio.ru/rusradio128.mp3"),
    Station("Record Russian Hits", "Русские хиты", "https://radiorecord.hostingradio.ru/russianhits64.aacp"),
    Station("Record Big Hits", "Мировые хиты", "https://radiorecord.hostingradio.ru/bighits96.aacp"),
    Station("Record Gold", "Золотые хиты", "https://radiorecord.hostingradio.ru/gold96.aacp"),
    Station("Record Rock", "Рок", "https://radiorecord.hostingradio.ru/rock96.aacp"),
    Station("Наше Радио", "Русский Рок", "http://nashe1.hostingradio.ru/nashe-128.mp3"),
    Station("Record Hardstyle", "Hardstyle", "https://radiorecord.hostingradio.ru/teo96.aacp"),
    Station("Record 80-х", "80-е", "https://radiorecord.hostingradio.ru/198096.aacp"),
    Station("Record 90-х", "Супердискотека 90-х", "https://radiorecord.hostingradio.ru/sd9096.aacp"),
    Station("Record 00-х", "2000-е", "https://radiorecord.hostingradio.ru/200096.aacp"),
    Station("Ретро Хит", "Ретро", "http://air.volna.top/Retro"),
    Station("Record Руки Вверх", "Руки Вверх!", "https://radiorecord.hostingradio.ru/rv96.aacp"),
    Station("Record Нафталин FM", "Старые хиты", "https://radiorecord.hostingradio.ru/naft96.aacp"),
    Station("Record", "Dance / Electronic", "https://radiorecord.hostingradio.ru/rr_main96.aacp"),
    Station("Record EDM", "EDM", "https://radiorecord.hostingradio.ru/club96.aacp"),
    Station("Record Eurodance", "Eurodance", "https://radiorecord.hostingradio.ru/eurodance96.aacp"),
    Station("Record House Hits", "House", "https://radiorecord.hostingradio.ru/househits96.aacp"),
    Station("Record Bass House", "Bass House", "https://radiorecord.hostingradio.ru/jackin96.aacp"),
    Station("Record Future House", "Future House", "https://radiorecord.hostingradio.ru/fut96.aacp"),
    Station("Record Tech House", "Tech House", "https://radiorecord.hostingradio.ru/techouse96.aacp"),
    Station("Record Deep", "Deep House", "https://radiorecord.hostingradio.ru/deep96.aacp"),
    Station("Record Tropical", "Tropical House", "https://radiorecord.hostingradio.ru/trop96.aacp"),
    Station("Record Trance", "Trance", "https://radiorecord.hostingradio.ru/tm96.aacp"),
    Station("Record Uplifting", "Uplifting Trance", "https://radiorecord.hostingradio.ru/uplift96.aacp"),
    Station("Record Armin van Buuren", "ASOT", "https://radiorecord.hostingradio.ru/armin96.aacp"),
    Station("Record Tiesto", "Tiesto", "https://radiorecord.hostingradio.ru/tiesto96.aacp"),
    Station("Record David Guetta", "David Guetta", "https://radiorecord.hostingradio.ru/guetta96.aacp"),
    Station("Record Martin Garrix", "Martin Garrix", "https://radiorecord.hostingradio.ru/martingarrix64.aacp"),
    Station("Record Rap Hits", "Rap", "https://radiorecord.hostingradio.ru/rap96.aacp"),
    Station("Record Black Rap", "Black Rap", "https://radiorecord.hostingradio.ru/yo96.aacp"),
    Station("Record Phonk", "Phonk", "https://radiorecord.hostingradio.ru/phonk96.aacp"),
    Station("Record Chill-Out", "Chill-Out", "https://radiorecord.hostingradio.ru/chil96.aacp"),
    Station("Record Lo-Fi", "Lo-Fi", "https://radiorecord.hostingradio.ru/lofi96.aacp"),
    Station("Record Ambient", "Ambient", "https://radiorecord.hostingradio.ru/ambient96.aacp"),
    Station("Record Organic", "Organic House", "https://radiorecord.hostingradio.ru/organic96.aacp"),
    Station("Jazz 89.1", "Джаз", "http://nashe1.hostingradio.ru/jazz-128.mp3"),
    Station("Record Симфония FM", "Классика", "https://radiorecord.hostingradio.ru/symph96.aacp"),
    Station("Record GOA/PSY", "Psy-Trance", "https://radiorecord.hostingradio.ru/goa96.aacp"),
    Station("Record Dubstep", "Dubstep", "https://radiorecord.hostingradio.ru/dub96.aacp"),
    Station("Record Drum&Bass", "D'n'B", "https://radiorecord.hostingradio.ru/drumhits96.aacp"),
    Station("Record Techno", "Techno", "https://radiorecord.hostingradio.ru/techno96.aacp"),
    Station("Record Synthwave", "Synthwave", "https://radiorecord.hostingradio.ru/synth96.aacp"),
    Station("Record Reggae", "Reggae", "https://radiorecord.hostingradio.ru/reggae96.aacp"),
    Station("Record Disco/Funk", "Disco & Funk", "https://radiorecord.hostingradio.ru/discofunk96.aacp"),
    Station("Record Pirate Station", "Drum&Bass", "https://radiorecord.hostingradio.ru/ps96.aacp"),
    Station("Вести FM", "Новости", "http://icecast.vgtrk.cdnvideo.ru/vestifm_mp3_192kbps"),
    Station("Маяк", "Новости / Разговорное", "http://icecast.vgtrk.cdnvideo.ru/mayakfm_mp3_192kbps"),
    Station("Шансон", "Шансон", "http://chanson.hostingradio.ru:8041/chanson256.mp3"),
    Station("Мегаполис FM", "Электронная", "http://109.239.129.43:8010/megapolis-48.aac")
)

val stations = mutableStateListOf<Station>()

fun playStation(player: Player, station: Station, allStations: List<Station>) {
    val mediaItems = allStations.map { st ->
        MediaItem.Builder()
            .setUri(st.url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(st.name)
                    .setArtist(st.genre)
                    .build()
            )
            .build()
    }

    val currentIndex = allStations.indexOf(station)
    player.setMediaItems(mediaItems, currentIndex, 0)
    player.prepare()
    player.playWhenReady = true
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioScreen() {
    val context = LocalContext.current
    val scope = remember { CoroutineScope(Dispatchers.IO) }

    // Загружаем станции при первом запуске
    LaunchedEffect(Unit) {
        if (stations.isEmpty()) {
            val saved = StationStorage.load(context)
            if (saved.isNotEmpty()) {
                stations.addAll(saved)
            } else {
                stations.addAll(defaultStations)
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedStation by remember { mutableStateOf<Station?>(null) }
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
        future.addListener({
            val ctrl = future.get()
            controller = ctrl

            // Синхронизируем состояние при подключении
            val currentMediaItem = ctrl.currentMediaItem
            if (currentMediaItem != null) {
                val title = currentMediaItem.mediaMetadata.title?.toString() ?: ""
                val found = stations.find { it.name == title }
                if (found != null) {
                    selectedStation = found
                    isPlaying = ctrl.playWhenReady
                }
            }
        }, ContextCompat.getMainExecutor(context))
        onDispose { controller?.release() }
    }

    // Слушатель плеера
    DisposableEffect(controller) {
        if (controller != null) {
            val listener = object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    mediaItem?.let { item ->
                        val title = item.mediaMetadata.title?.toString() ?: ""
                        val found = stations.find { it.name == title }
                        if (found != null) {
                            selectedStation = found
                        }
                    }
                }

                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    isPlaying = playWhenReady
                }
            }
            controller!!.addListener(listener)
            onDispose {
                controller!!.removeListener(listener)
            }
        } else {
            onDispose { }
        }
    }

    // Выбираем первую станцию когда список загрузился
    LaunchedEffect(stations.size) {
        if (selectedStation == null && stations.isNotEmpty()) {
            selectedStation = stations[0]
        }
    }

    val filteredStations = remember(searchQuery) {
        if (searchQuery.isBlank()) stations
        else stations.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.genre.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text(text = "+", fontSize = 28.sp, color = MaterialTheme.colorScheme.onPrimary)
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
                    },
                    onPreviousClick = {
                        val currentIndex = stations.indexOf(station)
                        if (currentIndex > 0) {
                            val prevStation = stations[currentIndex - 1]
                            selectedStation = prevStation
                            controller?.let { playStation(it, prevStation, stations) }
                            isPlaying = true
                        }
                    },
                    onNextClick = {
                        val currentIndex = stations.indexOf(station)
                        if (currentIndex < stations.size - 1) {
                            val nextStation = stations[currentIndex + 1]
                            selectedStation = nextStation
                            controller?.let { playStation(it, nextStation, stations) }
                            isPlaying = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "RadioHi 📻",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Поиск станции...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true
            )

            val listState = rememberLazyListState()

// Прокрутка к активной станции
            LaunchedEffect(selectedStation, filteredStations) {
                if (selectedStation != null && filteredStations.isNotEmpty()) {
                    val index = filteredStations.indexOf(selectedStation)
                    if (index >= 0) {
                        // Сначала мгновенно переходим, потом плавно
                        listState.scrollToItem(index)
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredStations) { station ->
                    StationCard(
                        station = station,
                        isSelected = station == selectedStation,
                        onClick = {
                            selectedStation = station
                            controller?.let { playStation(it, station, stations) }
                            isPlaying = true
                        },
                        onDelete = {
                            stations.remove(station)
                            if (selectedStation == station) {
                                selectedStation = stations.firstOrNull()
                            }
                            scope.launch {
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
                        text = " Где взять ссылку: radiopotok.ru или официальный сайт станции",
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
                            scope.launch {
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
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Предыдущая",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                .combinedClickable(
                    onClick = onPlayPauseClick,
                    onLongClick = { }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isPlaying) "⏸" else "▶",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        IconButton(onClick = onNextClick) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Следующая",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = station.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
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
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showDeleteConfirm = true }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = station.genre,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }

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