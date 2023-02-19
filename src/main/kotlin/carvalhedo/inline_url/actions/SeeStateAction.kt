package carvalhedo.inline_url.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import carvalhedo.inline_url.services.TogglePersistence
import carvalhedo.inline_url.services.ToggleService

class SeeStateAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project as Project
        val state = project.service<TogglePersistence>().state
        Messages.showMessageDialog(e.project, buildMessage(state), "Debug", Messages.getInformationIcon())
    }

    private fun buildMessage(state: ToggleService): String {
        return "isOn = " + state.isOn() + "\npath = " + state.getUrlPath()
    }
}