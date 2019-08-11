package talham7391.matchmaker

interface Lobby

interface MatchProperties

class BasicLobby(val properties: MatchProperties) : Lobby {
    val agents = mutableListOf<Agent>()
}
