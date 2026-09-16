package net.zemoa.impresso.print.presentation.selectfile

import net.zemoa.impresso.print.domain.PrintableFile

sealed interface SelectPrintableFileEffect {
    data class Continue(val file: PrintableFile) : SelectPrintableFileEffect
}
