package ru.iisuslik.cli

import org.junit.Assert.*
import org.junit.Test

class ExecutorTest {

    private val executor = Executor(VarsContainer())

    private fun Executor.runCommands(commands: List<Command>): String {
        val (result, status) = this.execute(Commands(commands))
        assertEquals(Executor.Status.CONTINUE, status)
        return result
    }


    @Test
    fun echoSimple() {
        assertEquals("kek", executor.runCommands(listOf(Echo(listOf("kek")))))
    }

    @Test
    fun echoTwoArgs() {
        assertEquals("kek lol", executor.runCommands(listOf(Echo(listOf("kek", "lol")))))
    }

    @Test
    fun echoInput() {
        assertEquals("lol", executor.runCommands(listOf(Echo(listOf("kek")), Echo(listOf("lol")))))
    }

    @Test
    fun catSimple() {
        assertEquals("content1", executor.runCommands(listOf(Cat(listOf(getRealFileName("file1"))))))
    }

    @Test
    fun catInput() {
        assertEquals(
            "kek", executor.runCommands(
                listOf(
                    Echo(listOf("kek")),
                    Cat(emptyList())
                )
            )
        )
    }

    @Test
    fun catInputIgnore() {
        assertEquals(
            "content1", executor.runCommands(
                listOf(
                    Echo(listOf("kek")),
                    Cat(listOf(getRealFileName("file1")))
                )
            )
        )
    }

    @Test
    fun wcSimple() {
        assertEquals(
            "${getRealFileName("file1")}: 1 1 8",
            executor.runCommands(listOf(Wc(listOf(getRealFileName("file1")))))
        )
    }

    @Test
    fun wcInput() {
        assertEquals(
            "1 1 3", executor.runCommands(
                listOf(
                    Echo(listOf("kek")),
                    Wc(emptyList())
                )
            )
        )
    }

    @Test
    fun wcInputIgnore() {
        assertEquals(
            "${getRealFileName("file1")}: 1 1 8",
            executor.runCommands(
                listOf(
                    Echo(listOf("kek")),
                    Wc(listOf(getRealFileName("file1")))
                )
            )
        )
    }

    @Test
    fun grepSimple() {
        assertEquals(
            "2${System.lineSeparator()}",
            executor.runCommands(listOf(Grep(listOf("2", getRealFileName("file4")))))
        )
    }

    @Test
    fun grepInput() {
        assertEquals(
            "1${System.lineSeparator()}", executor.runCommands(
                listOf(
                    Echo(listOf("1")),
                    Grep(listOf("1"))
                )
            )
        )
    }

    @Test
    fun grepInputIgnore() {
        assertEquals(
            "2${System.lineSeparator()}", executor.runCommands(
                listOf(
                    Echo(listOf("1")),
                    Grep(listOf("2", getRealFileName("file4")))
                )
            )
        )
    }

    @Test
    fun grepIgnoreCase() {
        assertEquals(
            "A${System.lineSeparator()}", executor.runCommands(
                listOf(
                    Echo(listOf("A")),
                    Grep(listOf("-i", "a"))
                )
            )
        )
    }

    @Test
    fun grepFindWord() {
        assertEquals(
            "kek find me lol${System.lineSeparator()}", executor.runCommands(
                listOf(
                    Grep(listOf("-w", "find", getRealFileName("file4")))
                )
            )
        )
    }

    @Test
    fun grepMoreLines() {
        assertEquals(
            "kek find me lol${System.lineSeparator()}keeeeek${System.lineSeparator()}", executor.runCommands(
                listOf(
                    Grep(listOf("-w", "-A", "1", "find", getRealFileName("file4")))
                )
            )
        )
    }

    @Test(expected = ErrorInCommandException::class)
    fun grepWrongA() {
        executor.runCommands(
            listOf(
                Grep(listOf("-A", "-10", "find", getRealFileName("file4")))
            )
        )

    }

    @Test(expected = ErrorInCommandException::class)
    fun grepWrongKey() {
        executor.runCommands(
            listOf(
                Grep(listOf("-q", "find", getRealFileName("file4")))
            )
        )

    }

    @Test(expected = ErrorInCommandException::class)
    fun grepWrongEmptyA() {
        executor.runCommands(
            listOf(
                Grep(listOf("-A", "find", getRealFileName("file4")))
            )
        )
    }
}