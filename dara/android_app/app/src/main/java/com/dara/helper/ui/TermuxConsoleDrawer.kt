package com.dara.helper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.termux.terminal.TerminalSession
import com.termux.view.TerminalView

@Composable
fun TermuxConsoleDrawer(
    terminalSession: TerminalSession?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color.Black, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .padding(8.dp)
    ) {
        Text(
            text = "TERMUX ENGINE CONSOLE",
            color = Color.Gray,
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (terminalSession != null) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    TerminalView(context, null).apply {
                        attachCurrentSession(terminalSession)
                        setTextSize(30)
                        setBackgroundColor(0xFF000000.toInt())
                        keepScreenOn = true
                    }
                },
                update = { view ->
                    if (view.currentSession != terminalSession) {
                        view.attachCurrentSession(terminalSession)
                    }
                }
            )
        }
    }
}
