package com.example.budgetpulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.budgetpulse.data.model.CategoryIcons
import com.example.budgetpulse.data.model.CategoryItem
import com.example.budgetpulse.data.model.CategoryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategorySheet(
    activeCategories: List<CategoryItem>,
    availableCategories: List<CategoryItem>,
    onAddCategory: (CategoryItem) -> Unit,
    onCustomClick: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(CategoryType.EXPENSE) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
        ) {
            // Panel Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Category",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Requirement 3: Tab row at top to switch between "Expense" and "Income"
            SecondaryTabRow(
                selectedTabIndex = if (selectedTab == CategoryType.EXPENSE) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == CategoryType.EXPENSE,
                    onClick = { selectedTab = CategoryType.EXPENSE },
                    text = {
                        Text(
                            text = "Expense",
                            fontWeight = if (selectedTab == CategoryType.EXPENSE) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == CategoryType.INCOME,
                    onClick = { selectedTab = CategoryType.INCOME },
                    text = {
                        Text(
                            text = "Income",
                            fontWeight = if (selectedTab == CategoryType.INCOME) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter available categories by selected tab
            val filteredCategories = remember(availableCategories, selectedTab) {
                availableCategories.filter { it.type == selectedTab }
            }

            // Requirement 3: Main body is a vertical list of custom categories
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filteredCategories, key = { it.id }) { category ->
                    val isAlreadyAdded = activeCategories.any {
                        it.name.equals(category.name, ignoreCase = true) && it.type == category.type
                    }

                    CategoryListRow(
                        category = category,
                        isAdded = isAlreadyAdded,
                        onAddClick = { onAddCategory(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Requirement 3: Large, wide yellow "Custom" button fixed at very bottom
            Button(
                onClick = onCustomClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD54F), // Yellow background
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Custom",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryListRow(
    category: CategoryItem,
    isAdded: Boolean,
    onAddClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Requirement 3: Circular icon and category name on left
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(category.colorHex))
                ) {
                    Icon(
                        imageVector = CategoryIcons.getIcon(category.iconName),
                        contentDescription = category.name,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Requirement 3: Blue "+" button on the right
            if (isAdded) {
                IconButton(
                    onClick = { /* Already added */ },
                    enabled = false,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Gray.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Added",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF2196F3), CircleShape) // Blue background
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add ${category.name}",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}