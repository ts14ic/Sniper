package md.ts14ic.sniper

import org.junit.After
import org.junit.Test

class AuctionSniperEndToEndTest {
    /*
    DONE: single item join, lose without bidding
    TODO: single item join, bid and lose
    TODO: single item join, bid and win
    TODO: single item - show price details
    TODO: multiple items
    TODO: add new items through UI
    TODO: stop bidding at stop price
     */

    private val auction = FakeAuctionServer("item-54321")
    private val application = ApplicationRunner()

    @After
    fun afterEach() {
        auction.stop()
        application.stop()
    }

    @Test
    fun sniperJoinsAuctionUntilAuctionClosed() {
        auction.startSellingItem()

        application.startBiddingIn(auction)
        auction.hasReceivedJoinRequestFromSniper()

        auction.announceClosed()
        application.showsSniperHasLostAuction()
    }
}
