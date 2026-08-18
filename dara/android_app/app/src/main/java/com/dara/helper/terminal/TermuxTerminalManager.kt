package com.dara.helper.terminal

import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient

class TermuxTerminalManager(private val workingDir: String) {

    lateinit var session: TerminalSession
        private set

    fun initializeSession(onLogUpdate: () -> Unit): TerminalSession {
        val shellExecutable = "/system/bin/sh"
        val environment = arrayOf(
            "HOME=$workingDir",
            "PATH=/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin",
            "TERM=xterm-256color"
        )

        val client = object : TerminalSessionClient {
            override fun onTextChanged(changedSession: TerminalSession) {
                onLogUpdate()
            }
            override fun onTitleChanged(changedSession: TerminalSession) {}
            override fun onSessionFinished(finishedSession: TerminalSession) {}
            override fun onClipboardText(session: TerminalSession, text: String) {}
            override fun onBell(session: TerminalSession) {}
            override fun onColorsChanged(session: TerminalSession) {}
            override fun onTerminalCursorStateChange(state: Boolean) {}
            override fun setTerminalShellPid(session: TerminalSession, pid: Int) {}
            override fun getTerminalCursorStyle(): Int = 0
        }

        session = TerminalSession(
            shellExecutable,
            workingDir,
            arrayOf("-l"),
            environment,
            TerminalSession.FAIL_SAFE_SHELL_PACKAGE,
            client
        )
        
        return session
    }

    fun sendCommand(command: String) {
        if (::session.isInitialized) {
            session.write("$command
")
        }
    }
}
