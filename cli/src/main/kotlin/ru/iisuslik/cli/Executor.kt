package ru.iisuslik.cli

/**
 * Class that can execute commands in context
 */
class Executor(val varsContainer: VarsContainer) {
    /**
     * EXIT status means that we have only 1 command - exit
     */
    enum class Status { EXIT, CONTINUE }

    /**
     * Executes statement
     *
     * @param statement statement to execute
     */
    fun execute(statement: Statement): Pair<String, Status> {
        val result = statement.execute(varsContainer)
        return Pair(result, statement.status())
    }
}