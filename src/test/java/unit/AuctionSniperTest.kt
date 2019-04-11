package unit

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import md.ts14ic.sniper.Auction
import md.ts14ic.sniper.AuctionEventListener.*
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
            sniperListener.sniperBidding()
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
        sniper.currentPrice(1001, 25, PriceSource.FromOtherBidder)

        // ASSERT
        verify { sniperListener.sniperBidding() }
    }

    @Test
    fun reportIsWinningWhenCurrentPriceComesFromSniper() {
        // ACT
        sniper.currentPrice(123, 45, PriceSource.FromSniper)

        // ASSERT
        verify { sniperListener.sniperWinning() }
    }
}
