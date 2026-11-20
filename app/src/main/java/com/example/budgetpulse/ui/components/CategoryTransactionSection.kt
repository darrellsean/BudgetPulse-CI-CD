package com.example.budgetpulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.budgetpulse.data.model.CategoryIcons
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.data.model.TransactionItem
import com.example.budgetpulse.ui.theme.LocalAppCurrency
import com.example.budgetpulse.ui.viewmodel.CategoryTransactionGroup
import com.example.budgetpulse.ui.viewmodel.SummaryPeriod
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

private val IncomeGreen = Color(0xFF43A047)
private val ExpenseRed = Color(0xFFE53935)

private fun money(symbol: String, value: Double) =
    "$symbol${String.format(Locale.ENGLISH, "%,.2f", value)}"

/** Daily / Weekly / Monthly selector shown above the summary card. */
@Composable
fun SummaryPeriodSelector(
    selected: SummaryPeriod,
    onSelected: (SummaryPeriod) -> Unit,
    rangeLabel: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SummaryPeriod.entries.forEachIndexed { index, period ->
                SegmentedButton(
                    selected = selected == period,
                    onClick = { onSelected(period) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = SummaryPeriod.entries.size)
                ) { Text(period.label) }
            }
        }
        Text(
            text = rangeLabel,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Expense / Income switch above the category list. */
@Composable
fun CategoryListHeader(
    listType: CategoryType,
    onListTypeChange: (CategoryType) -> Unit,
    visibleTotal: Double,
    modifier: Modifier = Modifier
) {
    val symbol = LocalAppCurrency.current.symbol

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "By Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = money(symbol, visibleTotal),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (listType == CategoryType.INCOME) IncomeGreen else ExpenseRed
            )
        }

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = listType == CategoryType.EXPENSE,
                onClick = { onListTypeChange(CategoryType.EXPENSE) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) { Text("Expense") }
            SegmentedButton(
                selected = listType == CategoryType.INCOME,
                onClick = { onListTypeChange(CategoryType.INCOME) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) { Text("Income") }
        }

        Text(
            text = "Tap a transaction to edit or delete it",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** One category card listing every transaction (with its description) under it. */
@Composable
fun CategoryTransactionGroupCard(
    group: CategoryTransactionGroup,
    sectionTotal: Double,
    onTransactionClick: (TransactionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val symbol = LocalAppCurrency.current.symbol
    val isIncome = group.type == CategoryType.INCOME
    val accent = if (isIncome) IncomeGreen else ExpenseRed
    val share = if (sectionTotal > 0) (group.total / sectionTotal).toFloat() else 0f

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Category header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(group.colorHex))
                ) {
                    Icon(
                        imageVector = CategoryIcons.getIcon(group.iconName),
                        contentDescription = group.categoryName,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.categoryName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${group.transactions.size} transaction${if (group.transactions.size == 1) "" else "s"} • ${(share * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = (if (isIncome) "+" else "-") + money(symbol, group.total),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = accent
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { share },
                color = accent,
                trackColor = accent.copy(alpha = 0.12f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Individual transactions with description
            group.transactions.forEachIndexed { index, transaction ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }
                CategoryTransactionRow(
                    transaction = transaction,
                    symbol = symbol,
                    accent = accent,
                    onClick = { onTransactionClick(transaction) }
                )
            }
        }
    }
}

@Composable
private fun CategoryTransactionRow(
    transaction: TransactionItem,
    symbol: String,
    accent: Color,
    onClick: () -> Unit
) {
    val dateLabel = transaction.date.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClickLabel = "Edit transaction") { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.note.ifBlank { "No description" },
                style = MaterialTheme.typography.bodyMedium,
                color = if (transaction.note.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$dateLabel • ${transaction.formattedTime}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = (if (transaction.type == CategoryType.INCOME) "+" else "-") + money(symbol, abs(transaction.amount)),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = accent
        )
    }
}