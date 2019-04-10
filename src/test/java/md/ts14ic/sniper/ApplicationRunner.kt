package md.ts14ic.sniper

import md.ts14ic.sniper.FakeAuctionServer.Companion.XMPP_HOSTNAME
import md.ts14ic.sniper.Main.MainWindow.Companion.STATUS_JOINING
import md.ts14ic.sniper.Main.MainWindow.Companion.STATUS_LOST

class ApplicationRunner {
    companion object {
        const val SNIPER_ID = "sniper"
        const val SNIPER_PASSWORD = "sniper"
    }

    private var driver: AuctionSniperDriver? = null

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
        driver!!.showsSniperStatus(STATUS_JOINING)
    }

    fun showsSniperHasLostAuction() {
        driver!!.showsSniperStatus(STATUS_LOST)
    }

    fun stop() {
        driver?.dispose()
    }
}
