package com.kunto.smartrecovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import com.kunto.smartrecovery.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import smartrecovery.composeapp.generated.resources.Res
import smartrecovery.composeapp.generated.resources.force_peak
import smartrecovery.composeapp.generated.resources.quick_menu


@Composable
fun <T : Any> DropdownMenu(options: List<T>, onClick: (T) -> Unit ) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Button(
            modifier = Modifier.width(150.dp),
            onClick = { expanded = !expanded }
        ) {
            IconButton {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                Text(stringResource(Res.string.quick_menu))
                Box(Modifier)
            }
        }


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onClick(option)
                        expanded = !expanded
                    }
                )
            }
        }
    }
}