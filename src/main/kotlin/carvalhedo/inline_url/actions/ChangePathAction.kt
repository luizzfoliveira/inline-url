package carvalhedo.inline_url.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import carvalhedo.inline_url.util.Util.promptForUrlPath

class ChangePathAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        promptForUrlPath(e)
    }
}