/**
 * adopted from https://github.com/gtache/intellij-lsp
 */
package com.intellij.plugin.powershell.lang.lsp.client

import com.google.gson.JsonObject
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.plugin.powershell.lang.lsp.ide.EditorEventManager
import com.intellij.plugin.powershell.lang.lsp.languagehost.LanguageServerEndpoint
import com.intellij.plugin.powershell.lang.lsp.util.getTextEditor
import com.intellij.ui.GuiUtils
import org.eclipse.lsp4j.ApplyWorkspaceEditParams
import org.eclipse.lsp4j.ApplyWorkspaceEditResponse
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.MessageActionItem
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.PublishDiagnosticsParams
import org.eclipse.lsp4j.RegistrationParams
import org.eclipse.lsp4j.ShowMessageRequestParams
import org.eclipse.lsp4j.UnregistrationParams
import org.eclipse.lsp4j.jsonrpc.CompletableFutures
import org.eclipse.lsp4j.jsonrpc.Endpoint
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageServer
import java.io.File
import java.net.URI
import java.util.concurrent.CompletableFuture

class PSLanguageClientImpl(private val project: Project) : LanguageClient, Endpoint {
    private val LOG: Logger = Logger.getInstance(javaClass)
    private var server: LanguageServer? = null
    private var serverEndpoint: LanguageServerEndpoint? = null

    override fun notify(method: String?, parameter: Any?) {
        LOG.debug("Received notify \"$method\", $parameter from server.")
    }

    override fun request(method: String?, parameter: Any?): CompletableFuture<*> {
        LOG.debug("Received request \"$method\", $parameter from server.")
        if ("editor/openFile" == method) {
            return handleOpenFileRequest(parameter as? JsonObject)
        }
        return CompletableFuture<Any>()
    }

    private fun handleOpenFileRequest(parameter: JsonObject?): CompletableFuture<EditorCommandResponse> {
        val ok = EditorCommandResponse.OK
        val unsupported = EditorCommandResponse.Unsupported
        return CompletableFutures.computeAsync {
            if (parameter == null) return@computeAsync unsupported
//      val isPreview = parameter.get("preview")
            val filePath = parameter.get("filePath")?.asString
            val vFile = VfsUtil.findFileByIoFile(File(filePath), true)
            if (vFile == null || !vFile.exists()) {
                LOG.warn("File $filePath does not exist or invalid.")
                return@computeAsync unsupported
            }
            val descriptor = OpenFileDescriptor(project, vFile, 0)
            GuiUtils.invokeLaterIfNeeded(
                { FileEditorManager.getInstance(project).openTextEditor(descriptor, true) },
                ModalityState.NON_MODAL,
            )
            return@computeAsync ok
        }
    }

    fun connectServer(server: LanguageServer, serverEndpoint: LanguageServerEndpoint) {
        this.server = server
        this.serverEndpoint = serverEndpoint
    }

    /**
     * The workspace/applyEdit request is sent from the server to the client to modify resource on the client side.
     */
    override fun applyEdit(params: ApplyWorkspaceEditParams): CompletableFuture<ApplyWorkspaceEditResponse> {
        throw UnsupportedOperationException()
    }

    /**
     * The client/registerCapability request is sent from the server to the client
     * to register for a new capability on the client side.
     * Not all clients need to support dynamic capability registration.
     * A client opts in via the ClientCapabilities.dynamicRegistration property
     */
    override fun registerCapability(params: RegistrationParams): CompletableFuture<Void> {
        throw UnsupportedOperationException()
    }

    /**
     * The client/unregisterCapability request is sent from the server to the client
     * to unregister a previously register capability.
     */
    override fun unregisterCapability(params: UnregistrationParams): CompletableFuture<Void> {
        throw UnsupportedOperationException()
    }

    /**
     * The telemetry notification is sent from the server to the client to ask
     * the client to log a telemetry event.
     */
    override fun telemetryEvent(`object`: Any) {
    }

    /**
     * Diagnostics notifications are sent from the server to the client to
     * signal results of validation runs.
     */
    override fun publishDiagnostics(diagnostics: PublishDiagnosticsParams) {
        val uri = diagnostics.uri
        updateDiagnostics(uri, diagnostics.diagnostics)
    }

    private fun updateDiagnostics(uri: String, diagnostics: List<Diagnostic>) {
        val file = VfsUtil.findFileByIoFile(File(URI(uri)), false) ?: return
        val editor = getTextEditor(file, project) ?: return
        val manager = EditorEventManager.forEditor(editor) ?: return
        manager.updateDiagnostics(diagnostics)
    }

    /**
     * The show message notification is sent from a server to a client to ask
     * the client to display a particular message in the user interface.
     */
    override fun showMessage(messageParams: MessageParams) {
    }

    /**
     * The show message request is sent from a server to a client to ask the
     * client to display a particular message in the user interface. In addition
     * to the show message notification the request allows to pass actions and
     * to wait for an answer from the client.
     */
    override fun showMessageRequest(requestParams: ShowMessageRequestParams): CompletableFuture<MessageActionItem> {
        TODO("Not implemented")
    }

    /**
     * The log message notification is send from the server to the client to ask
     * the client to log a particular message.
     */
    override fun logMessage(message: MessageParams) {
    }

    private enum class EditorCommandResponse {
        Unsupported,
        OK
    }
}
