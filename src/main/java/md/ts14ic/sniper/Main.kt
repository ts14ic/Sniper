package md.ts14ic.sniper

import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.Message
import java.awt.Color
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

    constructor() {
        startUserInterface()
    }

    private fun startUserInterface() {
        SwingUtilities.invokeAndWait {
            ui = MainWindow()
        }
    }

    private fun joinAuction(connection: XMPPConnection, itemId: String) {
        val chat = connection.chatManager.createChat(
                auctionId(itemId, connection),
                { chat, message ->
                    SwingUtilities.invokeLater {
                        ui.showStatus(MainWindow.STATUS_LOST)
                    }
                }
        )
        chat.sendMessage(Message())
    }

    class MainWindow : JFrame {
        companion object {
            const val STATUS_JOINING = "Joining"
            const val STATUS_LOST = "Lost"
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
}