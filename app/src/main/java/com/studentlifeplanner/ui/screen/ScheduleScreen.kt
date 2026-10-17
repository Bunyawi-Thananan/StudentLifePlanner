package com.studentlifeplanner.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentlifeplanner.data.model.Schedule
import com.studentlifeplanner.data.model.Course
import com.studentlifeplanner.ui.viewmodel.ScheduleViewModel
import com.studentlifeplanner.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onNavigateBack: () -> Unit,
    scheduleViewModel: ScheduleViewModel = viewModel(),
    courseViewModel: CourseViewModel = viewModel()
) {
    val schedules by scheduleViewModel.schedules.collectAsState(initial = emptyList())
    val courses by courseViewModel.courses.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<Schedule?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Schedule?>(null) }
    var selectedDay by remember { mutableStateOf(1) } // 1 = Monday

    val daysOfWeek = listOf(
        1 to "Mon", 2 to "Tue", 3 to "Wed",
        4 to "Thu", 5 to "Fri", 6 to "Sat", 7 to "Sun"
    )

    val filteredSchedules = schedules.filter { it.dayOfWeek == selectedDay }
        .sortedBy { it.startTime }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingSchedule = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, "Add Schedule")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Day Selector
            ScrollableTabRow(
                selectedTabIndex = selectedDay - 1,
                modifier = Modifier.fillMaxWidth()
            ) {
                daysOfWeek.forEach { (day, label) ->
                    Tab(
                        selected = selectedDay == day,
                        onClick = { selectedDay = day },
                        text = { Text(label) }
                    )
                }
            }

            if (filteredSchedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No classes on this day",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(filteredSchedules) { schedule ->
                        val course = courses.find { it.id == schedule.courseId }
                        ScheduleCard(
                            schedule = schedule,
                            course = course,
                            onEdit = {
                                editingSchedule = schedule
                                showDialog = true
                            },
                            onDelete = { showDeleteDialog = schedule }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        ScheduleDialog(
            schedule = editingSchedule,
            courses = courses,
            selectedDay = selectedDay,
            onDismiss = { showDialog = false },
            onSave = { schedule ->
                if (editingSchedule == null) {
                    scheduleViewModel.addSchedule(schedule)
                } else {
                    scheduleViewModel.updateSchedule(schedule)
                }
                showDialog = false
            }
        )
    }

    showDeleteDialog?.let { schedule ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Schedule") },
            text = { Text("Are you sure you want to delete this class?") },
            confirmButton = {
                TextButton(onClick = {
                    scheduleViewModel.deleteSchedule(schedule)
                    showDeleteDialog = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ScheduleCard(
    schedule: Schedule,
    course: Course?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (course != null)
                Color(android.graphics.Color.parseColor(course.color)).copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time indicator
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = schedule.startTime,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = schedule.endTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Vertical divider
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(
                        if (course != null)
                            Color(android.graphics.Color.parseColor(course.color))
                        else MaterialTheme.colorScheme.primary
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course?.courseName ?: "Unknown Course",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = course?.courseCode ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (schedule.room.isNotEmpty() || schedule.building.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = buildString {
                                if (schedule.room.isNotEmpty()) append(schedule.room)
                                if (schedule.building.isNotEmpty()) {
                                    if (schedule.room.isNotEmpty()) append(", ")
                                    append(schedule.building)
                                }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = schedule.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDialog(
    schedule: Schedule?,
    courses: List<Course>,
    selectedDay: Int,
    onDismiss: () -> Unit,
    onSave: (Schedule) -> Unit
) {
    var selectedCourse by remember { mutableStateOf(schedule?.courseId ?: courses.firstOrNull()?.id ?: 0) }
    var dayOfWeek by remember { mutableStateOf(schedule?.dayOfWeek ?: selectedDay) }
    var startTime by remember { mutableStateOf(schedule?.startTime ?: "") }
    var endTime by remember { mutableStateOf(schedule?.endTime ?: "") }
    var room by remember { mutableStateOf(schedule?.room ?: "") }
    var building by remember { mutableStateOf(schedule?.building ?: "") }
    var type by remember { mutableStateOf(schedule?.type ?: "Lecture") }
    var courseExpanded by remember { mutableStateOf(false) }
    var dayExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val daysOfWeek = listOf(
        1 to "Monday", 2 to "Tuesday", 3 to "Wednesday",
        4 to "Thursday", 5 to "Friday", 6 to "Saturday", 7 to "Sunday"
    )
    val types = listOf("Lecture", "Lab", "Tutorial", "Seminar", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (schedule == null) "Add Schedule" else "Edit Schedule") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Course Dropdown
                ExposedDropdownMenuBox(
                    expanded = courseExpanded,
                    onExpandedChange = { courseExpanded = !courseExpanded }
                ) {
                    OutlinedTextField(
                        value = courses.find { it.id == selectedCourse }?.courseName ?: "Select Course",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Course") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = courseExpanded,
                        onDismissRequest = { courseExpanded = false }
                    ) {
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.courseName) },
                                onClick = {
                                    selectedCourse = course.id
                                    courseExpanded = false
                                }
                            )
                        }
                    }
                }

                // Day Dropdown
                ExposedDropdownMenuBox(
                    expanded = dayExpanded,
                    onExpandedChange = { dayExpanded = !dayExpanded }
                ) {
                    OutlinedTextField(
                        value = daysOfWeek.find { it.first == dayOfWeek }?.second ?: "Select Day",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Day of Week") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dayExpanded,
                        onDismissRequest = { dayExpanded = false }
                    ) {
                        daysOfWeek.forEach { (day, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    dayOfWeek = day
                                    dayExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("09:00") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("10:30") },
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = building,
                    onValueChange = { building = it },
                    label = { Text("Building (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        types.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    type = t
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedCourse != 0 && startTime.isNotEmpty() && endTime.isNotEmpty()) {
                        val newSchedule = Schedule(
                            id = schedule?.id ?: 0,
                            userId = schedule?.userId ?: 0,
                            courseId = selectedCourse,
                            dayOfWeek = dayOfWeek,
                            startTime = startTime,
                            endTime = endTime,
                            room = room,
                            building = building,
                            type = type
                        )
                        onSave(newSchedule)
                    }
                },
                enabled = selectedCourse != 0 && startTime.isNotEmpty() && endTime.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}