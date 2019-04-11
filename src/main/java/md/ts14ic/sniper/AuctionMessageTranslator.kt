package md.ts14ic.sniper

import md.ts14ic.sniper.AuctionEventListener.*
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message

class AuctionMessageTranslator : MessageListener {
    companion object {
        const val EVENT_TYPE_CLOSE = "CLOSE"
        const val EVENT_TYPE_PRICE = "PRICE"
    }

    private val listener: AuctionEventListener
    private val sniperId: String

    constructor(sniperId: String, listener: AuctionEventListener) {
        this.listener = listener
        this.sniperId = sniperId
    }

    override fun processMessage(chat: Chat?, message: Message) {
        val event = AuctionEvent.from(message.body)

        when (event.type) {
            EVENT_TYPE_CLOSE ->
                listener.auctionClosed()
            EVENT_TYPE_PRICE ->
                listener.currentPrice(event.currentPrice, event.increment, event.isFrom(sniperId))
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
        private val bidder
            get() = get("Bidder")
        private val fields = mutableMapOf<String, String>()

        fun isFrom(sniperId: String): PriceSource {
            return if (sniperId == bidder) {
                PriceSource.FromSniper
            } else {
                PriceSource.FromOtherBidder
            }
        }

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
    fun currentPrice(price: Int, increment: Int, priceSource: PriceSource)

    enum class PriceSource {
        FromSniper, FromOtherBidder
    }
}
