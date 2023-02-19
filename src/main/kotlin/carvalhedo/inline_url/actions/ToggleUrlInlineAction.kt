package carvalhedo.inline_url.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import carvalhedo.inline_url.services.TogglePersistence

class ToggleUrlInlineAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val service = project?.service<TogglePersistence>()
        val state = service?.state
        state?.toggleUrlInlay()
    }
}
