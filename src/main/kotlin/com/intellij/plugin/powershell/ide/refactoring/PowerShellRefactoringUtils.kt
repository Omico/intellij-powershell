package com.intellij.plugin.powershell.ide.refactoring

import com.intellij.openapi.util.text.StringUtil
import com.intellij.plugin.powershell.psi.PowerShellClassDeclarationStatement
import com.intellij.plugin.powershell.psi.PowerShellComponent
import com.intellij.plugin.powershell.psi.PowerShellConstructorDeclarationStatement
import com.intellij.plugin.powershell.psi.PowerShellEnumDeclarationStatement
import com.intellij.plugin.powershell.psi.PowerShellEnumLabelDeclaration
import com.intellij.plugin.powershell.psi.PowerShellFunctionStatement
import com.intellij.plugin.powershell.psi.PowerShellMethodDeclarationStatement
import com.intellij.plugin.powershell.psi.PowerShellPropertyDeclarationStatement
import com.intellij.plugin.powershell.psi.PowerShellTargetVariableExpression
import com.intellij.plugin.powershell.psi.PowerShellTypes
import com.intellij.plugin.powershell.psi.PowerShellVariable
import com.intellij.psi.PsiElement

internal fun isBracedVariable(element: PsiElement?): Boolean = isVariable(element) &&
    element!!.firstChild?.node?.elementType === PowerShellTypes.BRACED_VAR_START

internal fun isSimpleVariable(element: PsiElement?): Boolean = isVariable(element) &&
    element!!.firstChild?.node?.elementType !== PowerShellTypes.BRACED_VAR_START

private fun isVariable(element: PsiElement?) = element is PowerShellTargetVariableExpression

internal fun isFunctionStatement(element: PsiElement?) = element is PowerShellFunctionStatement

internal fun isMember(element: PsiElement?): Boolean {
    return element is PowerShellClassDeclarationStatement ||
        element is PowerShellPropertyDeclarationStatement ||
        element is PowerShellMethodDeclarationStatement ||
        element is PowerShellConstructorDeclarationStatement ||
        element is PowerShellEnumDeclarationStatement ||
        element is PowerShellEnumLabelDeclaration
}

internal fun isComponent(element: PsiElement): Boolean = element is PowerShellComponent

internal fun isVariableWithNamespace(element: PsiElement): Boolean =
    element is PowerShellVariable && StringUtil.isNotEmpty(element.getScopeName())
