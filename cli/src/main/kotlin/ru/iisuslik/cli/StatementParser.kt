package ru.iisuslik.cli

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams

/**
 * Class for parsing CLI statements
 */
class StatementParser(val varsContainer: VarsContainer) {
    private val builder = StringBuilder()
    private val currentInQuoteBuilder = StringBuilder()

    /**
     * Parses statement into special class
     *
     * @param statement string representation of statement
     * @return statement class
     */
    fun parse(statement: String): Statement {
        // Some preprocessing before parsing
        val substituted = substitution(statement)
        val statementLexer = CliLexer(CharStreams.fromString(substituted))
        val statementParser = CliParser(BufferedTokenStream(statementLexer))
        return statementParser.statement().value
    }

    /**
     * Replaces all \\s symbols into one " " and substitutes all vars from varsContainer
     *
     * @param statement
     * @return substitution result
     */
    fun substitution(statement: String): String {
        builder.clear()
        currentInQuoteBuilder.clear()
        var isDoubleQuotes = false
        var isQuotes = false
        for (c in statement) {
            if (isDoubleQuotes) {
                if (c == '"') {
                    dropToBuilder(c, true)
                    isDoubleQuotes = false
                } else {
                    currentInQuoteBuilder.append(c)
                }
            } else if (isQuotes) {
                if (c == '\'') {
                    dropToBuilder(c, false)
                    isQuotes = false
                } else {
                    currentInQuoteBuilder.append(c)
                }
            } else {
                if (c == '"' || c == '\'') {
                    dropToBuilder(c, true)
                    if (c == '"') {
                        isDoubleQuotes = true
                    } else {
                        isQuotes = true
                    }
                } else {
                    currentInQuoteBuilder.append(c)
                }
            }
        }
        if (!currentInQuoteBuilder.isEmpty()) {
            val res = currentInQuoteBuilder.toString()
            builder.append(substitute(res))
        }
        return builder.toString()
    }

    private fun dropToBuilder(symbolToAdd: Char, shouldSubstitute: Boolean) {
        val res = currentInQuoteBuilder.toString()
        builder.append(if (shouldSubstitute) substitute(res) else res)
        builder.append(symbolToAdd)
        currentInQuoteBuilder.clear()
    }


    private fun substitute(s: String) = s.replace("\\$[A-Za-z0-9]+".toRegex()) {
        val varName = it.value
        varsContainer.get(varName.subSequence(1 until varName.length).toString())
    }


    companion object {
        /**
         * Uses standard commands if we can, otherwise use external command
         */
        @JvmStatic
        fun build(name: String, args: List<String>): Command {
            return when (name) {
                "echo" -> Echo(args)
                "wc" -> Wc(args)
                "cat" -> Cat(args)
                "grep" -> Grep(args)
                // Real bash doesn't care too if we pass any args to pwd or exit
                "pwd" -> Pwd
                "exit" -> Exit
                else -> External(name, args)
            }
        }
    }
}