package com.studentlifeplanner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.studentlifeplanner.data.AppDatabase
import com.studentlifeplanner.data.model.User
import com.studentlifeplanner.data.model.Course
import com.studentlifeplanner.data.model.Assignment
import com.studentlifeplanner.data.model.Exam
import com.studentlifeplanner.data.model.Schedule
import com.studentlifeplanner.data.model.Note
import com.studentlifeplanner.data.repository.UserRepository
import com.studentlifeplanner.data.repository.CourseRepository
import com.studentlifeplanner.data.repository.AssignmentRepository
import com.studentlifeplanner.data.repository.ExamRepository
import com.studentlifeplanner.data.repository.ScheduleRepository
import com.studentlifeplanner.data.repository.NoteRepository
import com.studentlifeplanner.utils.PreferenceManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ==================== AUTH VIEW MODEL ====================

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository
    private val preferenceManager: PreferenceManager

    init {
        val database = AppDatabase.getDatabase(application)
        userRepository = UserRepository(database.userDao())
        preferenceManager = PreferenceManager(application)
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = userRepository.loginUser(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                preferenceManager.setLoggedIn(true)
                preferenceManager.setUserId(user.id)
                preferenceManager.setEmail(user.email)
                preferenceManager.setUsername(user.username)
                preferenceManager.setFullName(user.fullName)
                _loginState.value = LoginState.Success(user)
            } else {
                _loginState.value = LoginState.Error(
                    result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            val result = userRepository.registerUser(user)
            if (result.isSuccess) {
                _registerState.value = RegisterState.Success
            } else {
                _registerState.value = RegisterState.Error(
                    result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

// ==================== COURSE VIEW MODEL ====================

class CourseViewModel(application: Application) : AndroidViewModel(application) {
    private val courseRepository: CourseRepository
    private val preferenceManager: PreferenceManager

    init {
        val database = AppDatabase.getDatabase(application)
        courseRepository = CourseRepository(database.courseDao())
        preferenceManager = PreferenceManager(application)
    }

    val courses: Flow<List<Course>> = flow {
        val userId = preferenceManager.getUserId()
        courseRepository.getAllCourses(userId).collect { emit(it) }
    }

    fun addCourse(course: Course) {
        viewModelScope.launch {
            val userId = preferenceManager.getUserId()
            courseRepository.insertCourse(course.copy(userId = userId))
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            courseRepository.updateCourse(course)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            courseRepository.deleteCourse(course)
        }
    }
}

// ==================== ASSIGNMENT VIEW MODEL ====================

class AssignmentViewModel(application: Application) : AndroidViewModel(application) {
    private val assignmentRepository: AssignmentRepository
    private val preferenceManager: PreferenceManager

    init {
        val database = AppDatabase.getDatabase(application)
        assignmentRepository = AssignmentRepository(database.assignmentDao())
        preferenceManager = PreferenceManager(application)
    }

    val assignments: Flow<List<Assignment>> = flow {
        val userId = preferenceManager.getUserId()
        assignmentRepository.getAllAssignments(userId).collect { emit(it) }
    }

    val pendingAssignments: Flow<List<Assignment>> = flow {
        val userId = preferenceManager.getUserId()
        assignmentRepository.getPendingAssignments(userId).collect { emit(it) }
    }

    fun addAssignment(assignment: Assignment) {
        viewModelScope.launch {
            val userId = preferenceManager.getUserId()
            assignmentRepository.insertAssignment(assignment.copy(userId = userId))
        }
    }

    fun updateAssignment(assignment: Assignment) {
        viewModelScope.launch {
            assignmentRepository.updateAssignment(assignment)
        }
    }

    fun deleteAssignment(assignment: Assignment) {
        viewModelScope.launch {
            assignmentRepository.deleteAssignment(assignment)
        }
    }
}

// ==================== EXAM VIEW MODEL ====================

class ExamViewModel(application: Application) : AndroidViewModel(application) {
    private val examRepository: ExamRepository
    private val preferenceManager: PreferenceManager

    init {
        val database = AppDatabase.getDatabase(application)
        examRepository = ExamRepository(database.examDao())
        preferenceManager = PreferenceManager(application)
    }

    val exams: Flow<List<Exam>> = flow {
        val userId = preferenceManager.getUserId()
        examRepository.getAllExams(userId).collect { emit(it) }
    }

    val upcomingExams: Flow<List<Exam>> = flow {
        val userId = preferenceManager.getUserId()
        examRepository.getUpcomingExams(userId).collect { emit(it) }
    }

    fun addExam(exam: Exam) {
        viewModelScope.launch {
            val userId = preferenceManager.getUserId()
            examRepository.insertExam(exam.copy(userId = userId))
        }
    }

    fun updateExam(exam: Exam) {
        viewModelScope.launch {
            examRepository.updateExam(exam)
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            examRepository.deleteExam(exam)
        }
    }
}

// ==================== SCHEDULE VIEW MODEL ====================


class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val scheduleRepository: ScheduleRepository
    private val preferenceManager: PreferenceManager

    init {
        val database = AppDatabase.getDatabase(application)
        scheduleRepository = ScheduleRepository(database.scheduleDao())
        preferenceManager = PreferenceManager(application)
    }

    val schedules: Flow<List<Schedule>> = flow {
        val userId = preferenceManager.getUserId()
        scheduleRepository.getAllSchedules(userId).collect { emit(it) }
    }

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val userId = preferenceManager.getUserId()
            scheduleRepository.insertSchedule(schedule.copy(userId = userId))
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.updateSchedule(schedule)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
        }
    }
}

// ==================== NOTE VIEW MODEL ====================


class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteRepository: NoteRepository
    private val preferenceManager: PreferenceManager

    init {
        val database = AppDatabase.getDatabase(application)
        noteRepository = NoteRepository(database.noteDao())
        preferenceManager = PreferenceManager(application)
    }

    val notes: Flow<List<Note>> = flow {
        val userId = preferenceManager.getUserId()
        noteRepository.getAllNotes(userId).collect { emit(it) }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            val userId = preferenceManager.getUserId()
            noteRepository.insertNote(note.copy(userId = userId))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }
}