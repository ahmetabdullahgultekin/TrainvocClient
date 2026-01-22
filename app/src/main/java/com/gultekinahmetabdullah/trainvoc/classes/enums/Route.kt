package com.gultekinahmetabdullah.trainvoc.classes.enums

object Route {
    const val HOME = "home"
    const val MAIN = "main"
    const val SPLASH = "splash"
    const val STORY = "story"
    const val QUIZ = "quiz"
    const val QUIZ_MENU = "quiz_menu"
    const val QUIZ_EXAM_MENU = "quiz_exam_menu"
    const val MANAGEMENT = "management"
    const val USERNAME = "username"
    const val WELCOME = "welcome"
    const val HELP = "help"
    const val ABOUT = "about"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val DICTIONARY = "dictionary"
    const val BACKUP = "backup"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val WORD_DETAIL = "word_detail/{wordId}"
    const val FEATURE_FLAGS_ADMIN = "feature_flags_admin"
    const val FEATURE_FLAGS_USER = "feature_flags_user"
    const val GAMES_MENU = "games_menu"

    // Phase 1 - New Routes
    const val PROFILE = "profile"
    const val WORD_OF_DAY = "word_of_day"
    const val FAVORITES = "favorites"
    const val LAST_QUIZ_RESULTS = "last_quiz_results"
    const val DAILY_GOALS = "daily_goals"
    const val ACHIEVEMENTS = "achievements"

    // Phase 2 - Additional Routes
    const val STREAK_DETAIL = "streak_detail"

    // Phase 3 - Engagement Features
    const val LEADERBOARD = "leaderboard"
    const val WORD_PROGRESS = "word_progress"

    // Phase 4 - Accessibility
    const val ACCESSIBILITY_SETTINGS = "accessibility_settings"

    // Phase 5 - Update Notes & Changelog
    const val CHANGELOG = "changelog?versionCode={versionCode}"

    fun wordDetail(wordId: String) = "word_detail/$wordId"

    fun changelog(versionCode: Int? = null) =
        if (versionCode != null) "changelog?versionCode=$versionCode" else "changelog"
}