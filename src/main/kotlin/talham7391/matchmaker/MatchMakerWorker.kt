package talham7391.matchmaker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

sealed class MatchMakerMessage

data class RegisterAgentMessage(val agent: Agent, val properties: MatchProperties) : MatchMakerMessage()

fun CoroutineScope.matchMakerWorker(matchMaker: MatchMaker): SendChannel<MatchMakerMessage> {
    val channel = Channel<MatchMakerMessage>()
    launch {
        for (message in channel) {
            when (message) {
                is RegisterAgentMessage -> {
                    matchMaker.registerAgent(message.agent, message.properties)
                }
            }
        }
    }
    return channel
}