package talham7391.matchmaker

import kotlinx.coroutines.CompletableDeferred

interface Agent {
    fun joinedLobby(lobby: Lobby)
}

open class BasicAgent(val data: Any?) : Agent {
    var lobby: Lobby? = null

    override fun joinedLobby(lobby: Lobby) {
        this.lobby = lobby
    }
}

open class AsyncAgent(val data: Any?) : Agent {
    val lobby = CompletableDeferred<Lobby>()

    override fun joinedLobby(lobby: Lobby) {
        this.lobby.complete(lobby)
    }
}