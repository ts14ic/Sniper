package md.ts14ic.sniper

import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.SwingUtilities
import javax.swing.table.AbstractTableModel

class MainWindow : JFrame {
    companion object {
        const val MAIN_WINDOW_NAME = "Sniper"
        const val SNIPER_TABLE_NAME = "status"

        const val STATUS_JOINING = "Joining"
        const val STATUS_BIDDING = "Bidding"
        const val STATUS_LOST = "Lost"
        const val STATUS_WON = "Won"
        const val STATUS_WINNING = "Winning"
    }

    private val snipers: SnipersTableModel

    constructor() : super("Auction Sniper") {
        name = MAIN_WINDOW_NAME

        snipers = SnipersTableModel()
        fillContentPane(makeSnipersTable())
        pack()

        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
    }

    private fun fillContentPane(snipersTable: JTable) {
        contentPane.layout = BorderLayout()
        contentPane.add(JScrollPane(snipersTable), BorderLayout.CENTER)
    }

    private fun makeSnipersTable(): JTable {
        return JTable(snipers)
                .apply {
                    name = SNIPER_TABLE_NAME
                }
    }

    fun showStatus(status: String) {
        snipers.statusText = status
    }

    fun sniperStateChanged(state: AuctionSniper.SniperState, statusText: String) {
        snipers.sniperStateChanged(state, statusText)
    }
}

class SnipersTableModel : AbstractTableModel {
    var statusText: String
        set(value) {
            field = value
            fireTableRowsUpdated(0, 0)
        }

    constructor() : super() {
        statusText = MainWindow.STATUS_JOINING
    }

    override fun getRowCount(): Int {
        return 1
    }

    override fun getColumnCount(): Int {
        return 1
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): String {
        return statusText
    }

    enum class Column {
        ITEM_IDENTIFIER,
        LAST_PRICE,
        LAST_BID,
        SNIPER_STATUS;

        companion object {
            fun at(offset: Int): Column {
                return values()[offset]
            }
        }
    }
}

class SniperStateDisplayer : SniperListener {
    private val ui: MainWindow

    constructor(ui: MainWindow) {
        this.ui = ui
    }

    override fun sniperBidding(state: AuctionSniper.SniperState) {
        SwingUtilities.invokeLater {
            ui.sniperStatusChanged(state, MainWindow.STATUS_BIDDING)
        }
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
