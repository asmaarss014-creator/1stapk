package com.dara.helper

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dara.helper.terminal.TermuxTerminalManager
import com.dara.helper.ui.PackageList
import com.dara.helper.ui.TermuxConsoleDrawer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private lateinit var terminalManager: TermuxTerminalManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val workingDir = filesDir.absolutePath
        terminalManager = TermuxTerminalManager(workingDir)

        setContent {
            var sessionState by remember { mutableStateOf<com.termux.terminal.TerminalSession?>(null) }
            var suggestedUpdate by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                sessionState = terminalManager.initializeSession {
                    // Refreshes view on terminal output change
                }
                
                val update = sendTelemetry("http://10.0.2.2:5000", Build.SERIAL ?: "DEVICE_001", "1.0.0")
                if (update != null) {
                    suggestedUpdate = update
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                Text(
                    text = "DARA HELPER TOOL",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (suggestedUpdate != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "ADMIN UPDATE SUGGESTION: $suggestedUpdate",
                            color = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    PackageList(onPushClicked = { pkgName ->
                        terminalManager.sendCommand("echo '[EXEC] Merging $pkgName.ba and $pkgName.lm...'")
                        terminalManager.sendCommand("cat $workingDir/$pkgName.ba $workingDir/$pkgName.lm > $workingDir/$pkgName.tar.gz")
                        terminalManager.sendCommand("echo '[EXTR] Unpacking payload archive...'")
                        terminalManager.sendCommand("tar -xzf $workingDir/$pkgName.tar.gz -C $workingDir/")
                        terminalManager.sendCommand("rm $workingDir/$pkgName.tar.gz")
                        terminalManager.sendCommand("echo '[OK] Operation complete.'")
                    })
                }

                TermuxConsoleDrawer(terminalSession = sessionState)
            }
        }
    }

    private suspend fun sendTelemetry(adminServerUrl: String, deviceId: String, currentVer: String): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$adminServerUrl/api/telemetry")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val jsonBody = JSONObject().apply {
                put("device_id", deviceId)
                put("device_model", "${Build.MANUFACTURER} ${Build.MODEL}")
                put("installed_pkg_ver", currentVer)
                put("timestamp", System.currentTimeMillis())
            }

            conn.outputStream.use { it.write(jsonBody.toString().toByteArray()) }

            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().readText()
                val jsonResponse = JSONObject(response)
                return@withContext jsonResponse.optString("suggested_update", null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}
