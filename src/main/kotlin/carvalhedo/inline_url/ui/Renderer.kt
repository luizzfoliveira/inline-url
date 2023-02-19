package carvalhedo.inline_url.ui

import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Graphics
import java.awt.Rectangle

class Renderer(private val url: String): EditorCustomElementRenderer {
    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        return 50
    }

    override fun paint(inlay: Inlay<*>, graphics: Graphics, rectangle: Rectangle, textAttributes: TextAttributes) {
        val fontMetrics = graphics.fontMetrics
        val x: Int = rectangle.x + rectangle.width / 2
        val y: Int = rectangle.y + (rectangle.height - fontMetrics.height) / 2 + fontMetrics.ascent
        graphics.color = textAttributes.foregroundColor
        graphics.drawString(url, x, y)
    }
}