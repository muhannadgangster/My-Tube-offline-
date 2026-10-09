package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM video_comments WHERE videoId = :videoId ORDER BY timestamp DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Query("SELECT COUNT(*) FROM video_comments WHERE videoId = :videoId")
    fun getCommentCountForVideo(videoId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM video_comments WHERE videoId = :videoId")
    suspend fun getCommentCountDirect(videoId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("UPDATE video_comments SET likesCount = likesCount + 1, isLiked = 1 WHERE commentId = :commentId")
    suspend fun likeComment(commentId: Long)

    @Query("DELETE FROM video_comments WHERE commentId = :commentId")
    suspend fun deleteComment(commentId: Long)

    @Query("DELETE FROM video_comments WHERE videoId = :videoId")
    suspend fun deleteCommentsForVideo(videoId: String)
}
