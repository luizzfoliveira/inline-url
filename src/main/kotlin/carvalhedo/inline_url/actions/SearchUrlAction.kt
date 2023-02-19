package carvalhedo.inline_url.actions

import clojure.java.api.Clojure
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import carvalhedo.inline_url.util.ClojureUtil.READ_EDN_NS
import carvalhedo.inline_url.util.ClojureUtil.requireClojure


class SearchUrlAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val oldLoader = Thread.currentThread().contextClassLoader
        try {
            val loader = SearchUrlAction::class.java.classLoader
            Thread.currentThread().contextClassLoader = loader

            requireClojure(READ_EDN_NS)

            val vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE)
            val fileName: String = vFile?.name as String

            if(Clojure.`var`(READ_EDN_NS, "is-edn?").invoke(fileName) as Boolean) {
                val fileContent: String = e.project?.let { FileEditorManager.getInstance(it).selectedTextEditor?.document?.text } as String
                val url: String? = Messages.showInputDialog(e.project, "Url:", "Search Your Url", Messages.getQuestionIcon())
                if (url != null) {
                    val handlers: String = Clojure.`var`(READ_EDN_NS, "search-url").invoke(fileContent, url) as String
                    Messages.showMessageDialog(e.project, handlers, "Handlers For That Url", Messages.getInformationIcon())
                }
            } else {
                Messages.showMessageDialog(e.project, "Wrong file type.\nPlease try again in a *.edn file", "Error", Messages.getInformationIcon())
            }
        } finally {
            Thread.currentThread().contextClassLoader = oldLoader
        }
    }
}