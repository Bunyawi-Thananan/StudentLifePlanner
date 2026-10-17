package com.studentlifeplanner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(tableName = "users")
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val studentId: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

@Entity(tableName = "courses")
@Parcelize
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val courseCode: String,
    val courseName: String,
    val instructor: String = "",
    val credits: Int = 3,
    val color: String = "#2196F3",
    val semester: String = "",
    val year: Int = 2024
) : Parcelable

@Entity(tableName = "schedules")
@Parcelize
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val courseId: Int,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val room: String = "",
    val building: String = "",
    val type: String = "Lecture"
) : Parcelable

@Entity(tableName = "assignments")
@Parcelize
data class Assignment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val courseId: Int,
    val title: String,
    val description: String = "",
    val dueDate: String,
    val dueTime: String = "23:59",
    val priority: Int = 2,
    val status: String = "pending",
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val notificationTime: String? = null
) : Parcelable

@Entity(tableName = "exams")
@Parcelize
data class Exam(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val courseId: Int,
    val title: String,
    val examDate: String,
    val startTime: String,
    val endTime: String,
    val room: String = "",
    val building: String = "",
    val examType: String = "Midterm",
    val totalMarks: Int = 100,
    val notes: String = "",
    val isCompleted: Boolean = false,
    val obtainedMarks: Int? = null,
    val notificationTime: String? = null
) : Parcelable

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val courseId: Int,
    val title: String,
    val content: String = "",
    val fileUri: String? = null,
    val fileType: String? = null,
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) : Parcelable