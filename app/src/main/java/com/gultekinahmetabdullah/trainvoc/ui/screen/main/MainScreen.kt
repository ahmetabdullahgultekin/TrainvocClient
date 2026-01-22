package com.gultekinahmetabdullah.trainvoc.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gultekinahmetabdullah.trainvoc.ui.animations.AnimationSpecs
import com.gultekinahmetabdullah.trainvoc.classes.enums.Route
import com.gultekinahmetabdullah.trainvoc.classes.quiz.Quiz
import com.gultekinahmetabdullah.trainvoc.classes.quiz.QuizParameter
import com.gultekinahmetabdullah.trainvoc.navigation.gamesNavGraph
import com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary.WordManagementScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.components.AppBottomBar
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.components.AppBottomSheet
import com.gultekinahmetabdullah.trainvoc.ui.screen.main.components.AppTopBar
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.AboutScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.HelpScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.SettingsScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.other.StatsScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.QuizExamMenuScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.QuizScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.settings.NotificationSettingsScreen
import com.gultekinahmetabdullah.trainvoc.ui.screen.welcome.UsernameScreen
import com.gultekinahmetabdullah.trainvoc.viewmodel.QuizViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.SettingsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StatsViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.StoryViewModel
import com.gultekinahmetabdullah.trainvoc.viewmodel.WordViewModel
import com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.QuizMenuScreen

/**
 * Main screen with bottom navigation
 *
 * This is the primary navigation container for the app. Each destination
 * manages its own ViewModel instance using hiltViewModel() for proper
 * scoping and lifecycle management.
 *
 * @param startWordId Optional word ID to navigate to on startup (from notifications)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    startWordId: String? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )
    val showBottomSheet = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val isTopAppBarVisible = remember { mutableStateOf(true) }
    val isBottomBarVisible = remember { mutableStateOf(true) } // Always show bottom bar

    val parameter = remember { mutableStateOf<QuizParameter?>(null) }

    // Get ViewModels scoped to navigation graph for proper lifecycle management
    // These are scoped to the NavBackStackEntry, not to MainScreen
    // This ensures they survive configuration changes and are shared across quiz flow
    val quizViewModel: QuizViewModel = hiltViewModel()
    val wordViewModel: WordViewModel = hiltViewModel()
    val statsViewModel: StatsViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val storyViewModel: StoryViewModel = hiltViewModel()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            if (isBottomBarVisible.value) {
                AppBottomBar(navController)
            }
        },
        topBar = {
            if (isTopAppBarVisible.value) {
                AppTopBar(
                    navBackStackEntry = navBackStackEntry.value,
                    navController = navController,
                    onMenuClick = { showBottomSheet.value = true }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Route.HOME,
                modifier = Modifier.padding(paddingValues),
                enterTransition = { AnimationSpecs.slideInFromRight() },
                exitTransition = { AnimationSpecs.slideOutToLeft() },
                popEnterTransition = { AnimationSpecs.slideInFromLeft() },
                popExitTransition = { AnimationSpecs.slideOutToRight() }
            ) {
                composable(Route.HOME) {
                    HomeScreen(
                        onNavigateToHelp = { navController.navigate(Route.HELP) },
                        onNavigateToStory = { navController.navigate(Route.STORY) },
                        onNavigateToSettings = { navController.navigate(Route.SETTINGS) },
                        onNavigateToStats = { navController.navigate(Route.STATS) },
                        onNavigateToQuiz = { navController.navigate(Route.QUIZ_EXAM_MENU) },
                        // Phase 1 - New navigation callbacks
                        onNavigateToProfile = { navController.navigate(Route.PROFILE) },
                        onNavigateToWordOfDay = { navController.navigate(Route.WORD_OF_DAY) },
                        onNavigateToFavorites = { navController.navigate(Route.FAVORITES) },
                        onNavigateToLastQuiz = { navController.navigate(Route.LAST_QUIZ_RESULTS) },
                        // Phase 2 & 3 - Gamification & Engagement
                        onNavigateToDailyGoals = { navController.navigate(Route.DAILY_GOALS) },
                        onNavigateToAchievements = { navController.navigate(Route.ACHIEVEMENTS) },
                        onNavigateToStreakDetail = { navController.navigate(Route.STREAK_DETAIL) },
                        onNavigateToLeaderboard = { navController.navigate(Route.LEADERBOARD) },
                        onNavigateToWordProgress = { navController.navigate(Route.WORD_PROGRESS) },
                        onNavigateToDictionary = { navController.navigate(Route.DICTIONARY) },
                        onNavigateToGames = { navController.navigate(Route.GAMES_MENU) },
                        // Phase 5 - Update Notes & Changelog
                        onNavigateToChangelog = { navController.navigate(Route.changelog()) },
                    )
                }
                composable(Route.STORY) {
                    StoryScreen(
                        viewModel = storyViewModel,
                        onLevelSelected = { level ->
                            parameter.value = QuizParameter.Level(level)

                            parameter.value?.let {
                                quizViewModel.startQuiz(
                                    it,
                                    Quiz.quizTypes[0]
                                ) // Default to first quiz type
                            }
                            navController.navigate(Route.QUIZ)
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(Route.QUIZ_EXAM_MENU) {
                    QuizExamMenuScreen(
                        onExamSelected = { quizParameter ->
                            navController.navigate(Route.QUIZ_MENU)
                            parameter.value = quizParameter
                        }
                    )
                }
                composable(Route.QUIZ_MENU) {
                    QuizMenuScreen(
                        onQuizSelected = { quiz ->
                            parameter.value?.let {
                                quizViewModel.startQuiz(it, quiz)
                                navController.navigate(Route.QUIZ)
                            }
                        }
                    )
                }
                composable(Route.QUIZ) {
                    QuizScreen(
                        quizViewModel = quizViewModel,
                        onQuit = { navController.navigate(Route.HOME) },
                    )
                }
                composable(Route.MANAGEMENT) {
                    WordManagementScreen(wordViewModel = wordViewModel)
                }
                composable(Route.USERNAME) {
                    UsernameScreen(navController)
                }
                composable(Route.SETTINGS) {
                    SettingsScreen(navController, settingsViewModel)
                }
                composable(Route.BACKUP) {
                    com.gultekinahmetabdullah.trainvoc.ui.backup.BackupScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Route.NOTIFICATION_SETTINGS) {
                    NotificationSettingsScreen(navController = navController)
                }
                composable(Route.ACCESSIBILITY_SETTINGS) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.other.AccessibilitySettingsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Route.CHANGELOG,
                    arguments = listOf(
                        navArgument("versionCode") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { backStackEntry ->
                    val versionCode = backStackEntry.arguments?.getInt("versionCode")
                    com.gultekinahmetabdullah.trainvoc.ui.screen.other.ChangelogScreen(
                        navController = navController,
                        targetVersionCode = if (versionCode != null && versionCode != -1) versionCode else null
                    )
                }
                composable(Route.HELP) {
                    HelpScreen()
                }
                composable(Route.ABOUT) {
                    AboutScreen()
                }
                composable(Route.STATS) {
                    StatsScreen(statsViewModel = statsViewModel)
                }
                composable(Route.DICTIONARY) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary.DictionaryScreen(
                        navController = navController,
                        wordViewModel = wordViewModel
                    )
                }
                composable(
                    route = Route.WORD_DETAIL,
                    arguments = listOf(navArgument("wordId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val wordId = backStackEntry.arguments?.getString("wordId") ?: ""
                    com.gultekinahmetabdullah.trainvoc.ui.screen.dictionary.WordDetailScreen(
                        wordId = wordId,
                        wordViewModel = wordViewModel,
                        onNavigateToSynonym = { synonymWord ->
                            navController.navigate(Route.wordDetail(synonymWord))
                        }
                    )
                }

                // Phase 1 - New Screens
                composable(Route.PROFILE) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.profile.ProfileScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Route.WORD_OF_DAY) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.features.WordOfTheDayScreen(
                        onBackClick = { navController.popBackStack() },
                        onPractice = { navController.navigate(Route.QUIZ) }
                    )
                }
                composable(Route.FAVORITES) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.features.FavoritesScreen(
                        onBackClick = { navController.popBackStack() },
                        onPracticeFavorites = { navController.navigate(Route.QUIZ) },
                        onWordClick = { wordId -> navController.navigate(Route.wordDetail(wordId)) }
                    )
                }
                composable(Route.LAST_QUIZ_RESULTS) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.quiz.LastQuizResultsScreen(
                        onBackClick = { navController.popBackStack() },
                        onRetryQuiz = { navController.navigate(Route.QUIZ) },
                        onReviewMissed = {
                            // Navigate to quiz with review mode
                            // For now, restart quiz - future: add review mode parameter
                            navController.navigate(Route.QUIZ)
                        }
                    )
                }

                // Phase 2 - Gamification Screens
                composable(Route.DAILY_GOALS) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.gamification.DailyGoalsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Route.ACHIEVEMENTS) {
                    val viewModel: com.gultekinahmetabdullah.trainvoc.viewmodel.GamificationViewModel = hiltViewModel()
                    val achievements by viewModel.achievementProgress.collectAsState()

                    com.gultekinahmetabdullah.trainvoc.gamification.ui.AchievementsScreen(
                        achievements = achievements,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Route.STREAK_DETAIL) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.gamification.StreakDetailScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // Phase 3 - Engagement Features
                composable(Route.LEADERBOARD) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.social.LeaderboardScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Route.WORD_PROGRESS) {
                    com.gultekinahmetabdullah.trainvoc.ui.screen.progress.WordProgressScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // Phase 4 - Games Navigation
                // Add all 11 game screens via games nav graph
                gamesNavGraph(navController)
            }
        }
    }

    if (showBottomSheet.value) {
        AppBottomSheet(
            sheetState = sheetState,
            coroutineScope = coroutineScope,
            showBottomSheet = showBottomSheet,
            navController = navController
        )
    }

    LaunchedEffect(startWordId) {
        if (!startWordId.isNullOrEmpty()) {
            navController.navigate(Route.wordDetail(startWordId))
        }
    }
}
