package unit

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import md.ts14ic.sniper.Auction
import md.ts14ic.sniper.AuctionSniper
import md.ts14ic.sniper.SniperListener
import org.junit.Before
import org.junit.Test

class AuctionSniperTest {
    @RelaxedMockK
    lateinit var sniperListener: SniperListener
    @RelaxedMockK
    lateinit var auction: Auction
    @InjectMockKs
    lateinit var sniper: AuctionSniper

    @Before
    fun initMocks() = MockKAnnotations.init(this)

    @Test
    fun reportsLostWhenAuctionCloses() {
        // ACT
        sniper.auctionClosed()

        // ASSERT
        verify { sniperListener.sniperLost() }
    }

    @Test
    fun bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        // ARRANGE
        val price = 1001
        val increment = 25

        // ACT
        sniper.currentPrice(price, increment)

        // ASSERT
        verify { sniperListener.currentPrice(price, increment) }
    }
}
