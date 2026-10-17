package com.studentlifeplanner.data.repository

import com.studentlifeplanner.data.dao.*
import com.studentlifeplanner.data.model.*
import com.studentlifeplanner.utils.hashPassword
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User): Result<Long> {
        return try {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                return Result.failure(Exception("Email already exists"))
            }
            val hashedPassword = user.password.hashPassword()
            val newUser = user.copy(password = hashedPassword)
            val userId = userDao.insertUser(newUser)
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmail(email)
                ?: return Result.failure(Exception("User not found"))

            val hashedPassword = password.hashPassword()
            if (user.password != hashedPassword) {
                return Result.failure(Exception("Invalid password"))
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class CourseRepository(private val courseDao: CourseDao) {
    fun getAllCourses(userId: Int): Flow<List<Course>> = courseDao.getAllCourses(userId)
    suspend fun insertCourse(course: Course) = courseDao.insertCourse(course)
    suspend fun updateCourse(course: Course) = courseDao.updateCourse(course)
    suspend fun deleteCourse(course: Course) = courseDao.deleteCourse(course)
}

class AssignmentRepository(private val assignmentDao: AssignmentDao) {
    fun getAllAssignments(userId: Int): Flow<List<Assignment>> = assignmentDao.getAllAssignments(userId)
    fun getPendingAssignments(userId: Int): Flow<List<Assignment>> = assignmentDao.getPendingAssignments(userId)
    suspend fun insertAssignment(assignment: Assignment) = assignmentDao.insertAssignment(assignment)
    suspend fun updateAssignment(assignment: Assignment) = assignmentDao.updateAssignment(assignment)
    suspend fun deleteAssignment(assignment: Assignment) = assignmentDao.deleteAssignment(assignment)
}

class ExamRepository(private val examDao: ExamDao) {
    fun getAllExams(userId: Int): Flow<List<Exam>> = examDao.getAllExams(userId)
    fun getUpcomingExams(userId: Int): Flow<List<Exam>> = examDao.getUpcomingExams(userId)
    suspend fun insertExam(exam: Exam) = examDao.insertExam(exam)
    suspend fun updateExam(exam: Exam) = examDao.updateExam(exam)
    suspend fun deleteExam(exam: Exam) = examDao.deleteExam(exam)
}

class ScheduleRepository(private val scheduleDao: ScheduleDao) {
    fun getAllSchedules(userId: Int): Flow<List<Schedule>> = scheduleDao.getAllSchedules(userId)
    suspend fun insertSchedule(schedule: Schedule) = scheduleDao.insertSchedule(schedule)
    suspend fun updateSchedule(schedule: Schedule) = scheduleDao.updateSchedule(schedule)
    suspend fun deleteSchedule(schedule: Schedule) = scheduleDao.deleteSchedule(schedule)
}

class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNotes(userId: Int): Flow<List<Note>> = noteDao.getAllNotes(userId)
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
}