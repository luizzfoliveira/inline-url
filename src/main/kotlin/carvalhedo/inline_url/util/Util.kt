package carvalhedo.inline_url.util

import clojure.java.api.Clojure
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import carvalhedo.inline_url.actions.SearchUrlAction
import carvalhedo.inline_url.services.LineIndexImpl
import carvalhedo.inline_url.services.TogglePersistence
import carvalhedo.inline_url.util.ClojureUtil.LINE_INDEXER_NS
import carvalhedo.inline_url.util.ClojureUtil.SEARCH_URL_NS
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

internal object Util {
    fun buildLineIndexer(source: FileEditorManager, file: VirtualFile, urlPath: String) {
        val oldLoader = Thread.currentThread().contextClassLoader
        try {
            val loader = SearchUrlAction::class.java.classLoader
            Thread.currentThread().contextClassLoader = loader

            ClojureUtil.requireClojure(LINE_INDEXER_NS)
            ClojureUtil.requireClojure(SEARCH_URL_NS)

            if(file.extension?.equals("edn") == true) {
                val project = source.project
                val fileContent: String = source.selectedTextEditor?.document?.text as String
                val filePath: String = file.path
                val projectService = project.service<LineIndexImpl>()
                projectService.setLineIndexer(fileContent, filePath, urlPath)
            }
        } finally {
            Thread.currentThread().contextClassLoader = oldLoader
        }
    }

    fun promptForUrlPath(e: AnActionEvent): String? {
        val service = service<TogglePersistence>()
        val state = service.state
        val path: String? = Messages.showInputDialog(e.project, "Set Your Url Path\nUse Clojure-like vector of keywords\n(Ex.: [:a :b :routes])", "Set Path", Messages.getQuestionIcon(), state.path, null)
        if (path != null) {
            if (isValidPath(path)) {
                state.setUrlPath(path)
            }
        }
        return path
    }

    private fun isValidPath(urlPath: String): Boolean {
        val returnValue: Boolean
        val oldLoader = Thread.currentThread().contextClassLoader
        try {
            val loader = SearchUrlAction::class.java.classLoader
            Thread.currentThread().contextClassLoader = loader

            ClojureUtil.requireClojure(LINE_INDEXER_NS)

            returnValue = Clojure.`var`(LINE_INDEXER_NS, "valid-path?").invoke(urlPath) as Boolean
        } finally {
            Thread.currentThread().contextClassLoader = oldLoader
        }
        return returnValue
    }
}
