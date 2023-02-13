// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.powershell.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.intellij.plugin.powershell.psi.PowerShellTypes.*;
import com.intellij.plugin.powershell.psi.*;

public class PowerShellFlowControlStatementImplGen extends PowerShellPsiElementImpl implements PowerShellFlowControlStatement {

  public PowerShellFlowControlStatementImplGen(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PowerShellVisitor visitor) {
    visitor.visitFlowControlStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PowerShellVisitor) accept((PowerShellVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PowerShellExpression getExpression() {
    return findChildByClass(PowerShellExpression.class);
  }

  @Override
  @Nullable
  public PowerShellPipelineTail getPipelineTail() {
    return findChildByClass(PowerShellPipelineTail.class);
  }

  @Override
  @NotNull
  public List<PowerShellRedirection> getRedirectionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PowerShellRedirection.class);
  }

  @Override
  @Nullable
  public PowerShellVerbatimCommandArgument getVerbatimCommandArgument() {
    return findChildByClass(PowerShellVerbatimCommandArgument.class);
  }

}
