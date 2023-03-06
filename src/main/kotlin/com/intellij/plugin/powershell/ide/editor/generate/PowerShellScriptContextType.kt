package com.intellij.plugin.powershell.ide.editor.generate

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.plugin.powershell.PowerShellFileType
import com.intellij.plugin.powershell.psi.PowerShellBlockBody
import com.intellij.plugin.powershell.psi.PowerShellCommandName
import com.intellij.plugin.powershell.psi.PowerShellComponent
import com.intellij.plugin.powershell.psi.impl.PowerShellFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace

abstract class PowerShellScriptContextType(presentableName: String) : TemplateContextType(presentableName) {
    protected open fun isInContext(place: PsiElement) = true

    final override fun isInContext(templateActionContext: TemplateActionContext): Boolean =
        when (val place = templateActionContext.file.findElementAt(templateActionContext.startOffset)) {
            null,
            is PsiWhiteSpace,
            is PsiComment,
            -> false
            else -> templateActionContext.file is PowerShellFile && isInContext(place)
        }

    final override fun createHighlighter(): SyntaxHighlighter? =
        SyntaxHighlighterFactory.getSyntaxHighlighter(PowerShellFileType.INSTANCE, null, null)
}

open class PowerShellLanguageContext(
    presentableName: String = "PowerShell",
) : PowerShellScriptContextType(presentableName)

class PowerShellDeclarationContext : PowerShellLanguageContext("Declaration") {

    override fun isInContext(place: PsiElement): Boolean = isDeclarationContext(place)

    private fun isDeclarationContext(place: PsiElement): Boolean {
        val psiElement = place.context ?: return false
        return psiElement.context is PowerShellComponent || psiElement.context is PowerShellFile
    }
}

class PowerShellStatementContext : PowerShellLanguageContext("Statement") {

    override fun isInContext(place: PsiElement): Boolean = isStatementContext(place)

    private fun isStatementContext(place: PsiElement): Boolean {
        val psiElement = place.context ?: return false
        return when (val elementContext = psiElement.context) {
            is PowerShellCommandName -> true
            else -> elementContext is PowerShellBlockBody || elementContext is PowerShellFile
        }
    }
}
