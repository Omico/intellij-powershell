package com.intellij.plugin.powershell.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.plugin.powershell.lang.resolve.PowerShellTypeUtil
import com.intellij.plugin.powershell.psi.PowerShellCallableReference
import com.intellij.plugin.powershell.psi.PowerShellCommandName
import com.intellij.plugin.powershell.psi.PowerShellExpression
import com.intellij.plugin.powershell.psi.types.PowerShellType
import com.intellij.psi.PsiElement

/**
 * Andrey 24/08/17.
 */
open class PowerShellCommandCallExpressionImpl(node: ASTNode) :
    PowerShellReferencePsiElementImpl(node),
    PowerShellCallableReference,
    PowerShellExpression {
    override fun getType(): PowerShellType {
        return PowerShellTypeUtil.inferExpressionType(this)
    }

    override fun getRangeInElement(): TextRange {
        val refRange = getNameElement()?.textRange ?: this.textRange
        return TextRange(refRange.startOffset - textRange.startOffset, refRange.endOffset - textRange.startOffset)
    }

    override fun getNameElement(): PsiElement? = findChildByClass(PowerShellCommandName::class.java)?.identifier

    override fun getCanonicalText(): String = getNameElement()?.text ?: super.getCanonicalText()
}
