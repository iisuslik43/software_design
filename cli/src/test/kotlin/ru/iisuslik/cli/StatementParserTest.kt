package ru.iisuslik.cli

import org.junit.Test
import org.junit.Assert.*

class StatementParserTest {

    private val parser = StatementParser(VarsContainer())

    private fun getStatement(vararg commands: Command) = Commands(commands.toList())

    @Test
    fun parseExit() {
        assertEquals(getStatement(Exit), parser.parse("exit"))
    }

    @Test
    fun parseEcho() {
        assertEquals(getStatement(Echo(listOf("kek", "lol"))), parser.parse("echo kek lol"))
    }

    @Test
    fun parseWcEmpty() {
        assertEquals(getStatement(Wc(emptyList())), parser.parse("wc"))
    }

    @Test
    fun parseWcWithArgs() {
        assertEquals(getStatement(Wc(listOf("kekFile"))), parser.parse("wc kekFile"))
    }

    @Test
    fun parseCat() {
        assertEquals(getStatement(Cat(listOf("kek", "lol"))), parser.parse("cat kek lol"))
    }

    @Test
    fun parseGrep() {
        assertEquals(getStatement(Grep(listOf("kek", "lol"))), parser.parse("grep kek lol"))
    }

    @Test
    fun parsePwd() {
        assertEquals(getStatement(Pwd), parser.parse("pwd"))
    }

    @Test
    fun parseUselessArgs() {
        assertEquals(getStatement(Pwd), parser.parse("pwd kek lol"))
        assertEquals(getStatement(Exit), parser.parse("exit kek lol"))
    }

    @Test
    fun tooMuchSpacesBetweenArgs() {
        assertEquals(getStatement(Echo(listOf("kek", "lol"))), parser.parse("echo   kek lol"))
    }

    @Test
    fun tooMuchSpacesEverywhere() {
        assertEquals(getStatement(Echo(listOf("kek"))), parser.parse("   echo    kek   "))
    }

    @Test
    fun quotes() {
        assertEquals(getStatement(Echo(listOf("kek | lol"))), parser.parse("echo \"kek | lol\""))
        assertEquals(getStatement(Echo(listOf("kek | lol"))), parser.parse("echo \'kek | lol\'"))
    }

    @Test
    fun assignment() {
        val container = VarsContainer()
        val parser = StatementParser(container)
        assertEquals(Assignment("kek", "lol"), parser.parse("kek=lol"))
    }

    @Test
    fun substitutionEasy() {
        val container = VarsContainer()
        container.add("kek", "lol")
        val parser = StatementParser(container)
        assertEquals("lol", parser.substitution("\$kek"))
    }

    @Test
    fun twoDifferentSubstitutions() {
        val container = VarsContainer()
        container.add("kek", "lol")
        container.add("such", "wow")
        val parser = StatementParser(container)
        assertEquals("lol wow wow", parser.substitution("\$kek \$such wow"))
    }

    @Test
    fun twoSimilarSubstitutions() {
        val c = VarsContainer()
        c.add("kek", "lol")
        val parser = StatementParser(c)
        assertEquals(
            "lol lol such text lol wow",
            parser.substitution("lol \$kek such text \$kek wow")
        )
    }

    @Test
    fun substitutionsNotExists() {
        val c = VarsContainer()
        val parser = StatementParser(c)
        assertEquals("", parser.substitution("\$kek"))
    }

    @Test
    fun unknownCommand() {
        assertEquals(
            getStatement(External("wtf", listOf("-flag", "kek"))),
            parser.parse("wtf -flag kek")
        )
    }

    @Test
    fun easyPipe() {
        assertEquals(
            getStatement(Echo(listOf("kek")), Cat(emptyList())),
            parser.parse("echo kek | cat")
        )
    }

    @Test
    fun bigPipe() {
        assertEquals(
            getStatement(Echo(listOf("kek", "lol")), Cat(emptyList()), External("head", emptyList())),
            parser.parse("echo kek lol | cat | head")
        )
    }

    @Test
    fun quotesInside() {
        assertEquals(
            getStatement(Echo(listOf("       \" x   "))),
            parser.parse("echo '       \" x   '")
        )
        assertEquals(
            getStatement(Echo(listOf("       ' x   "))),
            parser.parse("echo \"       ' x   \"")
        )
    }

    @Test
    fun hardEcho() {
        assertEquals(
            getStatement(Echo(listOf("       \" x", "'asd"))),
            parser.parse("echo '       \" x' \"'asd\"")
        )
    }

    @Test
    fun superHardEcho() {
        assertEquals(
            getStatement(Echo(listOf("       \" x", "wedsf     'cv"))),
            parser.parse("echo '       \" x' \"wedsf     'cv\"")
        )
    }

    @Test
    fun substitutionDoubleQuotes() {
        val container = VarsContainer()
        container.add("kek", "lol")
        val parser = StatementParser(container)
        assertEquals("\"lol\"", parser.substitution("\"\$kek\""))
    }

    @Test
    fun substitutionQuotesNotHappening() {
        val container = VarsContainer()
        container.add("kek", "lol")
        val parser = StatementParser(container)
        assertEquals("'\$kek'", parser.substitution("'\$kek'"))
    }

    @Test
    fun echoDollar() {
        assertEquals(
            getStatement(Echo(listOf("a\$a"))),
            parser.parse("echo 'a\$a'")
        )
    }
}