package com.dara.helper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val DarkSurface = Color(0xFF121212)

@Composable
fun PackageList(onPushClicked: (String) -> Unit) {
    val samplePackages = listOf("payload-pkg", "libcurl-base", "python-runtime")
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(samplePackages) { pkg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "$pkg.tar.gz", color = Color.White)
                        Text(text = "Splits: .ba | .lm  (41.2 MB)", color = Color.Gray, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { onPushClicked(pkg) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("PUSH", color = Color.White)
                    }
                }
            }
        }
    }
}
