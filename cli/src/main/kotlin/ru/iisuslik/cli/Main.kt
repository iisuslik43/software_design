package ru.iisuslik.cli

import org.antlr.v4.runtime.RecognitionException
import java.lang.IllegalStateException

/**
 * Main CLI function with main cycle
 */
fun main() {
    val varsContainer = VarsContainer()
    val executor = Executor(varsContainer)
    val parser = StatementParser(varsContainer)
    while (true) {
        print(":~$ ")
        val nextLine = readLine() ?: return
        if (nextLine == "") {
            continue
        }
        try {
            val statement = parser.parse(nextLine)
            val (result, status) = executor.execute(statement)
            if (status == Executor.Status.EXIT) {
                break
            } else {
                println(result)
            }
        } catch (e: RecognitionException) {
            println("Parsing error")
        } catch (e: IllegalStateException) {
            println("Parsing error")
        } catch (e: CommandNotFoundException) {
            println("Command not found: ${e.message}")
        } catch (e: ErrorInCommandException) {

        }
    }
}