package com.example.financialapp.ui.settings

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.financialapp.R
import com.example.financialapp.ui.theme.ThemeMode

/**
 * Settings screen:
 * - Appearance: accent header filled with theme primary (SectionAccent)
 * - Profile photo
 * - Primary color & CSV
 * - Background picker panel (scrollable)
 */
@Composable
fun SettingsScreen(
    currentMode: ThemeMode,
    currentPrimary: Long,
    onChangeMode: (ThemeMode) -> Unit,
    onChangeColor: (Long) -> Unit,
    onExportCsv: () -> Unit,
    onAddMfa: () -> Unit,          // ⬅️ new callback
    bgVm: BackgroundFixedViewModel
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val scroll = rememberScrollState()
    val swatches = listOf(0xFF6750A4, 0xFF1E88E5, 0xFF43A047, 0xFFFB8C00, 0xFFE53935, 0xFF00897B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars) // avoid status bar overlap
            .verticalScroll(scroll)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Appearance: only this section uses the bold primary-filled header ---
        SectionAccent(title = "Appearance") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Radio("System", currentMode == ThemeMode.SYSTEM) { onChangeMode(ThemeMode.SYSTEM) }
                Radio("Light",  currentMode == ThemeMode.LIGHT)  { onChangeMode(ThemeMode.LIGHT) }
                Radio("Dark",   currentMode == ThemeMode.DARK)   { onChangeMode(ThemeMode.DARK) }
            }
        }

        // --- Profile photo ---
        ProfilePhotoSection()

        // --- Primary color swatches (kept with normal SectionBlock style) ---
        SectionBlock(title = "Primary color") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                swatches.forEach { argb ->
                    Box(
                        Modifier
                            .size(if (argb == currentPrimary) 36.dp else 28.dp)
                            .clip(CircleShape)
                            .background(Color(argb))
                            .clickable { onChangeColor(argb) }
                    )
                }
            }
            Button(onClick = {}) { Text("Primary sample") }
        }

        Button(onClick = onExportCsv) { Text("Export Transactions (CSV)") }

        // --- Background section (unchanged layout; panel is scrollable) ---
        SectionBlock(title = "Background") {
            val selected by bgVm.selected.collectAsState()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showPicker = true }) { Text("Change Background Image") }
                OutlinedButton(onClick = { bgVm.reset() }, enabled = (selected != null)) {
                    Text("Reset to Default")
                }
            }

            if (showPicker) {
                Spacer(Modifier.height(8.dp))
                BackgroundPickerPanel(
                    vm = bgVm,
                    onApply = { showPicker = false },
                    onCancel = { showPicker = false }
                )
            }
        }

        Divider()

        // --- Security ---
        Text("Security", style = MaterialTheme.typography.titleLarge)
        Text(
            "Protect your account by adding 2-Step Verification.",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = onAddMfa) {
            Text("Add MFA")
        }
    }
}

/** Small radio item (label + radio button) */
@Composable
private fun Radio(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label)
    }
}

/** Profile photo section (gallery picker -> ProfileViewModel) */
@Composable
fun ProfilePhotoSection(context: Context = LocalContext.current) {
    val viewModel = remember { ProfileViewModel(context) }
    val imageUri by viewModel.imageUri.collectAsState()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) viewModel.onImageSelected(uri) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUri ?: R.mipmap.ic_launcher,
            contentDescription = "Profile photo",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        ) { Text("Change Profile Photo") }
    }
}

/** Background picker panel (2x3 grid, internal scroll, height cap) */
@Composable
fun BackgroundPickerPanel(
    vm: BackgroundFixedViewModel,
    onApply: () -> Unit,
    onCancel: () -> Unit
) {
    val temp by vm.tempChoice.collectAsState()
    val options = vm.options
    val innerScroll = rememberScrollState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 420.dp) // cap to keep screen scrollable
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier
                .verticalScroll(innerScroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Choose Background", style = MaterialTheme.typography.titleMedium)

            val rows = options.chunked(3)
            rows.forEach { row ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { resId ->
                        val isSel = (temp == resId)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { vm.chooseTemp(resId) },
                            border = if (isSel)
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            else
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    if (row.size < 3) repeat(3 - row.size) {
                        Spacer(Modifier.weight(1f).height(110.dp))
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(
                    onClick = { vm.applyTemp(); onApply() },
                    enabled = temp != null,
                    modifier = Modifier.weight(1f)
                ) { Text("Apply") }
            }
        }
    }
}

/* ----------------------------- Common UI Blocks ----------------------------- */

/** Section with LEFT highlight bar (kept for non-Appearance sections) */
@Composable
fun SectionBlock(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val primary by rememberUpdatedState(MaterialTheme.colorScheme.primary)
    val bg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = bg,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            Modifier
                .padding(12.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(primary)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                content()
            }
        }
    }
}

/** Section with bold primary-filled header (used ONLY for Appearance) */
@Composable
fun SectionAccent(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val bodyBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.20f)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = bodyBg,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp
    ) {
        Column {
            // Header fully filled with primary color
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp)
                    .background(primary, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = onPrimary)
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                content()
            }
        }
    }
}
