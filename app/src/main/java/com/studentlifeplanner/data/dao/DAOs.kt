package com.studentlifeplanner.data.dao

import androidx.room.*
import com.studentlifeplanner.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE userId = :userId ORDER BY courseName")
    fun getAllCourses(userId: Int): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: Int): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun deleteCourseById(courseId: Int)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE userId = :userId ORDER BY dayOfWeek, startTime")
    fun getAllSchedules(userId: Int): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE userId = :userId AND dayOfWeek = :day ORDER BY startTime")
    fun getSchedulesByDay(userId: Int, day: Int): Flow<List<Schedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule): Long

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("DELETE FROM schedules WHERE courseId = :courseId")
    suspend fun deleteSchedulesByCourse(courseId: Int)
}

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments WHERE userId = :userId ORDER BY dueDate, dueTime")
    fun getAllAssignments(userId: Int): Flow<List<Assignment>>

    @Query("SELECT * FROM assignments WHERE userId = :userId AND isCompleted = 0 ORDER BY dueDate, dueTime")
    fun getPendingAssignments(userId: Int): Flow<List<Assignment>>

    @Query("SELECT * FROM assignments WHERE userId = :userId AND courseId = :courseId ORDER BY dueDate")
    fun getAssignmentsByCourse(userId: Int, courseId: Int): Flow<List<Assignment>>

    @Query("SELECT * FROM assignments WHERE id = :assignmentId")
    suspend fun getAssignmentById(assignmentId: Int): Assignment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment): Long

    @Update
    suspend fun updateAssignment(assignment: Assignment)

    @Delete
    suspend fun deleteAssignment(assignment: Assignment)

    @Query("UPDATE assignments SET isCompleted = :completed, completedAt = :completedAt WHERE id = :assignmentId")
    suspend fun markAsCompleted(assignmentId: Int, completed: Boolean, completedAt: Long?)
}

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams WHERE userId = :userId ORDER BY examDate, startTime")
    fun getAllExams(userId: Int): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE userId = :userId AND courseId = :courseId ORDER BY examDate")
    fun getExamsByCourse(userId: Int, courseId: Int): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE userId = :userId AND isCompleted = 0 ORDER BY examDate, startTime")
    fun getUpcomingExams(userId: Int): Flow<List<Exam>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getAllNotes(userId: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE userId = :userId AND courseId = :courseId ORDER BY updatedAt DESC")
    fun getNotesByCourse(userId: Int, courseId: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE userId = :userId AND isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteNotes(userId: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}