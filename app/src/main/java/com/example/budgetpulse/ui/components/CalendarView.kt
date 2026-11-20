package com.example.budgetpulse.ui.components

import java.util.Locale
import com.example.budgetpulse.ui.theme.LocalAppCurrency
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgetpulse.data.model.DailySummary
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarView(
    selectedMonth: YearMonth,
    selectedDate: LocalDate,
    dailySummaries: Map<LocalDate, DailySummary>,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Month Switcher Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month"
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clickable { onTodayClick() }
                    ) {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Days of the week (Mon to Sun)
            val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calculate grid dates matrix
            val calendarDates = remember(selectedMonth) {
                getCalendarDatesForMonth(selectedMonth)
            }

            // Render Calendar Grid in 7-item Rows
            val rows = calendarDates.chunked(7)
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rows.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { date ->
                            val isSelected = date == selectedDate
                            val isCurrentMonth = YearMonth.from(date) == selectedMonth
                            val summary = dailySummaries[date]

                            CalendarDayCell(
                                date = date,
                                isCurrentMonth = isCurrentMonth,
                                isSelected = isSelected,
                                dailySummary = summary,
                                onClick = { onDateSelected(date) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    dailySummary: DailySummary?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Requirement 1: Currently selected day highlighted with a solid yellow rounded rectangle background
    val backgroundColor = if (isSelected) {
        Color(0xFFFFD54F) // Solid yellow
    } else {
        Color.Transparent
    }

    val dateTextColor = when {
        isSelected -> Color.Black
        isCurrentMonth -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(horizontal = 2.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                fontWeight = if (isSelected || date == LocalDate.now()) FontWeight.ExtraBold else FontWeight.Medium,
                color = dateTextColor
            )

            // Requirement 1: Dates containing transaction records display daily total amount directly below date number
            if (dailySummary != null && (dailySummary.totalIncome > 0 || dailySummary.totalExpense > 0)) {
                val isPositive = dailySummary.netTotal >= 0
                val currency = LocalAppCurrency.current.symbol
                val amountText = if (isPositive) {
                    if (dailySummary.totalIncome > 0) "+$currency${dailySummary.totalIncome.toInt()}" else "${currency}0"
                } else {
                    "-$currency${dailySummary.totalExpense.toInt()}"
                }

                val amountColor = if (isSelected) {
                    if (isPositive) Color(0xFF2E7D32) else Color(0xFFC62828)
                } else {
                    if (isPositive) Color(0xFF43A047) else Color(0xFFE53935)
                }

                Text(
                    text = amountText,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    fontWeight = FontWeight.Bold,
                    color = amountColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

private fun getCalendarDatesForMonth(yearMonth: YearMonth): List<LocalDate> {
    val firstOfMonth = yearMonth.atDay(1)
    val dayOfWeek = firstOfMonth.dayOfWeek.value // Mon = 1, Sun = 7
    val daysBefore = dayOfWeek - 1

    val startDate = firstOfMonth.minusDays(daysBefore.toLong())
    val totalDays = 35 // 5 rows * 7 days

    return (0 until totalDays).map { startDate.plusDays(it.toLong()) }
}