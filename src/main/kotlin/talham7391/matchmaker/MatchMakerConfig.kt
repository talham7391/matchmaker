package talham7391.matchmaker

interface MatchMakerConfig {
    fun makeLobby(lobbyProperties: LobbyProperties): Lobby
    fun addAgentToLobby(agent: Agent, lobby: Lobby): Boolean
    fun isLobbyReady(lobby: Lobby): Boolean
}
