/**
 * adopted from https://github.com/gtache/intellij-lsp
 */
package com.intellij.plugin.powershell.lang.lsp.ide

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.plugin.powershell.lang.PowerShellLanguage
import com.intellij.plugin.powershell.lang.lsp.ide.listeners.DocumentListenerImpl
import com.intellij.plugin.powershell.lang.lsp.ide.listeners.EditorMouseListenerImpl
import com.intellij.plugin.powershell.lang.lsp.ide.listeners.EditorMouseMotionListenerImpl
import com.intellij.plugin.powershell.lang.lsp.ide.listeners.SelectionListenerImpl
import com.intellij.plugin.powershell.lang.lsp.languagehost.LanguageServerEndpoint
import com.intellij.plugin.powershell.lang.lsp.languagehost.ServerOptions
import com.intellij.plugin.powershell.lang.lsp.util.DocumentUtils.offsetToLSPPos
import com.intellij.plugin.powershell.lang.lsp.util.editorToURI
import com.intellij.plugin.powershell.lang.lsp.util.editorToURIString
import com.intellij.psi.PsiDocumentManager
import org.eclipse.lsp4j.CompletionList
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.TextDocumentContentChangeEvent
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.TextDocumentItem
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.eclipse.lsp4j.TextDocumentSyncKind
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier
import java.net.URI
import java.util.Collections
import java.util.concurrent.TimeUnit

class EditorEventManager(private val project: Project, private val editor: Editor, private val mouseListener: EditorMouseListenerImpl,
                         private val mouseMotionListener: EditorMouseMotionListenerImpl, private val documentListener: DocumentListenerImpl,
                         private val selectionListener: SelectionListenerImpl, private val requestManager: LSPRequestManager,
                         private val serverOptions: ServerOptions, private val languageServerEndpoint: LanguageServerEndpoint) {
  private val LOG: Logger = Logger.getInstance(javaClass)
  private var isOpen: Boolean = false
  private val identifier = TextDocumentIdentifier(editorToURIString(editor))
  private var version: Int = -1
  private val changesParams = DidChangeTextDocumentParams(VersionedTextDocumentIdentifier(), Collections.singletonList(TextDocumentContentChangeEvent()))
  private val syncKind = serverOptions.syncKind

  private val completionTriggers = if (serverOptions.completionProvider?.triggerCharacters != null)
    serverOptions.completionProvider.triggerCharacters?.filter { c -> "." != c }
  else emptySet<String>()
  private val signatureTriggers = if (serverOptions.signatureHelpProvider.triggerCharacters != null)
    serverOptions.signatureHelpProvider.triggerCharacters.toSet()
  else emptySet<String>()

  private var diagnosticsInfo: List<Diagnostic> = listOf()

  init {
    changesParams.textDocument.uri = identifier.uri
    editorToManager[editor] = this
  }

  fun getEditor(): Editor {
    return editor
  }

  fun getDiagnostics(): List<Diagnostic> = diagnosticsInfo

  companion object {
    private val uriToManager = mutableMapOf<URI, EditorEventManager>()
    private val editorToManager = mutableMapOf<Editor, EditorEventManager>()
    fun forEditor(editor: Editor): EditorEventManager? {
      return editorToManager[editor]
    }
  }

  fun registerListeners() {
    editor.addEditorMouseListener(mouseListener)
    editor.addEditorMouseMotionListener(mouseMotionListener)
    editor.document.addDocumentListener(documentListener)
    editor.selectionModel.addSelectionListener(selectionListener)
  }

  fun documentOpened() {
    if (!editor.isDisposed) {
      if (isOpen) {
        LOG.warn("Editor $editor was already open")
      } else {
        requestManager.didOpen(DidOpenTextDocumentParams(TextDocumentItem(identifier.uri, PowerShellLanguage.INSTANCE.id, incVersion(), editor.document.text)))
        isOpen = true
      }
    }
  }

  fun removeListeners() {
    editor.removeEditorMouseMotionListener(mouseMotionListener)
    editor.document.removeDocumentListener(documentListener)
    editor.removeEditorMouseListener(mouseListener)
    editor.selectionModel.removeSelectionListener(selectionListener)
  }


  fun documentClosed() {
    if (isOpen) {
      requestManager.didClose(DidCloseTextDocumentParams(identifier))
      isOpen = false
      editorToManager.remove(editor)
      uriToManager.remove(editorToURI(editor))
    } else {
      LOG.warn("Editor ${identifier.uri} + was already closed")
    }
  }

  fun documentChanged(event: DocumentEvent) {
    if (!editor.isDisposed) {
      if (event.document == editor.document) {
        changesParams.textDocument.version = incVersion()
        when (syncKind) {
          TextDocumentSyncKind.None,
          TextDocumentSyncKind.Incremental -> {
            val changeEvent = changesParams.contentChanges[0]
            val newText = event.newFragment
            val offset = event.offset
            val newTextLength = event.newLength
            val lspPosition: Position = offsetToLSPPos(editor, offset)
            val startLine = lspPosition.line
            val startColumn = lspPosition.character
            val oldText = event.oldFragment

            //if text was deleted/replaced, calculate the end position of inserted/deleted text
            val (endLine, endColumn) = if (oldText.isNotEmpty()) {
              val line = startLine + StringUtil.countNewLines(oldText)
              val oldLines = oldText.toString().split('\n')
              val oldTextLength = if (oldLines.isEmpty()) 0 else oldLines.last().length
              val column = if (oldLines.size == 1) startColumn + oldTextLength else oldTextLength
              Pair(line, column)
            } else Pair(startLine, startColumn) //if insert or no text change, the end position is the same
            val range = Range(Position(startLine, startColumn), Position(endLine, endColumn))
            changeEvent.range = range
            changeEvent.rangeLength = newTextLength
            changeEvent.text = newText.toString()
          }
          TextDocumentSyncKind.Full -> {
            changesParams.contentChanges[0].text = editor.document.text
          }
          else -> Unit
        }
        requestManager.didChange(changesParams)
      } else {
        LOG.error("Wrong document for the EditorEventManager")
      }
    }
  }

  private fun incVersion(): Int {
    version++
    return version - 1
  }

  fun completion(pos: Position): CompletionList {
    val request = requestManager.completion(TextDocumentPositionParams(identifier, pos))
    val result = CompletionList()
    if (request == null) return result
    return try {
      val res = request.get(500, TimeUnit.MILLISECONDS)
      if (res != null) {
        if (res.isLeft) {
          result.items = res.left
        } else if (res.isRight) {
          result.setIsIncomplete(res.right.isIncomplete)
          result.items = res.right.items
        }
        result
      } else result
    } catch (e: Exception) {
      LOG.warn("Error on completion request: $e")
      result
    }
  }

  fun updateDiagnostics(diagnostics: List<Diagnostic>) {
    saveDiagnostics(diagnostics)
    val restartAnalyzerRunnable = Runnable {
      val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
      if (psiFile != null) DaemonCodeAnalyzer.getInstance(project).restart(psiFile)
    }
    if (ApplicationManager.getApplication().isDispatchThread) {
      restartAnalyzerRunnable.run()
    } else {
      ApplicationManager.getApplication().runReadAction {
        restartAnalyzerRunnable.run()
      }
    }
  }

  /**
   *
   * Saves diagnostics which will then be used by IDE code analyzer and shown in Editor on inspection run
   */
  private fun saveDiagnostics(diagnostics: List<Diagnostic>) {
    diagnosticsInfo = diagnostics
  }

}

val DEFAULT_DID_CHANGE_CONFIGURATION_PARAMS = DidChangeConfigurationParams(PowerShellLanguageServerSettingsWrapper(LanguageServerSettings()))

data class PowerShellLanguageServerSettingsWrapper(val Powershell: LanguageServerSettings)
data class LanguageServerSettings(val EnableProfileLoading: Boolean = true, val ScriptAnalysis: ScriptAnalysisSettings = ScriptAnalysisSettings(), val CodeFormatting: CodeFormattingSettings = CodeFormattingSettings())
data class CodeFormattingSettings(var NewLineAfterOpenBrace: Boolean = true)
data class ScriptAnalysisSettings(var Enabled: Boolean = true)
