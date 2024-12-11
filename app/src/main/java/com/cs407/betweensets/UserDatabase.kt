package com.cs407.betweensets

import android.content.Context
import android.os.Parcelable
import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.parcelize.Parcelize
import java.util.Date

// User entity
@Entity(
    indices = [Index(value = ["userName"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val userName: String = ""
)

// Converters for Room database
class Converters {
    @TypeConverter
    fun fromTimeStamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}

// Note entity
@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Int = 0,
    val noteTitle: String,
    val noteSets: Int,
    val noteReps: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT) val noteDetail: String?,
    val notePath: String?,
    val lastEdited: Date
)

// Workout entity
@Entity
data class Workout(
    @PrimaryKey(autoGenerate = true) val workoutId: Int = 0,
    val workoutName: String,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT) val exercises: String // Comma-separated exercise IDs
)

// User-Note relationship entity
@Entity(
    primaryKeys = ["userId", "noteId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Note::class,
            parentColumns = ["noteId"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserNoteRelation(
    val userId: Int,
    val noteId: Int
)

// NoteSummary for returning simplified note information
@Parcelize
data class NoteSummary(
    val noteId: Int,
    val noteTitle: String,
    val noteSets: Int,
    val noteReps: Int,
    val lastEdited: Date
) : Parcelable

// User DAO
@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE userName = :name")
    suspend fun getByName(name: String): User

    @Query("SELECT * FROM user WHERE userId = :id")
    suspend fun getById(id: Int): User

    @Query("SELECT * FROM note ORDER BY lastEdited DESC")
    suspend fun getAllNotes(): List<Note>

    @Query(
        """SELECT * FROM User, Note, UserNoteRelation
            WHERE User.userId = :id
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId
            ORDER BY Note.lastEdited DESC"""
    )
    suspend fun getUsersWithNoteListsById(id: Int): List<NoteSummary>

    @Query(
        """SELECT * FROM User, Note, UserNoteRelation
            WHERE User.userId = :id
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId
            ORDER BY Note.lastEdited DESC"""
    )
    fun getUsersWithNoteListsByIdPaged(id: Int): PagingSource<Int, NoteSummary>

    @Insert
    suspend fun insert(user: User)
}

// Note DAO
@Dao
interface NoteDao {
    @Query("SELECT * FROM note WHERE noteId = :id")
    suspend fun getById(id: Int): Note

    @Query("SELECT noteId FROM note WHERE rowId = :rowId")
    suspend fun getByRowId(rowId: Long): Int

    @Upsert
    suspend fun upsert(note: Note): Long

    @Insert
    suspend fun insertRelation(userAndNote: UserNoteRelation)

    @Transaction
    suspend fun upsertNote(note: Note, userId: Int) {
        val rowId = upsert(note)
        if (note.noteId == 0) {
            val noteId = getByRowId(rowId)
            insertRelation(UserNoteRelation(userId, noteId))
        }
    }

    @Query(
        """SELECT COUNT(*) FROM User, Note, UserNoteRelation
            WHERE User.userId = :userId
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId"""
    )
    suspend fun userNoteCount(userId: Int): Int
}

// Workout DAO
@Dao
interface WorkoutDao {
    @Query("SELECT * FROM Workout")
    suspend fun getAllWorkouts(): List<Workout>

    @Insert
    suspend fun insertWorkout(workout: Workout)

    @Query("SELECT * FROM Workout WHERE workoutId = :id")
    suspend fun getWorkoutById(id: Int): Workout

    @Query(
        """SELECT * FROM Note
           WHERE noteId IN (:exerciseIds)"""
    )
    suspend fun getNotesByWorkout(exerciseIds: List<Int>): List<Note>
}

// Delete DAO
@Dao
interface DeleteDao {
    @Query("DELETE FROM user WHERE userId = :userId")
    suspend fun deleteUser(userId: Int)

    @Query(
        """SELECT Note.noteId FROM User, Note, UserNoteRelation
                WHERE User.userId = :userId
                    AND UserNoteRelation.userId = User.userId
                    AND Note.noteId = UserNoteRelation.noteId"""
    )
    suspend fun getAllNoteIdsByUser(userId: Int): List<Int>

    @Query("DELETE FROM note WHERE noteId IN (:notesIds)")
    suspend fun deleteNotes(notesIds: List<Int>)

    @Transaction
    suspend fun delete(userId: Int) {
        deleteNotes(getAllNoteIdsByUser(userId))
        deleteUser(userId)
    }
}

// Database definition
@Database(entities = [User::class, Note::class, UserNoteRelation::class, Workout::class], version = 3)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun deleteDao(): DeleteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE Note_new (
                        noteId INTEGER PRIMARY KEY NOT NULL,
                        noteTitle TEXT NOT NULL,
                        noteSets INTEGER NOT NULL DEFAULT 0,
                        noteReps INTEGER NOT NULL DEFAULT 0,
                        noteDetail TEXT,
                        notePath TEXT,
                        lastEdited INTEGER NOT NULL
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO Note_new (noteId, noteTitle, noteSets, noteReps, noteDetail, notePath, lastEdited)
                    SELECT noteId, noteTitle, 0 AS noteSets, 0 AS noteReps, noteDetail, notePath, lastEdited
                    FROM Note
                """.trimIndent())

                database.execSQL("DROP TABLE Note")
                database.execSQL("ALTER TABLE Note_new RENAME TO Note")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE Workout (
                        workoutId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        workoutName TEXT NOT NULL,
                        exercises TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }
    }
}
