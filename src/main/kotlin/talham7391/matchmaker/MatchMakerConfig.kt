package talham7391.matchmaker

interface MatchMakerConfig {
    fun makeLobby(properties: MatchProperties): Lobby
    fun addAgentToLobby(agent: Agent, lobby: Lobby): Boolean
    fun isLobbyReady(lobby: Lobby): Boolean
}

abstract class BasicMatchMakerConfig : MatchMakerConfig {

    override fun makeLobby(properties: MatchProperties): Lobby = BasicLobby(properties)

    override fun addAgentToLobby(agent: Agent, lobby: Lobby): Boolean {
        val basicLobby = lobby as? BasicLobby
        basicLobby ?: return false

        basicLobby.agents.add(agent)
        return true
    }

    override fun isLobbyReady(lobby: Lobby): Boolean {
        val basicLobby = lobby as? BasicLobby
        return if (basicLobby == null) false else isBasicLobbyReady(basicLobby)
    }

    abstract fun isBasicLobbyReady(lobby: BasicLobby): Boolean
}