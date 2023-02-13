package com.intellij.plugin.powershell.psi

import com.intellij.plugin.powershell.psi.PowerShellTypes.BEGIN
import com.intellij.plugin.powershell.psi.PowerShellTypes.BRACED_ID
import com.intellij.plugin.powershell.psi.PowerShellTypes.BREAK
import com.intellij.plugin.powershell.psi.PowerShellTypes.CATCH
import com.intellij.plugin.powershell.psi.PowerShellTypes.CLASS
import com.intellij.plugin.powershell.psi.PowerShellTypes.COMMENT
import com.intellij.plugin.powershell.psi.PowerShellTypes.CONFIGURATION
import com.intellij.plugin.powershell.psi.PowerShellTypes.CONTINUE
import com.intellij.plugin.powershell.psi.PowerShellTypes.DASH
import com.intellij.plugin.powershell.psi.PowerShellTypes.DATA
import com.intellij.plugin.powershell.psi.PowerShellTypes.DEC_INTEGER
import com.intellij.plugin.powershell.psi.PowerShellTypes.DEFINE
import com.intellij.plugin.powershell.psi.PowerShellTypes.DIV
import com.intellij.plugin.powershell.psi.PowerShellTypes.DO
import com.intellij.plugin.powershell.psi.PowerShellTypes.DOT
import com.intellij.plugin.powershell.psi.PowerShellTypes.DQ_CLOSE
import com.intellij.plugin.powershell.psi.PowerShellTypes.DQ_OPEN
import com.intellij.plugin.powershell.psi.PowerShellTypes.DS
import com.intellij.plugin.powershell.psi.PowerShellTypes.DYNAMICPARAM
import com.intellij.plugin.powershell.psi.PowerShellTypes.ELSE
import com.intellij.plugin.powershell.psi.PowerShellTypes.ELSEIF
import com.intellij.plugin.powershell.psi.PowerShellTypes.END
import com.intellij.plugin.powershell.psi.PowerShellTypes.ENUM
import com.intellij.plugin.powershell.psi.PowerShellTypes.EXIT
import com.intellij.plugin.powershell.psi.PowerShellTypes.EXPANDABLE_HERE_STRING_END
import com.intellij.plugin.powershell.psi.PowerShellTypes.EXPANDABLE_HERE_STRING_PART
import com.intellij.plugin.powershell.psi.PowerShellTypes.EXPANDABLE_HERE_STRING_START
import com.intellij.plugin.powershell.psi.PowerShellTypes.EXPANDABLE_STRING_PART
import com.intellij.plugin.powershell.psi.PowerShellTypes.FILTER
import com.intellij.plugin.powershell.psi.PowerShellTypes.FINALLY
import com.intellij.plugin.powershell.psi.PowerShellTypes.FOR
import com.intellij.plugin.powershell.psi.PowerShellTypes.FOREACH
import com.intellij.plugin.powershell.psi.PowerShellTypes.FROM
import com.intellij.plugin.powershell.psi.PowerShellTypes.FUNCTION
import com.intellij.plugin.powershell.psi.PowerShellTypes.GENERIC_ID_PART
import com.intellij.plugin.powershell.psi.PowerShellTypes.HAT
import com.intellij.plugin.powershell.psi.PowerShellTypes.HEX_INTEGER
import com.intellij.plugin.powershell.psi.PowerShellTypes.HIDDEN
import com.intellij.plugin.powershell.psi.PowerShellTypes.IF
import com.intellij.plugin.powershell.psi.PowerShellTypes.IN
import com.intellij.plugin.powershell.psi.PowerShellTypes.INLINESCRIPT
import com.intellij.plugin.powershell.psi.PowerShellTypes.OP_AND
import com.intellij.plugin.powershell.psi.PowerShellTypes.OP_BAND
import com.intellij.plugin.powershell.psi.PowerShellTypes.OP_BOR
import com.intellij.plugin.powershell.psi.PowerShellTypes.OP_BXOR
import com.intellij.plugin.powershell.psi.PowerShellTypes.OP_C
import com.intellij.plugin.powershell.psi.PowerShellTypes.OP_OR
import com.intellij.plugin.powershell.psi.PowerShellTypes.OP_XOR
import com.intellij.plugin.powershell.psi.PowerShellTypes.PARALLEL
import com.intellij.plugin.powershell.psi.PowerShellTypes.PARAM
import com.intellij.plugin.powershell.psi.PowerShellTypes.PERS
import com.intellij.plugin.powershell.psi.PowerShellTypes.PLUS
import com.intellij.plugin.powershell.psi.PowerShellTypes.PROCESS
import com.intellij.plugin.powershell.psi.PowerShellTypes.QMARK
import com.intellij.plugin.powershell.psi.PowerShellTypes.REAL_NUM
import com.intellij.plugin.powershell.psi.PowerShellTypes.REQUIRES_COMMENT_START
import com.intellij.plugin.powershell.psi.PowerShellTypes.RETURN
import com.intellij.plugin.powershell.psi.PowerShellTypes.SIMPLE_ID
import com.intellij.plugin.powershell.psi.PowerShellTypes.STAR
import com.intellij.plugin.powershell.psi.PowerShellTypes.STATIC
import com.intellij.plugin.powershell.psi.PowerShellTypes.SWITCH
import com.intellij.plugin.powershell.psi.PowerShellTypes.THIS
import com.intellij.plugin.powershell.psi.PowerShellTypes.THROW
import com.intellij.plugin.powershell.psi.PowerShellTypes.TRAP
import com.intellij.plugin.powershell.psi.PowerShellTypes.TRY
import com.intellij.plugin.powershell.psi.PowerShellTypes.UNTIL
import com.intellij.plugin.powershell.psi.PowerShellTypes.USING
import com.intellij.plugin.powershell.psi.PowerShellTypes.VAR
import com.intellij.plugin.powershell.psi.PowerShellTypes.VAR_ID
import com.intellij.plugin.powershell.psi.PowerShellTypes.VERBATIM_HERE_STRING
import com.intellij.plugin.powershell.psi.PowerShellTypes.VERBATIM_STRING
import com.intellij.plugin.powershell.psi.PowerShellTypes.WHILE
import com.intellij.plugin.powershell.psi.PowerShellTypes.WORKFLOW
import com.intellij.psi.tree.TokenSet

/**
 * Andrey 17/07/17.
 */
object PowerShellTokenTypeSets {

    val KEYWORDS = TokenSet.create(
        BEGIN,
        BREAK,
        CATCH,
        CLASS,
        CONTINUE,
        DATA,
        DEFINE,
        DO,
        DYNAMICPARAM,
        ELSE,
        ELSEIF,
        END,
        EXIT,
        FILTER,
        FINALLY,
        FOR,
        FOREACH,
        FROM,
        FUNCTION,
        IF,
        IN,
        INLINESCRIPT,
        PARALLEL,
        PARAM,
        PROCESS,
        RETURN,
        SWITCH,
        THROW,
        TRAP,
        TRY,
        UNTIL,
        USING,
        VAR,
        WHILE,
        WORKFLOW,
        CONFIGURATION,
        THIS,
        HIDDEN,
        STATIC,
        ENUM,
    )
    val COMMENTS = TokenSet.create(COMMENT, REQUIRES_COMMENT_START)
    val STRINGS = TokenSet.create(
        EXPANDABLE_STRING_PART,
        DQ_OPEN,
        DQ_CLOSE,
        VERBATIM_STRING,
        EXPANDABLE_HERE_STRING_START,
        EXPANDABLE_HERE_STRING_END,
        EXPANDABLE_HERE_STRING_PART,
        VERBATIM_HERE_STRING,
    )
    val NUMBERS = TokenSet.create(DEC_INTEGER, HEX_INTEGER, REAL_NUM)
    val IDENTIFIERS = TokenSet.create(SIMPLE_ID, BRACED_ID, VAR_ID, GENERIC_ID_PART, DS, QMARK, HAT)
    private val IDENTIFIERS_ALLOWED_AS_KEYWORD = TokenSet.create(
        PARAM, UNTIL, WORKFLOW, END, DEFINE, FINALLY, PARALLEL, CONTINUE, BEGIN, DYNAMICPARAM, IN,
        PROCESS, BREAK, ELSE, INLINESCRIPT, CATCH, THIS, HIDDEN, FOREACH, STATIC,
    )
    val MEMBER_IDENTIFIERS = TokenSet.orSet(TokenSet.create(SIMPLE_ID), IDENTIFIERS_ALLOWED_AS_KEYWORD)
    val BRACED_VARIABLE_IDENTIFIERS = TokenSet.create(BRACED_ID)
    val SIMPLE_VARIABLE_IDENTIFIERS = TokenSet.create(SIMPLE_ID, VAR_ID, THIS, QMARK, HAT, DS)
    val FUNCTION_IDENTIFIERS = TokenSet.orSet(
        TokenSet.create(SIMPLE_ID, GENERIC_ID_PART, THIS, QMARK, HAT, DS, DOT),
        IDENTIFIERS_ALLOWED_AS_KEYWORD,
    )
    val OPERATORS = TokenSet.create(OP_C, OP_AND, OP_OR, OP_XOR, OP_BAND, OP_BOR, OP_BXOR, PLUS, DASH, STAR, DIV, PERS)
    val PROCESS_AS_WORD_TOKENS = TokenSet.create(
        QMARK,
        DS,
        HAT,
        PowerShellTypes.LP,
        PowerShellTypes.RP,
        DOT,
    )
}
