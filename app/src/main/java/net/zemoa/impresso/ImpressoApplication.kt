package net.zemoa.impresso

import android.app.Application
import net.zemoa.impresso.app.AppGraph

class ImpressoApplication : Application() {
    val appGraph: AppGraph by lazy { AppGraph(this) }
}
