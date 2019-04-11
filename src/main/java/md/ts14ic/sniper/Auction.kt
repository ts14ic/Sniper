package md.ts14ic.sniper

import org.jivesoftware.smack.Chat

interface Auction {
    fun bid(amount: Int)
    fun join()
}

class XmppAuction : Auction {
    private val chat: Chat

    constructor(chat: Chat) {
        this.chat = chat
    }

    override fun bid(amount: Int) {
        sendMessage(Main.BID_COMMAND_FORMAT.format(amount))
    }

    override fun join() {
        sendMessage(Main.JOIN_COMMAND_FORMAT)
    }

    private fun sendMessage(message: String) {
        chat.sendMessage(message)
    }
}