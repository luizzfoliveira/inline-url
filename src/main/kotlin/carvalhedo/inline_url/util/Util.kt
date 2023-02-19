package carvalhedo.inline_url.util

import clojure.java.api.Clojure
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import carvalhedo.inline_url.actions.SearchUrlAction
import carvalhedo.inline_url.services.LineIndexImpl
import carvalhedo.inline_url.util.ClojureUtil.LINE_INDEXER_NS
import carvalhedo.inline_url.util.ClojureUtil.READ_EDN_NS

internal object Util {
    fun buildLineIndexer(source: FileEditorManager, file: VirtualFile, urlPath: String) {
        val oldLoader = Thread.currentThread().contextClassLoader
        try {
            val loader = SearchUrlAction::class.java.classLoader
            Thread.currentThread().contextClassLoader = loader

            ClojureUtil.requireClojure(LINE_INDEXER_NS)
            ClojureUtil.requireClojure(READ_EDN_NS)

            val fileName: String = file.name

            if(Clojure.`var`(READ_EDN_NS, "is-edn?").invoke(fileName) as Boolean) {
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
}
