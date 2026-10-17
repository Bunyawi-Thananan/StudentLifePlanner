package com.studentlifeplanner.ui.screen

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentlifeplanner.data.model.Assignment
import com.studentlifeplanner.data.model.Course
import com.studentlifeplanner.ui.viewmodel.AssignmentViewModel
import com.studentlifeplanner.ui.viewmodel.CourseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentsScreen(
    onNavigateBack: () -> Unit,
    assignmentViewModel: AssignmentViewModel = viewModel(),
    courseViewModel: CourseViewModel = viewModel()
) {
    val assignments by assignmentViewModel.assignments.collectAsState(initial = emptyList())
    val courses by courseViewModel.courses.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editingAssignment by remember { mutableStateOf<Assignment?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Assignment?>(null) }
    var filterCompleted by remember { mutableStateOf(false) }

    val filteredAssignments = if (filterCompleted) {
        assignments.filter { !it.isCompleted }
    } else {
        assignments
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignments") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { filterCompleted = !filterCompleted }) {
                        Icon(
                            if (filterCompleted) Icons.Default.FilterAlt else Icons.Default.FilterAltOff,
                            "Filter"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingAssignment = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, "Add Assignment")
            }
        }
    ) { paddingValues ->
        if (filteredAssignments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Assignment,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        if (filterCompleted) "No pending assignments" else "No assignments yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(filteredAssignments) { assignment ->
                    val course = courses.find { it.id == assignment.courseId }
                    AssignmentCard(
                        assignment = assignment,
                        courseName = course?.courseName ?: "Unknown Course",
                        onEdit = {
                            editingAssignment = assignment
                            showDialog = true
                        },
                        onDelete = { showDeleteDialog = assignment },
                        onToggleComplete = {
                            assignmentViewModel.updateAssignment(
                                assignment.copy(
                                    isCompleted = !assignment.isCompleted,
                                    completedAt = if (!assignment.isCompleted) System.currentTimeMillis() else null
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AssignmentDialog(
            assignment = editingAssignment,
            courses = courses,
            onDismiss = { showDialog = false },
            onSave = { assignment ->
                if (editingAssignment == null) {
                    assignmentViewModel.addAssignment(assignment)
                } else {
                    assignmentViewModel.updateAssignment(assignment)
                }
                showDialog = false
            }
        )
    }

    showDeleteDialog?.let { assignment ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Assignment") },
            text = { Text("Are you sure you want to delete ${assignment.title}?") },
            confirmButton = {
                TextButton(onClick = {
                    assignmentViewModel.deleteAssignment(assignment)
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
fun AssignmentCard(
    assignment: Assignment,
    courseName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (assignment.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = assignment.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (assignment.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = courseName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Due: ${assignment.dueDate} ${assignment.dueTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (assignment.description.isNotEmpty()) {
                    Text(
                        text = assignment.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                // Priority Badge
                val priorityText = when (assignment.priority) {
                    1 -> "Low"
                    2 -> "Medium"
                    3 -> "High"
                    4 -> "Urgent"
                    else -> "Medium"
                }
                Text(
                    text = "Priority: $priorityText",
                    style = MaterialTheme.typography.labelSmall,
                    color = when (assignment.priority) {
                        1 -> MaterialTheme.colorScheme.primary
                        2 -> MaterialTheme.colorScheme.secondary
                        3 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }
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
fun AssignmentDialog(
    assignment: Assignment?,
    courses: List<Course>,
    onDismiss: () -> Unit,
    onSave: (Assignment) -> Unit
) {
    var title by remember { mutableStateOf(assignment?.title ?: "") }
    var description by remember { mutableStateOf(assignment?.description ?: "") }
    var selectedCourse by remember { mutableStateOf(assignment?.courseId ?: courses.firstOrNull()?.id ?: 0) }
    var dueDate by remember { mutableStateOf(assignment?.dueDate ?: "") }
    var dueTime by remember { mutableStateOf(assignment?.dueTime ?: "23:59") }
    var priority by remember { mutableStateOf(assignment?.priority ?: 2) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (assignment == null) "Add Assignment" else "Edit Assignment") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                // Course Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = courses.find { it.id == selectedCourse }?.courseName ?: "Select Course",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Course") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.courseName) },
                                onClick = {
                                    selectedCourse = course.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("2024-12-31") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = dueTime,
                    onValueChange = { dueTime = it },
                    label = { Text("Due Time (HH:MM)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("23:59") },
                    singleLine = true
                )

                Text("Priority", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1 to "Low", 2 to "Medium", 3 to "High", 4 to "Urgent").forEach { (value, label) ->
                        FilterChip(
                            selected = priority == value,
                            onClick = { priority = value },
                            label = { Text(label) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotEmpty() && selectedCourse != 0) {
                        val newAssignment = Assignment(
                            id = assignment?.id ?: 0,
                            userId = assignment?.userId ?: 0,
                            courseId = selectedCourse,
                            title = title,
                            description = description,
                            dueDate = dueDate,
                            dueTime = dueTime,
                            priority = priority,
                            isCompleted = assignment?.isCompleted ?: false,
                            completedAt = assignment?.completedAt
                        )
                        onSave(newAssignment)
                    }
                },
                enabled = title.isNotEmpty() && selectedCourse != 0
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