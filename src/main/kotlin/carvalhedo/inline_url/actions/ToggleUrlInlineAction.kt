package carvalhedo.inline_url.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import carvalhedo.inline_url.services.TogglePersistence
import carvalhedo.inline_url.util.Util.promptForUrlPath
import com.intellij.openapi.fileEditor.FileEditorManager

class ToggleUrlInlineAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val service = service<TogglePersistence>()
        val state = service.state
        val project = e.project
        if (project != null) {
            val editor = FileEditorManager.getInstance(project)
            if (state.isOn()) {
                val selectedEditor = editor.selectedTextEditor
                selectedEditor?.inlayModel?.getAfterLineEndElementsInRange(0, selectedEditor.document.textLength)?.forEach { it.dispose() }
            } else {
                if (state.path.isEmpty()) {
                    promptForUrlPath(e)
                }
            }
        }
        state.toggleUrlInlay()
    }
}
