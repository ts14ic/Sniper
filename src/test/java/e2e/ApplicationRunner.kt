package e2e

import e2e.FakeAuctionServer.Companion.XMPP_HOSTNAME
import md.ts14ic.sniper.Main
import md.ts14ic.sniper.Main.MainWindow.Companion.STATUS_JOINING

class ApplicationRunner {
    companion object {
        const val SNIPER_ID = "sniper"
        const val SNIPER_PASSWORD = "sniper"
        const val SNIPER_XMPP_ID = "sniper@localhost/Auction"
    }

    private lateinit var driver: AuctionSniperDriver

    fun startBiddingIn(auction: FakeAuctionServer) {
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
        driver.showsSniperStatus(STATUS_JOINING)
    }

    fun showsSniperHasLostAuction() {
        driver.showsSniperStatus(Main.MainWindow.STATUS_LOST)
    }

    fun showsSniperHasWonAuction() {
        driver.showsSniperStatus(Main.MainWindow.STATUS_WON)
    }

    fun hasShownSniperIsBidding() {
        driver.showsSniperStatus(Main.MainWindow.STATUS_BIDDING)
    }

    fun hasShownSniperIsWinning() {
        driver.showsSniperStatus(Main.MainWindow.STATUS_WINNING)
    }

    fun stop() {
        driver.dispose()
    }
}
