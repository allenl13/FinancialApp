package com.example.financialapp.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.financialapp.util.formatAsDmyOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsListScreen(
    nav: NavController,
    vm: GoalsListViewModel = viewModel()
) {
    val items by vm.goals.collectAsState()
    val cats by vm.categories.collectAsState()
    val catMap by vm.categoryMap.collectAsState()
    val sort by vm.sortMode.collectAsState()
    val query by vm.searchQuery.collectAsState()
    val selectedCatId by vm.selectedCategoryId.collectAsState()
    val showCompleted by vm.showCompletedOnly.collectAsState()

    var showAdd by remember { mutableStateOf(false) }

    // Show headers when a category is selected OR when sorting by CATEGORY
    val groupByCategory = (selectedCatId != null) || (sort == GoalSort.CATEGORY)

    val grouped = remember(items, catMap) {
        val byCat = items.groupBy { it.categoryId }
        val orderedKeys = byCat.keys.sortedWith(compareBy(nullsLast()) { key ->
            key?.let { catMap[it]?.name ?: "" } ?: "~~~"
        })
        orderedKeys.map { key -> key to byCat[key].orEmpty() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saving Goals") },
                actions = {
                    var menuOpen by remember { mutableStateOf(false) }

                    TextButton(onClick = { menuOpen = true }) {
                        Text(
                            when (sort) {
                                GoalSort.DUE_DATE -> "Sort: Due Date"
                                GoalSort.REMAINING -> "Sort: Remaining"
                                GoalSort.CATEGORY -> "Sort: Category"
                            }
                        )
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("Sort by Due Date") },
                            onClick = { vm.setSort(GoalSort.DUE_DATE); menuOpen = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Remaining") },
                            onClick = { vm.setSort(GoalSort.REMAINING); menuOpen = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Category") },
                            onClick = { vm.setSort(GoalSort.CATEGORY); menuOpen = false }
                        )
                    }
                }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = { showAdd = true }) { Text("+") } }
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            // --- Search ---
            OutlinedTextField(
                value = query,
                onValueChange = vm::setSearch,
                label = { Text("Search goals") },
                singleLine = true,
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = vm::clearSearch) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // --- Category filter (All + categories) ---
            var catExpanded by remember { mutableStateOf(false) }
            val allOption = CategoryMiniUi(-1L, "All", "#9E9E9E")
            val currentLabel = when (val id = selectedCatId) {
                null -> allOption.name
                else -> cats.firstOrNull { it.id == id }?.name ?: allOption.name
            }

            ExposedDropdownMenuBox(
                expanded = catExpanded,
                onExpandedChange = { catExpanded = !catExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    readOnly = true,
                    value = currentLabel,
                    onValueChange = {},
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = catExpanded,
                    onDismissRequest = { catExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(allOption.name) },
                        onClick = { vm.setCategoryFilter(null); catExpanded = false }
                    )
                    cats.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.name) },
                            onClick = { vm.setCategoryFilter(c.id); catExpanded = false }
                        )
                    }
                }
            }

            // --- Show Completed toggle ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Completed", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.weight(1f))
                Switch(checked = showCompleted, onCheckedChange = vm::setShowCompletedOnly)
            }

            if (items.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val emptyMsg =
                        if (showCompleted && query.isBlank() && selectedCatId == null)
                            "No completed goals yet."
                        else if (query.isNotBlank() && selectedCatId != null)
                            "No results for “$query” in this category."
                        else if (query.isNotBlank())
                            "No results for “$query”."
                        else if (selectedCatId != null)
                            "No goals in this category."
                        else
                            "No goals yet. Tap + to create one."
                    Text(emptyMsg)
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    if (groupByCategory) {
                        grouped.forEach { (catId, list) ->
                            val header = catId?.let { catMap[it]?.name } ?: "Uncategorized"
                            item {
                                CategoryHeader(
                                    name = header,
                                    colorHex = catId?.let { catMap[it]?.colorHex } ?: "#9E9E9E"
                                )
                            }
                            itemsIndexed(list, key = { _, g -> g.id }) { _, g ->
                                GoalRow(item = g, onClick = { nav.navigate("goal/${g.id}") })
                                HorizontalDivider()
                            }
                        }
                    } else {
                        itemsIndexed(items, key = { _, g -> g.id }) { _, g ->
                            GoalRow(item = g, onClick = { nav.navigate("goal/${g.id}") })
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    if (showAdd) AddGoalDialog(
        categories = cats,
        onDismiss = { showAdd = false },
        onConfirm = { name, target, due, categoryId ->
            vm.create(name, target, due, categoryId)
            showAdd = false
        },
        onCreateCategory = { n, hex ->
            vm.createCategory(n, hex)
        }
    )
}

@Composable
private fun CategoryHeader(name: String, colorHex: String) {
    val c = safeColor(colorHex)
    Row(
        Modifier
            .fillMaxWidth()
            .background(c.copy(alpha = 0.10f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(12.dp).background(c))
        Spacer(Modifier.width(8.dp))
        Text(name, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
private fun GoalRow(item: GoalListItemUi, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(item.name, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = { item.progress }, modifier = Modifier.fillMaxWidth().height(8.dp))
        Spacer(Modifier.height(6.dp))
        Text(
            "${"%.2f".format(item.saved)} / ${"%.2f".format(item.target)} saved",
            style = MaterialTheme.typography.bodyMedium
        )
        item.dueDateMillis.formatAsDmyOrNull()?.let { d ->
            Spacer(Modifier.height(4.dp))
            Text("Due: $d", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGoalDialog(
    categories: List<CategoryMiniUi>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, targetText: String, dueDateText: String?, categoryId: Long?) -> Unit,
    onCreateCategory: suspend (name: String, colorHex: String) -> Long?
) {
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var due by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val noneOption = CategoryMiniUi(-1L, "Uncategorized", "#9E9E9E")
    var selected by remember { mutableStateOf(noneOption) }

    var showNewCat by remember { mutableStateOf(false) }

    val canSave = name.isNotBlank() && (target.toDoubleOrNull()?.let { it > 0 } == true)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Saving Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(
                    value = target, onValueChange = { target = it },
                    label = { Text("Target amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = due, onValueChange = { due = it },
                    label = { Text("Due date (DD/MM/YYYY, optional)") }
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth(),
                        readOnly = true,
                        value = selected.name,
                        onValueChange = {},
                        label = { Text("Category (optional)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("➕ New category…") },
                            onClick = {
                                expanded = false
                                showNewCat = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(noneOption.name) },
                            onClick = {
                                selected = noneOption
                                expanded = false
                            }
                        )
                        categories.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c.name) },
                                onClick = {
                                    selected = c
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = {
                    val catId = selected.id.takeIf { it >= 0 }
                    onConfirm(name, target, due.ifBlank { null }, catId)
                }
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )

    if (showNewCat) {
        NewCategoryDialog(
            onDismiss = { showNewCat = false },
            onConfirm = { n, hex ->
                scope.launch {
                    val newId = onCreateCategory(n, hex)
                    if (newId != null) selected = CategoryMiniUi(newId, n.trim(), hex)
                    showNewCat = false
                }
            }
        )
    }
}

@Composable
private fun NewCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val palette = listOf("#FF6D00", "#2962FF", "#00C853", "#AA00FF", "#D50000")
    var selected by remember { mutableStateOf(palette.first()) }

    val canSave = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    palette.forEach { hex ->
                        val size = if (hex == selected) 28.dp else 24.dp
                        Box(
                            modifier = Modifier
                                .size(size)
                                .background(safeColor(hex))
                                .clickable { selected = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = canSave, onClick = { onConfirm(name, selected) }) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun safeColor(hex: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(hex))
}.getOrElse { Color.Gray }
