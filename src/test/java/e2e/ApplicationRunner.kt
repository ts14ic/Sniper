package e2e

import e2e.FakeAuctionServer.Companion.XMPP_HOSTNAME
import md.ts14ic.sniper.Main
import md.ts14ic.sniper.MainWindow
import md.ts14ic.sniper.MainWindow.Companion.STATUS_JOINING

class ApplicationRunner {
    companion object {
        const val SNIPER_ID = "sniper"
        const val SNIPER_PASSWORD = "sniper"
        const val SNIPER_XMPP_ID = "sniper@localhost/Auction"
    }

    private lateinit var itemId: String
    private lateinit var driver: AuctionSniperDriver

    fun startBiddingIn(auction: FakeAuctionServer) {
        itemId = auction.itemId
        val thread = Thread({
            try {
                Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.itemId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, "Test Application")
        thread.isDaemon = true
        thread.start()
        driver = AuctionSniperDriver(/*timeout ms*/1000)
        driver.showsSniperStatus(itemId, 0, 0, STATUS_JOINING)
    }

    fun hasShownSniperIsBidding(lastPrice: Int, lastBid: Int) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, MainWindow.STATUS_BIDDING)
    }

    fun hasShownSniperIsWinning(winningBid: Int) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, MainWindow.STATUS_WINNING)
    }

    fun showsSniperHasLostAuction(lastPrice: Int) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, MainWindow.STATUS_LOST)
    }

    fun showsSniperHasWonAuction(lastPrice: Int) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, MainWindow.STATUS_WON)
    }

    fun stop() {
        driver.dispose()
    }
}
