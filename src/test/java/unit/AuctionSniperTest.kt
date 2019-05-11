package unit

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.mockk.verifyOrder
import md.ts14ic.sniper.Auction
import md.ts14ic.sniper.AuctionEventListener.PriceSource
import md.ts14ic.sniper.AuctionSniper
import md.ts14ic.sniper.SniperListener
import org.junit.Before
import org.junit.Test

class AuctionSniperTest {
    companion object {
        private val ITEM_ID = "item"
    }

    @RelaxedMockK
    lateinit var sniperListener: SniperListener
    @RelaxedMockK
    lateinit var auction: Auction

    lateinit var sniper: AuctionSniper

    @Before
    fun initMocks() {
        MockKAnnotations.init(this)
        sniper = AuctionSniper(ITEM_ID, auction, sniperListener)
    }

    @Test
    fun reportsLostIfAuctionClosesImmediately() {
        // ACT
        sniper.auctionClosed()

        // ASSERT
        verify { sniperListener.sniperLost() }
    }

    @Test
    fun reportsLostIfAuctionClosesWhenBidding() {
        // ACT
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder)
        sniper.auctionClosed()

        // ASSERT
        verifyOrder {
            sniperListener.sniperBidding(any<AuctionSniper.SniperState>())
            sniperListener.sniperLost()
        }
    }

    @Test
    fun reportsWonIfAuctionClosesWhenWinning() {
        // ACT
        sniper.currentPrice(123, 45, PriceSource.FromSniper)
        sniper.auctionClosed()

        // ASSERT
        verifyOrder {
            sniperListener.sniperWinning()
            sniperListener.sniperWon()
        }
    }

    @Test
    fun bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        // ACT
        val price = 1001
        val increment = 25
        val bid = price + increment
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder)

        // ASSERT
        verify { sniperListener.sniperBidding(AuctionSniper.SniperState(ITEM_ID, price, bid)) }
    }

    @Test
    fun reportIsWinningWhenCurrentPriceComesFromSniper() {
        // ACT
        sniper.currentPrice(123, 45, PriceSource.FromSniper)

        // ASSERT
        verify { sniperListener.sniperWinning() }
    }
}
