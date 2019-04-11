package e2e

import md.ts14ic.sniper.Main
import md.ts14ic.sniper.Main.Companion.AUCTION_RESOURCE
import md.ts14ic.sniper.Main.Companion.ITEM_ID_AS_LOGIN
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.Message
import java.lang.String.format
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

class FakeAuctionServer {
    companion object {
        const val XMPP_HOSTNAME = "localhost"
        private const val AUCTION_PASSWORD = "auction"
    }

    val itemId: String
    private val messageListener = SingleMessageListener()
    private val connection: XMPPConnection
    private lateinit var currentChat: Chat

    constructor(itemId: String) {
        this.itemId = itemId
        this.connection = XMPPConnection(XMPP_HOSTNAME)
    }

    fun startSellingItem() {
        connection.connect()
        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE)
        connection.chatManager.addChatListener { chat, _ ->
            currentChat = chat
            chat.addMessageListener(messageListener)
        }
    }

    fun hasReceivedJoinRequestFromSniper(sniperId: String) {
        receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT))
    }

    fun hasReceivedBid(bid: Int, sniperId: String) {
        receivesAMessageMatching(sniperId, equalTo(Main.BID_COMMAND_FORMAT.format(bid)))
    }

    private fun receivesAMessageMatching(sniperId: String, matcher: Matcher<in String>) {
        messageListener.receivesAMessage(matcher)
        assertThat(currentChat.participant, equalTo(sniperId))
    }

    fun reportPrice(price: Int, increment: Int, bidder: String) {
        currentChat.sendMessage(Main.PRICE_EVENT_FORMAT.format(price, increment, bidder))
    }

    fun announceClosed() {
        currentChat.sendMessage(Main.CLOSE_EVENT_FORMAT)
    }

    fun stop() {
        disconnect()
    }

    fun disconnect() {
        connection.disconnect()
    }

    class SingleMessageListener : MessageListener {
        private val messages = ArrayBlockingQueue<Message>(/*capacity*/1)

        override fun processMessage(chat: Chat, message: Message) {
            messages.add(message)
        }

        fun receivesAMessage(messageMatcher: Matcher<in String>) {
            val message = messages.poll(5, TimeUnit.SECONDS)
            assertThat("Message", message, `is`(notNullValue()))
            assertThat(message.body, messageMatcher)
        }
    }
}
