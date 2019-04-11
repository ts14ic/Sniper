package md.ts14ic.sniper

import md.ts14ic.sniper.Main.MainWindow.*
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.XMPPConnection
import java.awt.Color
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities
import javax.swing.border.LineBorder

class Main {
    companion object {
        const val MAIN_WINDOW_NAME = "Sniper"
        const val SNIPER_STATUS_NAME = "status"
        const val AUCTION_RESOURCE = "Auction"
        const val ITEM_ID_AS_LOGIN = "auction-%s"
        const val AUCTION_ID_FORMAT = "$ITEM_ID_AS_LOGIN@%s/$AUCTION_RESOURCE"

        const val JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: Join;"
        const val BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: Bid; Price: %s;"
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

    class MainWindow : JFrame {
        companion object {
            const val STATUS_JOINING = "Joining"
            const val STATUS_BIDDING = "Bidding"
            const val STATUS_LOST = "Lost"
            const val STATUS_WON = "Won"
            const val STATUS_WINNING = "Winning"
        }

        private val sniperStatus: JLabel

        constructor() : super("Auction Sniper") {
            name = MAIN_WINDOW_NAME

            sniperStatus = createLabel(STATUS_JOINING)
            add(sniperStatus)
            pack()

            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            isVisible = true
        }

        private fun createLabel(initialText: String): JLabel {
            return JLabel(initialText)
                    .apply {
                        name = SNIPER_STATUS_NAME
                        border = LineBorder(Color.BLACK)
                    }
        }

        fun showStatus(status: String) {
            sniperStatus.text = status
        }
    }

    class SniperStateDisplayer : SniperListener {
        private val ui: MainWindow

        constructor(ui: MainWindow) {
            this.ui = ui
        }

        override fun sniperBidding() {
            showStatus(MainWindow.STATUS_BIDDING)
        }

        override fun sniperLost() {
            showStatus(MainWindow.STATUS_LOST)
        }

        override fun sniperWinning() {
            showStatus(MainWindow.STATUS_WINNING)
        }

        override fun sniperWon() {
            showStatus(MainWindow.STATUS_WON)
        }

        private fun showStatus(status: String) {
            SwingUtilities.invokeLater {
                ui.showStatus(status)
            }
        }
    }
}
