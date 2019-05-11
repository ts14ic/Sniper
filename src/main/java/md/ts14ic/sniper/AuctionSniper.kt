package md.ts14ic.sniper

import md.ts14ic.sniper.AuctionEventListener.PriceSource

class AuctionSniper : AuctionEventListener {
    private val itemId: String
    private val auction: Auction
    private val listener: SniperListener
    private var isWinning: Boolean

    constructor(itemId: String, auction: Auction, listener: SniperListener) {
        this.itemId = itemId
        this.auction = auction
        this.listener = listener
        this.isWinning = false
    }

    override fun auctionClosed() {
        if (isWinning) {
            listener.sniperWon()
        } else {
            listener.sniperLost()
        }
    }

    override fun currentPrice(price: Int, increment: Int, priceSource: PriceSource) {
        isWinning = priceSource == PriceSource.FromSniper
        if (isWinning) {
            listener.sniperWinning()
        } else {
            val bid = price + increment
            auction.bid(bid)
            listener.sniperBidding(SniperState(itemId, price, bid))
        }
    }

    data class SniperState(val itemId: String, val lastPrice: Int, val increment: Int)
}

interface SniperListener {
    fun sniperBidding(state: AuctionSniper.SniperState)
    fun sniperLost()
    fun sniperWinning()
    fun sniperWon()
}
