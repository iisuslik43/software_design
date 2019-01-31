package ru.iisuslik.cli

fun main(args: Array<String>) {
    val varsContainer = VarsContainer()
    val executor = Executor(varsContainer)
    val parser = StatementParser(varsContainer)
    while (true) {
        print("8===>:~$ ")
        val nextLine = readLine() ?: return
        val statement = parser.parse(nextLine)
        val status = executor.execute(statement)
        if (status == Executor.Status.EXIT) {
            break
        }
    }
}