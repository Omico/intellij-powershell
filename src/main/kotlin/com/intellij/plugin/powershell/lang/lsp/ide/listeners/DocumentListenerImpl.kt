/**
 * adopted from https://github.com/gtache/intellij-lsp
 */
package com.intellij.plugin.powershell.lang.lsp.ide.listeners

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener

class DocumentListenerImpl : LSPEditorListener(), DocumentListener {
    /**
     * Called before the text of the document is changed.
     *
     * @param event the event containing the information about the change.
     */
    override fun beforeDocumentChange(event: DocumentEvent) {}

    /**
     * Called after the text of the document has been changed.
     *
     * @param event the event containing the information about the change.
     */
    override fun documentChanged(event: DocumentEvent) {
        if (checkManager()) {
            editorManager?.documentChanged(event)
        }
    }
}
