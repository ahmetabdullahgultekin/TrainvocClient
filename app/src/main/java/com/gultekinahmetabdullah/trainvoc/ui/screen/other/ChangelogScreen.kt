package com.gultekinahmetabdullah.trainvoc.ui.screen.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gultekinahmetabdullah.trainvoc.data.UpdateNotesManager
import com.gultekinahmetabdullah.trainvoc.model.UpdateHighlight
import com.gultekinahmetabdullah.trainvoc.model.UpdateNotes
import com.gultekinahmetabdullah.trainvoc.model.UpdateType
import com.gultekinahmetabdullah.trainvoc.ui.theme.Spacing

/**
 * ChangelogScreen - Full version history display with search and filtering
 *
 * Features:
 * - Displays all app versions chronologically
 * - Expandable/collapsible version cards
 * - Search functionality to filter versions and updates
 * - Filter by update type (NEW, IMPROVED, FIXED)
 * - Deep linking with version parameter for auto-expand
 * - Material 3 design matching UpdateNotesCard
 *
 * @param navController Navigation controller
 * @param targetVersionCode Optional version code to auto-expand (deep linking)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogScreen(
    navController: NavController,
    targetVersionCode: Int? = null
) {
    val context = LocalContext.current
    val updateNotesManager = remember { UpdateNotesManager.getInstance(context) }

    // Load all versions
    val allVersions = remember { updateNotesManager.getAllVersions() }

    // Search and filter state
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<UpdateType?>(null) }

    // Track expanded states for each version
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    // Auto-expand target version if specified (deep linking)
    LaunchedEffect(targetVersionCode) {
        if (targetVersionCode != null) {
            expandedStates[targetVersionCode] = true
        }
    }

    // Filter versions based on search query and selected filter
    val filteredVersions = remember(allVersions, searchQuery, selectedFilter) {
        allVersions.filter { version ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val query = searchQuery.lowercase()
                version.currentVersion.lowercase().contains(query) ||
                        version.releaseDate.lowercase().contains(query) ||
                        version.highlights.any {
                            it.title.lowercase().contains(query) ||
                                    it.description.lowercase().contains(query)
                        } ||
                        version.upcomingFeatures.any { it.lowercase().contains(query) }
            }

            val matchesFilter = if (selectedFilter == null) {
                true
            } else {
                version.highlights.any { it.type == selectedFilter }
            }

            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text("Changelog") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            item {
                Spacer(modifier = Modifier.height(Spacing.small))
            }

            // Header
            item {
                Column {
                    Text(
                        text = "Version History",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.extraSmall))
                    Text(
                        text = "${allVersions.size} versions • ${allVersions.sumOf { it.highlights.size }} updates",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search versions or updates...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true
                )
            }

            // Filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text("All") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == UpdateType.NEW,
                            onClick = {
                                selectedFilter = if (selectedFilter == UpdateType.NEW) null else UpdateType.NEW
                            },
                            label = { Text("New") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.NewReleases,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == UpdateType.IMPROVED,
                            onClick = {
                                selectedFilter = if (selectedFilter == UpdateType.IMPROVED) null else UpdateType.IMPROVED
                            },
                            label = { Text("Improved") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Upgrade,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == UpdateType.FIXED,
                            onClick = {
                                selectedFilter = if (selectedFilter == UpdateType.FIXED) null else UpdateType.FIXED
                            },
                            label = { Text("Fixed") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.BugReport,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }

            // Results count
            if (searchQuery.isNotEmpty() || selectedFilter != null) {
                item {
                    Text(
                        text = "${filteredVersions.size} version${if (filteredVersions.size != 1) "s" else ""} found",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Display filtered versions
            items(items = filteredVersions, key = { it.versionCode }) { version ->
                VersionCard(
                    updateNotes = version,
                    isInitiallyExpanded = expandedStates[version.versionCode] == true,
                    onExpandChange = { isExpanded ->
                        expandedStates[version.versionCode] = isExpanded
                    },
                    highlightFilter = selectedFilter
                )
            }

            // Empty state
            if (filteredVersions.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.extraLarge),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        Text(
                            text = "No versions found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Try adjusting your search or filters",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.medium))
            }
        }
    }
}

/**
 * Individual version card with expandable content
 */
@Composable
private fun VersionCard(
    updateNotes: UpdateNotes,
    isInitiallyExpanded: Boolean = false,
    onExpandChange: (Boolean) -> Unit = {},
    highlightFilter: UpdateType? = null
) {
    var isExpanded by remember { mutableStateOf(isInitiallyExpanded) }

    // Update expansion state when initial value changes (for deep linking)
    LaunchedEffect(isInitiallyExpanded) {
        if (isInitiallyExpanded && !isExpanded) {
            isExpanded = true
        }
    }

    // Chevron rotation animation
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "chevronRotation"
    )

    // Filter highlights if filter is active
    val displayedHighlights = if (highlightFilter != null) {
        updateNotes.highlights.filter { it.type == highlightFilter }
    } else {
        updateNotes.highlights
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = {
            isExpanded = !isExpanded
            onExpandChange(isExpanded)
        }
    ) {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Version ${updateNotes.currentVersion}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Released ${updateNotes.releaseDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    if (highlightFilter != null && displayedHighlights.isNotEmpty()) {
                        Text(
                            text = "${displayedHighlights.size} ${highlightFilter.name.lowercase()} update${if (displayedHighlights.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        isExpanded = !isExpanded
                        onExpandChange(isExpanded)
                    }
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }
            }

            // Expanded content
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    Spacer(Modifier.height(12.dp))

                    // Highlights
                    Text(
                        text = "What's New",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))

                    displayedHighlights.forEach { highlight ->
                        VersionHighlightItem(highlight)
                        Spacer(Modifier.height(8.dp))
                    }

                    // Show all highlights count if filtered
                    if (highlightFilter != null && displayedHighlights.size < updateNotes.highlights.size) {
                        Text(
                            text = "${updateNotes.highlights.size - displayedHighlights.size} more update${if (updateNotes.highlights.size - displayedHighlights.size != 1) "s" else ""} not shown",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    // Upcoming features if any
                    if (updateNotes.upcomingFeatures.isNotEmpty()) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Text(
                            text = "Coming Soon",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))

                        updateNotes.upcomingFeatures.forEach { feature ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = feature,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual highlight item for changelog
 */
@Composable
private fun VersionHighlightItem(highlight: UpdateHighlight) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = highlight.getIcon(),
            contentDescription = null,
            tint = when (highlight.type) {
                UpdateType.NEW -> MaterialTheme.colorScheme.tertiary
                UpdateType.IMPROVED -> MaterialTheme.colorScheme.primary
                UpdateType.FIXED -> MaterialTheme.colorScheme.secondary
            },
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val badge = when (highlight.type) {
                    UpdateType.NEW -> "NEW"
                    UpdateType.IMPROVED -> "IMPROVED"
                    UpdateType.FIXED -> "FIXED"
                }
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = when (highlight.type) {
                        UpdateType.NEW -> MaterialTheme.colorScheme.tertiary
                        UpdateType.IMPROVED -> MaterialTheme.colorScheme.primary
                        UpdateType.FIXED -> MaterialTheme.colorScheme.secondary
                    },
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = highlight.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = highlight.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}
