package unit

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import md.ts14ic.sniper.AuctionEventListener
import md.ts14ic.sniper.AuctionEventListener.*
import md.ts14ic.sniper.AuctionMessageTranslator
import org.jivesoftware.smack.packet.Message
import org.junit.Before
import org.junit.Test

class AuctionMessageTranslatorTest {
    companion object {
        const val SNIPER_ID = "sniper"
    }

    @RelaxedMockK
    lateinit var listener: AuctionEventListener
    lateinit var translator: AuctionMessageTranslator

    @Before
    fun beforeEach() {
        MockKAnnotations.init(this)
        translator = AuctionMessageTranslator(SNIPER_ID, listener)
    }

    @Test
    fun notifiesAuctionClosedWhenCloseMessageReceived() {
        // ACT
        val message = Message().apply {
            body = "SOLVersion: 1.1; Event: CLOSE;"
        }
        translator.processMessage(/*chat*/null, message)

        // ASSERT
        verify { listener.auctionClosed() }
    }

    @Test
    fun notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        // ACT
        val message = Message().apply {
            body = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"
        }
        translator.processMessage(/*chat*/null, message)

        // ASSERT
        verify { listener.currentPrice(192, 7, PriceSource.FromOtherBidder) }
    }

    @Test
    fun notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        // ACT
        val message = Message().apply {
            body = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: $SNIPER_ID;"
        }
        translator.processMessage(/*chat*/null, message)

        // ASSERT
        verify { listener.currentPrice(234, 5, PriceSource.FromSniper) }
    }
}