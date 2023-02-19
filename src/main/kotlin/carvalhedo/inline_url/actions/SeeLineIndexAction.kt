package carvalhedo.inline_url.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import carvalhedo.inline_url.services.LineIndexImpl

class SeeLineIndexAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project as Project
        val lineIndexService = project.service<LineIndexImpl>()
        Messages.showMessageDialog(e.project, lineIndexService.getLineIndexer().toString(), "Debug", Messages.getInformationIcon())
    }
}