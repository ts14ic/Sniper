package md.ts14ic.sniper

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message

class AuctionMessageTranslator : MessageListener {
    private val listener: AuctionEventListener

    constructor(listener: AuctionEventListener) {
        this.listener = listener
    }

    override fun processMessage(chat: Chat?, message: Message) {
        val event = unpackEventFrom(message)

        val type = event["Event"]
        if (type == "CLOSE") {
            listener.auctionClosed()
        } else if (type == "PRICE") {
            listener.currentPrice(
                    event.getValue("CurrentPrice").toInt(),
                    event.getValue("Increment").toInt()
            )
        }
    }

    private fun unpackEventFrom(message: Message): Map<String, String> {
        val event = mutableMapOf<String, String>()
        for (element in message.body.split(";")) {
            if (element.isEmpty()) {
                continue
            }
            val pair = element.split(":")
            val key = pair[0].trim()
            val value = pair[1].trim()
            event[key] = value
        }
        return event
    }
}

interface AuctionEventListener {
    fun auctionClosed()
    fun currentPrice(price: Int, increment: Int)
}
