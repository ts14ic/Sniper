package e2e

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
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID)

        auction.announceClosed()
        application.showsSniperHasLostAuction()
    }

    @Test
    fun sniperMakesAHigherBidButLoses() {
        auction.startSellingItem()

        application.startBiddingIn(auction)
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID)

        auction.reportPrice(/*current price*/1000, /*next increment*/98, "other bidder")
        application.hasShownSniperIsBidding()

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

        auction.announceClosed()
        application.showsSniperHasLostAuction()
    }
}
