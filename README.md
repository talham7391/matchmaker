# matchmaker

A library that helps connect agents with each other.

# Table of Contents

1. [Getting Started](#getting-started)
2. [Async Await](#async-await)

<a name="getting-started"></a>
# Getting Started

Implement the following 2 interfaces to get started.

###  MatchProperties

Agents looking for the same `MatchProperties` will be assigned together. This is where the different game modes, match settings, etc.. will go. Make sure this class implements the `hashCode()` method. The simplest way to do this is to use a data class.
```
data class CardsTable(
	val game: String,
	val numPlayers: Int
) : MatchProperties
```

### BasicMatchMakerConfig

`MatchMaker` needs a config class that will tell it how to make sense of the lobbies. `BasicMatchMakerConfig` handles creating lobbies and adding players to them but you'll need to provide `isBasicLobbyReady()`.
```
class CardGameConfig : BasicMatchMakerConfig() {
	override fun isBasicLobbyReady(lobby : BasicLobby): Boolean {
		val cardsTable = lobby.properties as? CardsTable
		cardsTable ?: return false
		return cardsTable.numPlayers == lobby.agents.size
	}
}
```

### Putting it all together

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
}
```
Lets confirm the agents are in the same lobby:
```
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
```
Console output:
```
bob has joined a Hearts game with:
 - joe
 - mary
 - billy
```

<a name="async-await"></a>
# Async/Await

In some cases it may be awkward to use the `joinedLobby` method on the Agent to determine if an Agent is assigned a lobby. If you are using coroutines, then using the `AsyncAgent` would be cleaner:
```
val bob = AsyncAgent("bob")
matchMaker.registerAgent(bob, CardsTable("GoFish", 2))

val bobsLobby = bob.lobby.await()
```