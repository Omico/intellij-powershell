package com.intellij.plugin.powershell.lang.lsp.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.plugin.powershell.ide.run.join
import java.io.File
import java.net.URI

fun editorToURIString(editor: Editor): String? {
    val file = FileDocumentManager.getInstance().getFile(editor.document) ?: return null
    return VfsUtil.toUri(File(file.path)).toString()
}

fun editorToURI(editor: Editor): URI? {
    val file = FileDocumentManager.getInstance().getFile(editor.document) ?: return null
    return VfsUtil.toUri(File(file.path))
}

fun getTextEditor(file: VirtualFile, project: Project): Editor? {
    return FileEditorManager.getInstance(project).getAllEditors(file).filterIsInstance<TextEditor>()
        .map { e -> e.editor }.firstOrNull()
}

fun isRemotePath(docPath: String?) = docPath != null && docPath.contains(REMOTE_FILES_DIR_PREFIX)

private val REMOTE_FILES_DIR_PREFIX = join(System.getProperty("java.io.tmpdir"), "PSES-")
