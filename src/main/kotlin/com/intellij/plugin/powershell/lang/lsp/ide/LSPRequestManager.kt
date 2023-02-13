/**
 * adopted from https://github.com/gtache/intellij-lsp
 */
package com.intellij.plugin.powershell.lang.lsp.ide

import com.intellij.openapi.diagnostic.Logger
import com.intellij.plugin.powershell.lang.lsp.client.PSLanguageClientImpl
import com.intellij.plugin.powershell.lang.lsp.languagehost.LanguageServerEndpoint
import com.intellij.plugin.powershell.lang.lsp.languagehost.ServerStatus
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionList
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.TextDocumentPositionParams
import org.eclipse.lsp4j.TextDocumentSyncOptions
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import java.util.concurrent.CompletableFuture

class LSPRequestManager(
    private val serverEndpoint: LanguageServerEndpoint,
    private val languageServer: LanguageServer,
    private val client: PSLanguageClientImpl,
    private val capabilities: ServerCapabilities,
) {

    private val LOG: Logger = Logger.getInstance(javaClass)
    private val documentSyncOptions: TextDocumentSyncOptions? =
        if (capabilities.textDocumentSync.isRight) capabilities.textDocumentSync.right else null
    private val workspaceService: WorkspaceService = languageServer.workspaceService
    private val documentService: TextDocumentService = languageServer.textDocumentService

    private fun checkStatus(): Boolean = serverEndpoint.getStatus() == ServerStatus.STARTED

    fun didClose(params: DidCloseTextDocumentParams) {
        if (checkStatus()) {
            try {
                if (documentSyncOptions == null || documentSyncOptions.openClose) documentService.didClose(params)
            } catch (e: Exception) {
                crashed(e)
            }
        }
    }

    fun didOpen(params: DidOpenTextDocumentParams) {
        if (checkStatus()) {
            try {
                if (documentSyncOptions == null || documentSyncOptions.openClose) documentService.didOpen(params)
            } catch (e: Exception) {
                crashed(e)
            }
        }
    }

    private fun crashed(e: Exception) {
        LOG.warn(e)
        serverEndpoint.crashed(e)
    }

    fun didChange(params: DidChangeTextDocumentParams) {
        if (checkStatus()) {
            try {
                if (documentSyncOptions == null || documentSyncOptions.change != null) documentService.didChange(params)
            } catch (e: Exception) {
                crashed(e)
            }
        }
    }

    fun completion(params: TextDocumentPositionParams): CompletableFuture<Either<List<CompletionItem>, CompletionList>>? {
        if (checkStatus()) {
            try {
                if (capabilities.completionProvider != null) return documentService.completion(params)
            } catch (e: Exception) {
                crashed(e)
            }
        }
        return null
    }
}
