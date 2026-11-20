package com.example.budgetpulse.ui.components

import com.example.budgetpulse.ui.theme.LocalAppCurrency
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgetpulse.data.model.BudgetViewMode
import com.example.budgetpulse.data.model.CategoryBudget
import com.example.budgetpulse.data.model.CategoryIcons
import java.util.Locale

@Composable
fun BudgetCategoryRow(
    item: CategoryBudget,
    viewMode: BudgetViewMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(item.category.colorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CategoryIcons.getIcon(item.category.iconName),
                        contentDescription = item.category.name,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = item.category.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            val currency = LocalAppCurrency.current.symbol
            val displayValue = when (viewMode) {
                BudgetViewMode.PLAN -> {
                    item.allocatedBudget?.let { "$currency${String.format(Locale.ENGLISH, "%,.2f", it)}" } ?: "None"
                }
                BudgetViewMode.REMAIN -> {
                    item.allocatedBudget?.let { "$currency${String.format(Locale.ENGLISH, "%,.2f", item.remainingAmount)}" } ?: "None"
                }
            }

            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}