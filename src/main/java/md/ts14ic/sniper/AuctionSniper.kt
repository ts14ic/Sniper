package md.ts14ic.sniper

import md.ts14ic.sniper.AuctionEventListener.PriceSource

class AuctionSniper : AuctionEventListener {
    private val auction: Auction
    private val listener: SniperListener
    private var isWinning: Boolean

    constructor(auction: Auction, listener: SniperListener) {
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
            auction.bid(price + increment)
            listener.sniperBidding()
        }
    }
}

interface SniperListener {
    fun sniperBidding()
    fun sniperLost()
    fun sniperWinning()
    fun sniperWon()
}
