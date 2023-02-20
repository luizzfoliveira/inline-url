package carvalhedo.inline_url.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import carvalhedo.inline_url.services.TogglePersistence
import carvalhedo.inline_url.util.Util.buildLineIndexer

class FileOpenedListener: FileEditorManagerListener {
    private var lineChangedListener: LineChangedListener? = null

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val state = service<TogglePersistence>().state
        if (state.isOn()){
            if (lineChangedListener === null) {
                lineChangedListener = LineChangedListener()
                EditorFactory.getInstance().eventMulticaster.addCaretListener(lineChangedListener!!)
            }
            buildLineIndexer(source, file, state.getUrlPath())
        }
    }
}