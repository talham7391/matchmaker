package talham7391.matchmaker

interface Agent {
    fun joinedLobby(lobby: Lobby)
}

class BasicAgent(val data: Any?) : Agent {
    var lobby: Lobby? = null

    override fun joinedLobby(lobby: Lobby) {
        this.lobby = lobby
    }
}