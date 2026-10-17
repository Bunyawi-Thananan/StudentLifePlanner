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
import com.studentlifeplanner.data.model.Exam
import com.studentlifeplanner.data.model.Course
import com.studentlifeplanner.ui.viewmodel.ExamViewModel
import com.studentlifeplanner.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen(
    onNavigateBack: () -> Unit,
    examViewModel: ExamViewModel = viewModel(),
    courseViewModel: CourseViewModel = viewModel()
) {
    val exams by examViewModel.exams.collectAsState(initial = emptyList())
    val courses by courseViewModel.courses.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var editingExam by remember { mutableStateOf<Exam?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Exam?>(null) }
    var filterUpcoming by remember { mutableStateOf(false) }

    val filteredExams = if (filterUpcoming) {
        exams.filter { !it.isCompleted }
    } else {
        exams
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exams") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { filterUpcoming = !filterUpcoming }) {
                        Icon(
                            if (filterUpcoming) Icons.Default.FilterAlt else Icons.Default.FilterAltOff,
                            "Filter"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingExam = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, "Add Exam")
            }
        }
    ) { paddingValues ->
        if (filteredExams.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        if (filterUpcoming) "No upcoming exams" else "No exams yet",
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
                items(filteredExams) { exam ->
                    val course = courses.find { it.id == exam.courseId }
                    ExamCard(
                        exam = exam,
                        courseName = course?.courseName ?: "Unknown Course",
                        onEdit = {
                            editingExam = exam
                            showDialog = true
                        },
                        onDelete = { showDeleteDialog = exam },
                        onToggleComplete = {
                            examViewModel.updateExam(exam.copy(isCompleted = !exam.isCompleted))
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        ExamDialog(
            exam = editingExam,
            courses = courses,
            onDismiss = { showDialog = false },
            onSave = { exam ->
                if (editingExam == null) {
                    examViewModel.addExam(exam)
                } else {
                    examViewModel.updateExam(exam)
                }
                showDialog = false
            }
        )
    }

    showDeleteDialog?.let { exam ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Exam") },
            text = { Text("Are you sure you want to delete ${exam.title}?") },
            confirmButton = {
                TextButton(onClick = {
                    examViewModel.deleteExam(exam)
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
fun ExamCard(
    exam: Exam,
    courseName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (exam.isCompleted)
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
                checked = exam.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exam.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (exam.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = courseName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${exam.examDate} ${exam.startTime}-${exam.endTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (exam.room.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${exam.room}${if (exam.building.isNotEmpty()) ", ${exam.building}" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "${exam.examType} • ${exam.totalMarks} marks",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                exam.obtainedMarks?.let {
                    Text(
                        text = "Score: $it/${exam.totalMarks}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
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
fun ExamDialog(
    exam: Exam?,
    courses: List<Course>,
    onDismiss: () -> Unit,
    onSave: (Exam) -> Unit
) {
    var title by remember { mutableStateOf(exam?.title ?: "") }
    var selectedCourse by remember { mutableStateOf(exam?.courseId ?: courses.firstOrNull()?.id ?: 0) }
    var examDate by remember { mutableStateOf(exam?.examDate ?: "") }
    var startTime by remember { mutableStateOf(exam?.startTime ?: "") }
    var endTime by remember { mutableStateOf(exam?.endTime ?: "") }
    var room by remember { mutableStateOf(exam?.room ?: "") }
    var building by remember { mutableStateOf(exam?.building ?: "") }
    var examType by remember { mutableStateOf(exam?.examType ?: "Midterm") }
    var totalMarks by remember { mutableStateOf(exam?.totalMarks?.toString() ?: "100") }
    var obtainedMarks by remember { mutableStateOf(exam?.obtainedMarks?.toString() ?: "") }
    var notes by remember { mutableStateOf(exam?.notes ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val examTypes = listOf("Midterm", "Final", "Quiz", "Practical", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (exam == null) "Add Exam" else "Edit Exam") },
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

                // Exam Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = examType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Exam Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        examTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    examType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = examDate,
                    onValueChange = { examDate = it },
                    label = { Text("Exam Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("2024-12-31") },
                    singleLine = true
                )

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
                        placeholder = { Text("12:00") },
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = totalMarks,
                        onValueChange = { totalMarks = it },
                        label = { Text("Total Marks") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = obtainedMarks,
                        onValueChange = { obtainedMarks = it },
                        label = { Text("Score (Optional)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotEmpty() && selectedCourse != 0) {
                        val newExam = Exam(
                            id = exam?.id ?: 0,
                            userId = exam?.userId ?: 0,
                            courseId = selectedCourse,
                            title = title,
                            examDate = examDate,
                            startTime = startTime,
                            endTime = endTime,
                            room = room,
                            building = building,
                            examType = examType,
                            totalMarks = totalMarks.toIntOrNull() ?: 100,
                            notes = notes,
                            isCompleted = exam?.isCompleted ?: false,
                            obtainedMarks = obtainedMarks.toIntOrNull()
                        )
                        onSave(newExam)
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