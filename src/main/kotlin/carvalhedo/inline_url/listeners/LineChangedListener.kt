package carvalhedo.inline_url.listeners

import clojure.java.api.Clojure
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import carvalhedo.inline_url.actions.SearchUrlAction
import carvalhedo.inline_url.services.LineIndexImpl
import carvalhedo.inline_url.services.TogglePersistence
import carvalhedo.inline_url.ui.ShowUrl
import carvalhedo.inline_url.util.ClojureUtil
import org.jetbrains.annotations.NotNull


class LineChangedListener: CaretListener {
    override fun caretPositionChanged(@NotNull event: CaretEvent) {
        val state = event.editor.project?.service<TogglePersistence>()?.state
        if (state != null) {
            if (state.isOn()) {
                val currentLine: Int = event.caret?.logicalPosition?.line ?: -1

                val oldLoader = Thread.currentThread().contextClassLoader
                try {
                    val loader = SearchUrlAction::class.java.classLoader
                    Thread.currentThread().contextClassLoader = loader

                    ClojureUtil.requireClojure(ClojureUtil.READ_EDN_NS)

                    val project = event.editor.project as Project

                    if (hasLineChanged(event)) {
                        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(event.editor.document)
                        val vFile = psiFile!!.originalFile.virtualFile
                        val filePath = vFile.path

                        val editor = FileEditorManager.getInstance(project).selectedTextEditor

                        editor?.inlayModel?.getAfterLineEndElementsForLogicalLine(event.oldPosition.line)?.forEach { it.dispose() }

                        val lineIndexerService = project.service<LineIndexImpl>()
                        val lineIndexer = lineIndexerService.getLineIndexer()
                        ClojureUtil.requireClojure(ClojureUtil.LINE_INDEXER_NS)
                        if (Clojure.`var`(ClojureUtil.LINE_INDEXER_NS, "line-present-in-indexer?").invoke(lineIndexer, filePath, currentLine) as Boolean) {
                            val url: String = Clojure.`var`(ClojureUtil.LINE_INDEXER_NS, "get-url-from-line").invoke(lineIndexer, filePath, currentLine) as String
                            if (editor != null) {
                                ShowUrl.show(editor, url)
                            }
                        }
                    }
                } finally {
                    Thread.currentThread().contextClassLoader = oldLoader
                }
            }
        }
    }

    private fun hasLineChanged(event: CaretEvent): Boolean {
        return event.oldPosition.line != event.newPosition.line
    }
}