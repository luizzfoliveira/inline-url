package carvalhedo.inline_url.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.Messages
import carvalhedo.inline_url.services.TogglePersistence

class ChangePathAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val service = service<TogglePersistence>()
        val state = service.state
        val path: String? = Messages.showInputDialog(e.project, "Set Your Url Path\nUse Clojure-like vector of keywords\n(Ex.: [:a :b :routes])", "Set Path", Messages.getQuestionIcon(), state.path, null)
        if (path != null) {
            state.setUrlPath(path)
        }
    }
}