package net.zemoa.impresso.app

/** Boundary for future printer discovery; IMP-01 does not implement discovery protocols. */
interface DiscoveryCoordinator {
    fun start()

    fun stop()
}

class NoOpDiscoveryCoordinator : DiscoveryCoordinator {
    override fun start() = Unit

    override fun stop() = Unit
}
