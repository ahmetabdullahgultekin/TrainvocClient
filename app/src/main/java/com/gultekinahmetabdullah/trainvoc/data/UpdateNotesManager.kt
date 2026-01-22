package com.gultekinahmetabdullah.trainvoc.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.gultekinahmetabdullah.trainvoc.BuildConfig
import com.gultekinahmetabdullah.trainvoc.model.UpdateHighlight
import com.gultekinahmetabdullah.trainvoc.model.UpdateNotes
import com.gultekinahmetabdullah.trainvoc.model.UpdateType
import java.io.IOException

/**
 * Manager for handling update notes and version tracking
 */
class UpdateNotesManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "app_updates"
        private const val KEY_LAST_SEEN_VERSION = "last_seen_version"
        private const val KEY_DISMISSED_PREFIX = "dismissed_"
        private const val UPDATES_JSON_FILENAME = "updates.json"
        private const val ALL_VERSIONS_JSON_FILENAME = "all_versions.json"

        @Volatile
        private var instance: UpdateNotesManager? = null

        fun getInstance(context: Context): UpdateNotesManager {
            return instance ?: synchronized(this) {
                instance ?: UpdateNotesManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    /**
     * Get update notes from JSON file
     * Returns null if file doesn't exist or can't be parsed
     */
    fun getUpdateNotes(): UpdateNotes? {
        return try {
            val json = loadJsonFromAssets(UPDATES_JSON_FILENAME)
            gson.fromJson(json, UpdateNotesData::class.java).toUpdateNotes()
        } catch (e: IOException) {
            // If JSON file doesn't exist yet, return default update notes
            getDefaultUpdateNotes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get all versions from all_versions.json file
     * Returns list of all versions sorted by version code (newest first)
     */
    fun getAllVersions(): List<UpdateNotes> {
        return try {
            val json = loadJsonFromAssets(ALL_VERSIONS_JSON_FILENAME)
            val allVersionsData = gson.fromJson(json, AllVersionsData::class.java)
            allVersionsData.versions.map { it.toUpdateNotes() }
                .sortedByDescending { it.versionCode }
        } catch (e: IOException) {
            // If JSON file doesn't exist, return single current version
            listOfNotNull(getUpdateNotes())
        } catch (e: Exception) {
            e.printStackTrace()
            listOfNotNull(getUpdateNotes())
        }
    }

    /**
     * Check if update notes should be shown
     * Shows if:
     * - Current version is newer than last seen version
     * - Update notes haven't been dismissed for this version
     */
    fun shouldShowUpdateNotes(): Boolean {
        val currentVersion = BuildConfig.VERSION_CODE
        val lastSeenVersion = prefs.getInt(KEY_LAST_SEEN_VERSION, 0)
        val dismissed = prefs.getBoolean("$KEY_DISMISSED_PREFIX$currentVersion", false)

        return currentVersion > lastSeenVersion && !dismissed
    }

    /**
     * Mark update notes as seen (increments version)
     */
    fun markUpdateNotesSeen() {
        prefs.edit()
            .putInt(KEY_LAST_SEEN_VERSION, BuildConfig.VERSION_CODE)
            .apply()
    }

    /**
     * Dismiss update notes for current version
     */
    fun dismissUpdateNotes() {
        prefs.edit()
            .putBoolean("$KEY_DISMISSED_PREFIX${BuildConfig.VERSION_CODE}", true)
            .apply()
    }

    /**
     * Load JSON file from assets
     */
    private fun loadJsonFromAssets(filename: String): String {
        return context.assets.open(filename).bufferedReader().use { it.readText() }
    }

    /**
     * Get default update notes when JSON file doesn't exist
     */
    private fun getDefaultUpdateNotes(): UpdateNotes {
        return UpdateNotes(
            currentVersion = BuildConfig.VERSION_NAME,
            versionCode = BuildConfig.VERSION_CODE,
            releaseDate = "2026-01-22",
            highlights = listOf(
                UpdateHighlight(
                    type = UpdateType.NEW,
                    title = "Modern Material 3 Design",
                    description = "Complete UI overhaul with beautiful Material 3 components and animations"
                ),
                UpdateHighlight(
                    type = UpdateType.IMPROVED,
                    title = "Enhanced Settings Screen",
                    description = "Redesigned settings with better organization and visual hierarchy"
                ),
                UpdateHighlight(
                    type = UpdateType.IMPROVED,
                    title = "Better Help & Support",
                    description = "Improved FAQ with expandable cards and easier contact options"
                )
            ),
            upcomingFeatures = listOf(
                "Backend sync across devices",
                "Cloud backup with Google Drive",
                "Text-to-Speech integration",
                "Memory games"
            )
        )
    }

    /**
     * Data class for JSON deserialization
     */
    private data class UpdateNotesData(
        val currentVersion: String,
        val versionCode: Int,
        val releaseDate: String,
        val highlights: List<UpdateHighlightData>,
        val upcomingFeatures: List<String>
    ) {
        fun toUpdateNotes(): UpdateNotes {
            return UpdateNotes(
                currentVersion = currentVersion,
                versionCode = versionCode,
                releaseDate = releaseDate,
                highlights = highlights.map { it.toUpdateHighlight() },
                upcomingFeatures = upcomingFeatures
            )
        }
    }

    /**
     * Data class for JSON deserialization
     */
    private data class UpdateHighlightData(
        val type: String,
        val title: String,
        val description: String
    ) {
        fun toUpdateHighlight(): UpdateHighlight {
            val updateType = when (type.uppercase()) {
                "NEW" -> UpdateType.NEW
                "IMPROVED" -> UpdateType.IMPROVED
                "FIXED" -> UpdateType.FIXED
                else -> UpdateType.NEW
            }
            return UpdateHighlight(
                type = updateType,
                title = title,
                description = description
            )
        }
    }

    /**
     * Data class for all versions JSON deserialization
     */
    private data class AllVersionsData(
        val versions: List<UpdateNotesData>
    )
}
