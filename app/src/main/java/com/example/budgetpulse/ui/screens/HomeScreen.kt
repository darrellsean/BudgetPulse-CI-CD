package com.example.budgetpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.ui.components.AddCategorySheet
import com.example.budgetpulse.ui.components.AddTransactionDialog
import com.example.budgetpulse.ui.components.AppBottomNavigationBar
import com.example.budgetpulse.ui.components.CategoryCard
import com.example.budgetpulse.ui.components.CustomCategoryDialog
import com.example.budgetpulse.ui.components.NavTab
import com.example.budgetpulse.ui.components.SectionHeader
import com.example.budgetpulse.ui.viewmodel.MainViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateTab: (NavTab) -> Unit = {},
    viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    val expenseCategories = remember(uiState.activeCategories) {
        uiState.activeCategories.filter { it.type == CategoryType.EXPENSE }
    }
    val incomeCategories = remember(uiState.activeCategories) {
        uiState.activeCategories.filter { it.type == CategoryType.INCOME }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BudgetPulse",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.isEditMode) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Edit Mode",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleEditMode() }) {
                        Text(
                            text = if (uiState.isEditMode) "Done" else "Edit",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        // Same navbar setup as Budget & Settings pages (hidden while in Edit Mode)
        bottomBar = {
            if (!uiState.isEditMode) {
                AppBottomNavigationBar(
                    currentTab = NavTab.HOME,
                    onTabSelected = onNavigateTab
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        // Requirement 2: Detect long press gesture on page background to toggle Edit Mode
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            viewModel.toggleEditMode()
                        }
                    )
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Banner instruction hint in Edit Mode
                AnimatedVisibility(visible = uiState.isEditMode) {
                    Surface(
                        color = Color(0xFFFFF8E1), // Soft yellow banner
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "💡 Tap 'X' to delete card • Long press background or tap Done to exit Edit Mode",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                }

                // Requirement 4: LazyVerticalGrid for card layout on homepage
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 110.dp), // 3 columns on phones, more on tablets / landscape
                    contentPadding = PaddingValues(bottom = if (uiState.isEditMode) 100.dp else 16.dp, top = 8.dp, start = 8.dp, end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Hint: what the card amounts mean
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Totals for ${uiState.currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))} • Tap a category to add today's record",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Requirement 1: "Expense" Section (5 cards by default)
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SectionHeader(
                            title = "Expense",
                            icon = Icons.Default.ArrowDownward,
                            accentColor = Color(0xFFE53935),
                            count = expenseCategories.size
                        )
                    }

                    items(items = expenseCategories, key = { it.id }) { category ->
                        CategoryCard(
                            category = category,
                            isEditMode = uiState.isEditMode,
                            onDeleteClick = { viewModel.deleteCategory(category.id) },
                            onCardClick = {
                                // Tap a card -> record an expense/income for today
                                if (!uiState.isEditMode) viewModel.startAddingTransaction(category)
                            }
                        )
                    }

                    // Requirement 1: "Income" Section (1 card by default: Salary)
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionHeader(
                            title = "Income",
                            icon = Icons.Default.ArrowUpward,
                            accentColor = Color(0xFF43A047),
                            count = incomeCategories.size
                        )
                    }

                    items(items = incomeCategories, key = { it.id }) { category ->
                        CategoryCard(
                            category = category,
                            isEditMode = uiState.isEditMode,
                            onDeleteClick = { viewModel.deleteCategory(category.id) },
                            onCardClick = {
                                // Tap a card -> record an expense/income for today
                                if (!uiState.isEditMode) viewModel.startAddingTransaction(category)
                            }
                        )
                    }
                }
            }

            // Requirement 2: Yellow floating add button (containing "+" icon) appears at bottom center in edit mode
            AnimatedVisibility(
                visible = uiState.isEditMode,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                FloatingActionButton(
                    onClick = { viewModel.openAddCategorySheet() },
                    containerColor = Color(0xFFFFD54F), // Bright Yellow
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Category",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

        }

        // Requirement 3: Add Category Bottom Sheet
        if (uiState.showAddCategorySheet) {
            AddCategorySheet(
                activeCategories = uiState.activeCategories,
                availableCategories = uiState.availableCategories,
                onAddCategory = { category ->
                    viewModel.addCategoryToActive(category)
                },
                onCustomClick = {
                    viewModel.openCustomCategoryDialog()
                },
                onDismiss = {
                    viewModel.closeAddCategorySheet()
                }
            )
        }

        // Custom Category Dialog
        if (uiState.showCustomCategoryDialog) {
            CustomCategoryDialog(
                onDismiss = { viewModel.closeCustomCategoryDialog() },
                onConfirm = { name, type, iconName, colorHex ->
                    viewModel.addCustomCategory(name, type, iconName, colorHex)
                }
            )
        }

        // Add Transaction Dialog (amount + optional description, saved for today)
        uiState.addingTransactionCategory?.let { category ->
            AddTransactionDialog(
                category = category,
                onDismiss = { viewModel.dismissAddingTransaction() },
                onConfirm = { amount, description ->
                    viewModel.saveTransaction(category, amount, description)
                }
            )
        }
    }
}