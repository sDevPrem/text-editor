package com.sdevprem.basictexteditor.common

import com.sdevprem.basictexteditor.ui.editor.util.SpanStyleRange

fun List<SpanStyleRange>.filterInvalids(text: String) = filter {
    it.start <= it.end &&
            it.start <= text.length &&
            it.end <= text.length
}

