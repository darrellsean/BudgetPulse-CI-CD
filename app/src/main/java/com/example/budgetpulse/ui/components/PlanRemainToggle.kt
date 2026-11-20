package com.example.budgetpulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.budgetpulse.data.model.BudgetViewMode

@Composable
fun PlanRemainToggle(
    selectedMode: BudgetViewMode,
    onModeSelected: (BudgetViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val isPlan = selectedMode == BudgetViewMode.PLAN

            // Plan Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isPlan) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable { onModeSelected(BudgetViewMode.PLAN) }
                    .padding(horizontal = 22.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Plan",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isPlan) FontWeight.Bold else FontWeight.Medium,
                    color = if (isPlan) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Remain Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (!isPlan) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable { onModeSelected(BudgetViewMode.REMAIN) }
                    .padding(horizontal = 22.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Remain",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (!isPlan) FontWeight.Bold else FontWeight.Medium,
                    color = if (!isPlan) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}