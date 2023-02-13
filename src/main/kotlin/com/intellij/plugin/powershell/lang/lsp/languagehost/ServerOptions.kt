/**
 * adopted from https://github.com/gtache/intellij-lsp
 */
package com.intellij.plugin.powershell.lang.lsp.languagehost

import org.eclipse.lsp4j.CodeLensOptions
import org.eclipse.lsp4j.CompletionOptions
import org.eclipse.lsp4j.DocumentLinkOptions
import org.eclipse.lsp4j.DocumentOnTypeFormattingOptions
import org.eclipse.lsp4j.ExecuteCommandOptions
import org.eclipse.lsp4j.SignatureHelpOptions
import org.eclipse.lsp4j.TextDocumentSyncKind

class ServerOptions(
    internal val syncKind: TextDocumentSyncKind?,
    internal val completionProvider: CompletionOptions?,
    internal val signatureHelpProvider: SignatureHelpOptions,
    internal val codeLensProvider: CodeLensOptions?,
    internal val documentOnTypeFormattingProvider: DocumentOnTypeFormattingOptions?,
    internal val documentLinkProvider: DocumentLinkOptions?,
    internal val executeCommandProvider: ExecuteCommandOptions?,
)
