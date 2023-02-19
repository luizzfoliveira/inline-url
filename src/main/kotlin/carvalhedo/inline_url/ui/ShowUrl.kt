package carvalhedo.inline_url.ui

import com.intellij.openapi.editor.Editor

object ShowUrl {

    fun show(editor: Editor, url: String) {
        val caretModel = editor.caretModel
        editor.inlayModel.addAfterLineEndElement(
                caretModel.visualLineStart,
                false,
                Renderer(url))
    }
}