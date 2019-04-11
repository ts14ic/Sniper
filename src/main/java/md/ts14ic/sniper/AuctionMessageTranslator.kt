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
        val event = AuctionEvent.from(message.body)

        when (event.type) {
            "CLOSE" -> listener.auctionClosed()
            "PRICE" -> listener.currentPrice(event.currentPrice, event.increment)
        }
    }

    class AuctionEvent {
        companion object {
            fun from(message: String): AuctionEvent {
                val event = AuctionEvent()
                for (field in fieldsIn(message)) {
                    event.addField(field)
                }
                return event
            }

            private fun fieldsIn(message: String): List<String> {
                return message.split(";")
            }
        }

        val type
            get() = get("Event")
        val currentPrice
            get() = getInt("CurrentPrice")
        val increment
            get() = getInt("Increment")
        private val fields = mutableMapOf<String, String>()

        private fun get(key: String): String {
            return fields.getValue(key)
        }

        private fun getInt(key: String): Int {
            return fields.getValue(key).toInt()
        }

        private fun addField(field: String) {
            val pair = field.split(":")
            if (pair.size == 2) {
                fields[pair[0].trim()] = pair[1].trim()
            }
        }
    }
}

interface AuctionEventListener {
    fun auctionClosed()
    fun currentPrice(price: Int, increment: Int)
}
