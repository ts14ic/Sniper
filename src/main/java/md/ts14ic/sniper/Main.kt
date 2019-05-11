package md.ts14ic.sniper

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.XMPPConnection
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.SwingUtilities

class Main {
    companion object {
        const val AUCTION_RESOURCE = "Auction"
        const val ITEM_ID_AS_LOGIN = "auction-%s"
        const val AUCTION_ID_FORMAT = "$ITEM_ID_AS_LOGIN@%s/$AUCTION_RESOURCE"

        const val JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;"
        const val BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %s;"
        const val CLOSE_EVENT_FORMAT = "SOLVersion: 1.1; Event: CLOSE;"
        const val PRICE_EVENT_FORMAT = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %s; Increment: %s; Bidder: %s"

        private const val ARG_HOSTNAME = 0
        private const val ARG_USERNAME = 1
        private const val ARG_PASSWORD = 2
        private const val ARG_ITEM_ID = 3

        @JvmStatic
        fun main(vararg args: String) {
            val main = Main()
            main.joinAuction(connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]), args[ARG_ITEM_ID])
        }

        private fun connection(hostname: String, username: String, password: String): XMPPConnection {
            return XMPPConnection(hostname)
                    .apply {
                        connect()
                        login(username, password, AUCTION_RESOURCE)
                    }
        }

        private fun auctionId(itemId: String, connection: XMPPConnection): String {
            return AUCTION_ID_FORMAT.format(itemId, connection.serviceName)
        }
    }

    private lateinit var ui: MainWindow
    private var notToBeGcd: Chat? = null

    constructor() {
        startUserInterface()
    }

    private fun startUserInterface() {
        SwingUtilities.invokeAndWait {
            ui = MainWindow()
        }
    }

    private fun joinAuction(connection: XMPPConnection, itemId: String) {
        disconnectWhenUiCloses(connection)

        val chat = connection.chatManager.createChat(
                auctionId(itemId, connection),
                /*listener: added later*/null
        )
        this.notToBeGcd = chat

        val auction = XmppAuction(chat)
        chat.addMessageListener(AuctionMessageTranslator(
                connection.user,
                AuctionSniper(
                        itemId,
                        auction,
                        SniperStateDisplayer(ui)
                )
        ))
        auction.join()
    }

    private fun disconnectWhenUiCloses(connection: XMPPConnection) {
        ui.addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                connection.disconnect()
            }
        })
    }

}
