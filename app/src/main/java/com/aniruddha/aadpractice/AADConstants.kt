package com.aniruddha.aadpractice

class AADConstants {
    companion object {
        const val CHANNEL_ID = "channel_1"
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_REPLY_KEY = "reply_key"
        const val PROGRESS_MAX = 100
        const val PROGRESS_CURRENT = 0
        const val SCHEDULE_JOB_ID = 0
        const val DELETE_ALL_QUERY = "DELETE FROM WORD_TABLE"
        const val SELECT_ALL_QUERY = "SELECT * FROM WORD_TABLE ORDER BY word ASC"
    }
}