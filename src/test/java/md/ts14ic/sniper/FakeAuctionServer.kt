package md.ts14ic.sniper

import md.ts14ic.sniper.Main.Companion.AUCTION_RESOURCE
import md.ts14ic.sniper.Main.Companion.ITEM_ID_AS_LOGIN
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
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
    private var currentChat: Chat? = null

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

    fun hasReceivedJoinRequestFromSniper() {
        messageListener.receivesAMessage()
    }

    fun announceClosed() {
        currentChat!!.sendMessage(Message())
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

        fun receivesAMessage() {
            assertThat("Message", messages.poll(5, TimeUnit.SECONDS), `is`(notNullValue()))
        }
    }
}
