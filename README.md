# matchmaker

A library that helps connect agents with each other.

## Getting Started

Implement the following 2 interfaces to get started.

####  MatchProperties

Agents looking for the same `MatchProperties` will be assigned together. This is where the different game modes, match settings, etc.. will go. Make sure this class implements the `hashCode()` method. The simplest way to do this is to use a data class.
```
data class CardsTable(
	val game: String,
	val numPlayers: Int
) : MatchProperties
```

#### BasicMatchMakerConfig

`MatchMaker` needs a config class that will tell it how to make sense of the lobbies.
```
class CardGameConfig : BasicMatchMakerConfig() {
	override fun isBasicLobbyReady(lobby : BasicLobby): Boolean {
		val cardsTable = lobby.properties as? CardsTable
		cardsTable ?: return false
		return cardsTable.numPlayers == lobby.agents.size
	}
}
```

Now that we have the basic class implemented, we can glue everything together:

```
fun main() {
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
```
Console output:
```
bob has joined a Hearts game with:
 - joe
 - mary
 - billy
```