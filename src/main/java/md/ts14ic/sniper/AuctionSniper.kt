package md.ts14ic.sniper

class AuctionSniper : AuctionEventListener {
    private val auction: Auction
    private val listener: SniperListener

    constructor(auction: Auction, listener: SniperListener) {
        this.auction = auction
        this.listener = listener
    }

    override fun auctionClosed() {
        listener.sniperLost()
    }

    override fun currentPrice(price: Int, increment: Int) {
        auction.bid(price + increment)
        listener.sniperBidding()
    }
}

interface SniperListener {
    fun sniperBidding()
    fun sniperLost()
    fun sniperWinning()
}
