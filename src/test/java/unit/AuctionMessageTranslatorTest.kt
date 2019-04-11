package unit

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import md.ts14ic.sniper.AuctionEventListener
import md.ts14ic.sniper.AuctionMessageTranslator
import org.jivesoftware.smack.packet.Message
import org.junit.Before
import org.junit.Test

class AuctionMessageTranslatorTest {
    @RelaxedMockK
    lateinit var listener: AuctionEventListener
    @InjectMockKs
    lateinit var translator: AuctionMessageTranslator

    @Before
    fun beforeEach() = MockKAnnotations.init(this)

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
    fun notifiesBidDetailsWhenCurrentPriceMessageReceived() {
        // ACT
        val message = Message().apply {
            body = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"
        }
        translator.processMessage(/*chat*/null, message)

        // ASSERT
        verify { listener.currentPrice(192, 7) }
    }
}