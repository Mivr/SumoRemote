package eu.mcft.sumoremote.commands

data class Command (
        var id: Long = 0,
        var name: String? = null,
        var address: Int = 0,
        var command: Int = 0
)

