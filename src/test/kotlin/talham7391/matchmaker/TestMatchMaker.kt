/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package talham7391.matchmaker

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

interface AgentListener {
    fun agentReady(agent: MyAgent)
}

data class MyAgent(val name: String, val listener: AgentListener) : Agent {
    var lobby: MyLobby? = null

    override fun joinedLobby(lobby: Lobby) {
        this.lobby = lobby as? MyLobby
        listener.agentReady(this)
    }
}

data class MyMatchProperties(
        val map: String
) : MatchProperties

data class MyLobby(
        val properties: MyMatchProperties,
        val maxNumPlayers: Int
) : Lobby {
    val agents = mutableListOf<Agent>()
}

class MyMatchMakerConfig(val lobbySize: Int) : MatchMakerConfig {
    override fun makeLobby(properties: MatchProperties): Lobby {
        return MyLobby(properties as MyMatchProperties, lobbySize)
    }

    override fun addAgentToLobby(agent: Agent, lobby: Lobby): Boolean {
        val l = lobby as? MyLobby
        l?.let {
            it.agents.add(agent)
            return true
        }
        return false
    }

    override fun isLobbyReady(lobby: Lobby): Boolean {
        val p = lobby as MyLobby
        return p.agents.size == p.maxNumPlayers
    }
}

class TestCanMakeMatch : AgentListener {
    var readyAgent: MyAgent? = null

    @Test fun testCanMakeMatch() {
        val mm = MatchMaker(MyMatchMakerConfig(1))
        val map = "HALO"
        mm.registerAgent(MyAgent("bob", this), MyMatchProperties(map))
        assert(readyAgent != null)
        assert(readyAgent?.lobby?.properties?.map == map)
        assert(readyAgent?.let { it.lobby?.agents?.contains(it) } ?: false)
    }

    override fun agentReady(agent: MyAgent) {
        readyAgent = agent
    }
}

class TestCanJoinMultipleMatches : AgentListener {
    val lobbies = mutableSetOf<MyLobby>()

    @Test fun testCanJoinMultipleMatches() {
        val lobbySize = 8
        val numLobbies = 10
        val mm = MatchMaker(MyMatchMakerConfig(lobbySize))
        repeat(numLobbies) {
            val m = "HALO - ${Random().nextInt()}"
            repeat(lobbySize) {
                mm.registerAgent(MyAgent("bob", this), MyMatchProperties(m))
            }
        }
        assertEquals(numLobbies, lobbies.size)
    }

    override fun agentReady(agent: MyAgent) {
        agent.lobby?.let { lobbies.add(it) }
    }
}

class TestPlayersJoinRightMatch : AgentListener {

    @Test fun testPlayersJoinRightMatch() {
        val mm = MatchMaker(MyMatchMakerConfig(2))

        val p1 = MyAgent("p1", this)
        val p2 = MyAgent("p2", this)
        val p3 = MyAgent("p3", this)
        val p4 = MyAgent("p4", this)

        mm.registerAgent(p1, MyMatchProperties("president"))
        mm.registerAgent(p2, MyMatchProperties("estimation"))
        mm.registerAgent(p3, MyMatchProperties("estimation"))
        mm.registerAgent(p4, MyMatchProperties("president"))
    }

    override fun agentReady(agent: MyAgent) {
        if (agent.name == "p1" || agent.name == "p4") {
            assertEquals("president", agent.lobby?.properties?.map)
        } else {
            assertEquals("estimation", agent.lobby?.properties?.map)
        }
    }
}

data class CardsTable(
        val game: String,
        val numPlayers: Int
) : MatchProperties

class CardGameConfig : BasicMatchMakerConfig() {
    override fun isBasicLobbyReady(lobby: BasicLobby): Boolean {
        val cardsTable = lobby.properties as? CardsTable
        cardsTable ?: return false
        return cardsTable.numPlayers == lobby.agents.size
    }
}

class TestBasicImplementationsWork() {
    @Test fun testBasicImplementationsWork() {
        val matchMaker = MatchMaker(CardGameConfig())

        val people = listOf("bob", "joe", "mary", "billy")
        val agents = people.map { BasicAgent(it) }

        // no agent is matched to a lobby yet
        agents.forEach { assert(it.lobby == null) }

        // 4 agents request to match to the same match properties
        val heartsTable = CardsTable("Hearts", 4)
        agents.forEach { matchMaker.registerAgent(it, heartsTable) }

        // each should have been matched to a lobby
        agents.forEach { assert(it.lobby != null) }

        agents[0].let {
            val name = it.data as String
            val basicLobby = it.lobby as BasicLobby
            val properties = basicLobby.properties as CardsTable

            println("$name has joined a ${properties.game} game with:")
            for (agent in basicLobby.agents) {
                val basicAgent = agent as BasicAgent
                val agentName = basicAgent.data as String
                if (agentName != name) {
                    println(" - $agentName")
                }
            }
        }
    }
}
