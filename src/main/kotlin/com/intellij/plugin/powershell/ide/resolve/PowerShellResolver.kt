package com.intellij.plugin.powershell.ide.resolve

import com.intellij.plugin.powershell.psi.PowerShellCallableReference
import com.intellij.plugin.powershell.psi.PowerShellClassDeclarationStatement
import com.intellij.plugin.powershell.psi.PowerShellComponent
import com.intellij.plugin.powershell.psi.PowerShellEnumDeclarationStatement
import com.intellij.plugin.powershell.psi.PowerShellPsiElement
import com.intellij.plugin.powershell.psi.PowerShellQualifiedReferenceElement
import com.intellij.plugin.powershell.psi.PowerShellReferencePsiElement
import com.intellij.plugin.powershell.psi.PowerShellReferenceTypeElement
import com.intellij.plugin.powershell.psi.PowerShellVariable
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil

/**
 * Andrey 21/08/17.
 */
class PowerShellResolver<T> :
    ResolveCache.AbstractResolver<T, List<PowerShellResolveResult>> where T : PowerShellReferencePsiElement {
    companion object {
        val INSTANCE = PowerShellResolver<PowerShellReferencePsiElement>()
    }

    private fun resolveReference(ref: T): List<PowerShellResolveResult> {
        val result = ArrayList<PowerShellResolveResult>()
        val resolveProcessor = when (ref) {
            is PowerShellCallableReference -> //  $function: F  # invokes function F -> also callable reference (when ns== "function")
                PowerShellCallableResolveProcessor(ref)
            is PowerShellReferenceTypeElement -> PowerShellTypeResolveProcessor(ref)
            is PowerShellQualifiedReferenceElement<*> -> PowerShellQualifiedElementResolveProcessor(ref)
            else -> PowerShellVariableResolveProcessor(ref)
        }
        PsiTreeUtil.treeWalkUp(resolveProcessor, ref, null, ResolveState.initial())
        val res = resolveProcessor.getResult()
        if (res != null) result.add(res)
        return result
    }

    override fun resolve(ref: T, incompleteCode: Boolean): List<PowerShellResolveResult> {
        return resolveReference(ref)
    }
}

abstract class PowerShellReferenceResolveProcessor<out R : PowerShellReferencePsiElement>(protected val myRef: R) :
    PsiScopeProcessor {
    private var myResult: PowerShellResolveResult? =
        null // todo should navigate to closest declaration (of function or variable)

    fun getResult(): PowerShellResolveResult? {
        return myResult
    }

    protected fun setResult(element: PowerShellComponent): Boolean {
        if (myResult == null || myResult!!.element.textOffset > element.textOffset) {
            myResult = PowerShellResolveResult(element)
        }
        return true
    }
}

class PowerShellCallableResolveProcessor(ref: PowerShellCallableReference) :
    PowerShellReferenceResolveProcessor<PowerShellCallableReference>(ref) {

    override fun execute(element: PsiElement, state: ResolveState): Boolean {
        if (element is PowerShellComponent) {
            if (element.name.equals(
                    myRef.canonicalText,
                    true,
                ) && (element !is PowerShellVariable || PsNames.NAMESPACE_FUNCTION.equals(element.getScopeName(), true))
            ) {
                return setResult(element)
            }
        }
        return true
    }
}

class PowerShellVariableResolveProcessor(ref: PowerShellReferencePsiElement) :
    PowerShellReferenceResolveProcessor<PowerShellReferencePsiElement>(ref) {

    override fun execute(element: PsiElement, state: ResolveState): Boolean {
        val refName = myRef.canonicalText
        if (element is PowerShellVariable) {
            if (element.getQualifiedName().equals(refName, true)) {
                return setResult(element)
            }
        } else if (element is PowerShellComponent && element.name.equals(refName, true)) {
            return setResult(element)
        }

        return true
    }
}

class PowerShellTypeResolveProcessor(ref: PowerShellReferenceTypeElement) :
    PowerShellReferenceResolveProcessor<PowerShellReferencePsiElement>(ref) {

    override fun execute(element: PsiElement, state: ResolveState): Boolean {
        if (element is PowerShellClassDeclarationStatement || element is PowerShellEnumDeclarationStatement) {
            val refName = myRef.canonicalText
            if ((element as PowerShellComponent).name.equals(refName, true)) {
                return setResult(element)
            }
        }
        return true
    }
}

class PowerShellQualifiedElementResolveProcessor(ref: PowerShellQualifiedReferenceElement<PowerShellPsiElement>) :
    PowerShellReferenceResolveProcessor<PowerShellQualifiedReferenceElement<*>>(ref) {
    override fun execute(element: PsiElement, state: ResolveState): Boolean {
        val qualifier = myRef.qualifier
//    val definition = qualifier?.resolve()

        return true
    }
}
