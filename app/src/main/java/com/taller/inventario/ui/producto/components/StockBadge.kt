package com.taller.inventario.ui.producto.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taller.inventario.data.local.EstadoInventario
import com.taller.inventario.ui.theme.StockEmptyContainer
import com.taller.inventario.ui.theme.StockEmptyOnContainer
import com.taller.inventario.ui.theme.StockLowContainer
import com.taller.inventario.ui.theme.StockLowOnContainer
import com.taller.inventario.ui.theme.StockNormalContainer
import com.taller.inventario.ui.theme.StockNormalOnContainer

/**
 * Insignia visual que refleja el estado de stock con accesibilidad,
 * combinando color de alto contraste con transición animada suave,
 * icono temático y texto explícito.
 */
@Composable
fun StockBadge(
    estado: EstadoInventario,
    modifier: Modifier = Modifier
) {
    val (targetBgColor, targetTextColor, icon, label) = when (estado) {
        EstadoInventario.DISPONIBLE -> {
            Quadruple(
                StockNormalContainer,
                StockNormalOnContainer,
                Icons.Default.CheckCircle,
                "Disponible"
            )
        }
        EstadoInventario.BAJO_STOCK -> {
            Quadruple(
                StockLowContainer,
                StockLowOnContainer,
                Icons.Default.WarningAmber,
                "Bajo Stock"
            )
        }
        EstadoInventario.AGOTADO -> {
            Quadruple(
                StockEmptyContainer,
                StockEmptyOnContainer,
                Icons.Default.ErrorOutline,
                "Agotado"
            )
        }
    }

    val animatedBgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(durationMillis = 300),
        label = "StockBadgeBgAnimation"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = tween(durationMillis = 300),
        label = "StockBadgeTextAnimation"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(animatedBgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = animatedTextColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            color = animatedTextColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
