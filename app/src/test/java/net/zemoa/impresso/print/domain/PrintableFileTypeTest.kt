package net.zemoa.impresso.print.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrintableFileTypeTest {
    @Test
    fun `maps every supported MIME type`() {
        assertEquals(PrintableFileType.PDF, PrintableFileType.fromDeclaredMimeType("application/pdf"))
        assertEquals(PrintableFileType.JPEG, PrintableFileType.fromDeclaredMimeType("image/jpeg"))
        assertEquals(PrintableFileType.PNG, PrintableFileType.fromDeclaredMimeType("image/png"))
        assertEquals(PrintableFileType.GIF, PrintableFileType.fromDeclaredMimeType("image/gif"))
        assertEquals(PrintableFileType.WEBP, PrintableFileType.fromDeclaredMimeType("image/webp"))
        assertEquals(
            PrintableFileType.DOCX,
            PrintableFileType.fromDeclaredMimeType(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            ),
        )
        assertEquals(
            PrintableFileType.ODT,
            PrintableFileType.fromDeclaredMimeType("application/vnd.oasis.opendocument.text"),
        )
    }

    @Test
    fun `uses supported file extensions when MIME is unavailable`() {
        assertEquals(PrintableFileType.PDF, PrintableFileType.fromFileName("document.pdf"))
        assertEquals(PrintableFileType.JPEG, PrintableFileType.fromFileName("photo.JPG"))
        assertEquals(PrintableFileType.JPEG, PrintableFileType.fromFileName("photo.jpeg"))
        assertEquals(PrintableFileType.PNG, PrintableFileType.fromFileName("image.png"))
        assertEquals(PrintableFileType.GIF, PrintableFileType.fromFileName("image.gif"))
        assertEquals(PrintableFileType.WEBP, PrintableFileType.fromFileName("image.webp"))
        assertEquals(PrintableFileType.DOCX, PrintableFileType.fromFileName("document.docx"))
        assertEquals(PrintableFileType.ODT, PrintableFileType.fromFileName("document.odt"))
        assertNull(PrintableFileType.fromFileName("document.txt"))
    }

    @Test
    fun `distinguishes generic from explicitly declared MIME types`() {
        assertFalse(PrintableFileType.isUsableDeclaredMimeType(null))
        assertFalse(PrintableFileType.isUsableDeclaredMimeType("application/octet-stream"))
        assertFalse(PrintableFileType.isUsableDeclaredMimeType("*/*"))
        assertTrue(PrintableFileType.isUsableDeclaredMimeType("text/plain"))
    }
}
