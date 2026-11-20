package com.example.budgetpulse.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgetpulse.ui.theme.LocalAppCurrency
import java.util.Locale

@Composable
fun FinancialSummaryCard(
    income: Double,
    expense: Double,
    balance: Double,
    modifier: Modifier = Modifier
) {
    val currency = LocalAppCurrency.current.symbol
    fun money(value: Double) = "$currency${String.format(Locale.ENGLISH, "%,.2f", value)}"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Requirement 2: Income (Green)
            SummaryColumn(
                label = "Income",
                amountText = money(income),
                amountColor = Color(0xFF43A047), // Green
                modifier = Modifier.weight(1f)
            )

            // Requirement 2: Expense (Red)
            SummaryColumn(
                label = "Expense",
                amountText = money(expense),
                amountColor = Color(0xFFE53935), // Red
                modifier = Modifier.weight(1f)
            )

            // Requirement 2: Balance (Bold Black / onSurface)
            SummaryColumn(
                label = "Balance",
                amountText = money(balance),
                amountColor = MaterialTheme.colorScheme.onSurface, // Bold black
                isBold = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryColumn(
    label: String,
    amountText: String,
    amountColor: Color,
    modifier: Modifier = Modifier,
    isBold: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = amountText,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
            color = amountColor
        )
    }
}