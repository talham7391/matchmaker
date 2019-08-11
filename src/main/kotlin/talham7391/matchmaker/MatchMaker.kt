/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package talham7391.matchmaker


data class LobbyData(
        val lobby: Lobby,
        val agents: MutableList<Agent>
)

class MatchMaker(
        private val config: MatchMakerConfig
) {
    private val lobbies = mutableMapOf<MatchProperties, LobbyData>()

    fun registerAgent(agent: Agent, properties: MatchProperties): Boolean {
        if (properties !in lobbies) {
            lobbies[properties] = LobbyData(
                    config.makeLobby(properties),
                    mutableListOf()
            )
        }

        lobbies[properties]?.let { data ->
            if (config.addAgentToLobby(agent, data.lobby)) {
                data.agents.add(agent)
            } else {
                return false
            }


            if (config.isLobbyReady(data.lobby)) {
                lobbies.remove(properties)
                data.agents.forEach { it.joinedLobby(data.lobby) }
            }

            return true
        }

        return false
    }
}
