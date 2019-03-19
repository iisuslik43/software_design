package ru.iisuslik.cli

import org.junit.Assert.*
import org.junit.Test
import java.io.File


fun getRealFileName(name: String) = "src${File.separator}test${File.separator}resources${File.separator}$name"

class FileControllerTest {

    @Test
    fun pwdWorks() {
        assertTrue(pwd().endsWith("${File.separator}software_design${File.separator}cli"))
    }

    @Test
    fun catSimple() {
        assertEquals("content1", catFiles(listOf(getRealFileName("file1"))))
    }

    @Test
    fun catTwoFiles() {
        assertEquals(
            "content1content2",
            catFiles(listOf(getRealFileName("file1"), getRealFileName("file2")))
        )
    }

    @Test
    fun wc() {
        assertEquals(
            "${getRealFileName("file3")}: 1 2 9",
            wcFiles(listOf(getRealFileName("file3")))
        )
    }

    @Test
    fun wcTwoFiles() {
        assertEquals(
            "${getRealFileName("file3")}: 1 2 9${System.lineSeparator()}" +
                    "${getRealFileName("file1")}: 1 1 8${System.lineSeparator()}total: 2 3 17",
            wcFiles(listOf(getRealFileName("file3"), getRealFileName("file1")))
        )
    }

    @Test
    fun grepFile() {
        assertEquals(
            "kek find me lol${System.lineSeparator()}",
            grepFiles(listOf(getRealFileName("file4")), "find me".toRegex(), 0)
        )
    }

    @Test
    fun grepFileIgnoringCase() {
        assertEquals(
            "kek find me lol${System.lineSeparator()}",
            grepFiles(listOf(getRealFileName("file4")), "FIND ME".toRegex(RegexOption.IGNORE_CASE), 0)
        )
    }

    @Test
    fun grepTwoFiles() {
        assertEquals(
            "content1${System.lineSeparator()}content2${System.lineSeparator()}",
            grepFiles(
                listOf(getRealFileName("file1"), getRealFileName("file2")),
                "content".toRegex(), 0
            )
        )
    }

    @Test
    fun grepTwoLines() {
        assertEquals(
            "kek find me lol${System.lineSeparator()}" +
                    "lollollol${System.lineSeparator()}",
            grepFiles(
                listOf( getRealFileName("file4")),
                "(lol)+".toRegex(), 0
            )
        )
    }

    @Test
    fun grepAdditionalLines() {
        assertEquals(
            "kek find me lol${System.lineSeparator()}" +
                    "keeeeek${System.lineSeparator()}" +
                    "lollollol${System.lineSeparator()}",
            grepFiles(listOf(getRealFileName("file4")), "find me".toRegex(), 2)
        )
    }

    @Test
    fun grepWord() {
        assertEquals(
            "kek find me lol${System.lineSeparator()}",
            grepFiles(listOf(getRealFileName("file4")), "(\\b|^)find(\\b|$)".toRegex(), 0)
        )
    }
    @Test
    fun grepWordOneOnLine() {
        assertEquals(
            "1${System.lineSeparator()}",
            grepFiles(listOf(getRealFileName("file4")), "(\\b|^)1(\\b|$)".toRegex(), 0)
        )
    }
}