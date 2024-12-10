package com.cs407.betweensets

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.room.Upsert
import com.cs407.betweensets.R
import java.util.Date

// Define your own @Entity, @Dao and @Database
@Entity(
    indices = [Index(
        value = ["userName"], unique = true
    )]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val userName: String = ""
)
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

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Int = 0,
    val noteTitle: String,
    val noteAbstract: String,

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT) val noteDetail: String?,
    val notePath: String?,
    val lastEdited: Date
)

@Entity(
    primaryKeys = ["userId", "noteId"],
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Note::class,
        parentColumns = ["noteId"],
        childColumns = ["noteId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UserNoteRelation(
    val userId: Int,
    val noteId: Int
)

data class NoteSummary(
    val noteId: Int,
    val noteTitle: String,
    val noteAbstract: String,
    val lastEdited: Date
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE userName = :name")
    suspend fun getByName(name: String): User

    @Query("SELECT * FROM user WHERE userId = :id")
    suspend fun getById(id: Int): User

    @Query(
        """SELECT * FROM User, Note, UserNoteRelation
            WHERE User.userId = :id
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId
            ORDER BY Note.lastEdited DESC"""
    )
    suspend fun getUsersWithNoteListsById(id: Int): List <NoteSummary>

    @Query(
        """SELECT * FROM User, Note, UserNoteRelation
            WHERE User.userId = :id 
                AND UserNoteRelation.userId = User.userId
                AND Note.noteId = UserNoteRelation.noteId
            ORDER BY Note.lastEdited DESC"""
    )
    fun getUsersWithNoteListsByIdPaged(id: Int): PagingSource<Int, NoteSummary>

    @Insert(entity = User::class)
    suspend fun insert(user:User)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM note WHERE noteId = :id")
    suspend fun getById(id: Int): Note

    @Query("SELECT noteId FROM note WHERE rowId = :rowId")
    suspend fun getByRowId(rowId: Long): Int

    @Upsert(entity = Note::class)
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

@Dao
interface DeleteDao {
    @Query("DELETE FROM user WHERE userId = :userId")
    suspend fun deleteUser(userId: Int)

    @Query(
        """SELECT Note.noteId FROM User, Note, UserNoteRelation
                WHERE User.userId = :userId
                    AND UserNoteRelation.userId
                    AND Note.noteId = UserNoteRelation.noteId
        """
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

@Database(entities = [User::class, Note::class, UserNoteRelation::class], version = 1)

@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
    abstract fun deleteDao(): DeleteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    context.getString(R.string.note_database),
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}