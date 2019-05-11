package e2e

import com.objogate.wl.swing.AWTEventQueueProber
import com.objogate.wl.swing.driver.JFrameDriver
import com.objogate.wl.swing.driver.JTableDriver
import com.objogate.wl.swing.gesture.GesturePerformer
import com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching
import com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText
import md.ts14ic.sniper.MainWindow
import org.hamcrest.Matchers.equalTo

class AuctionSniperDriver(timeoutMillis: Int) :
        JFrameDriver(
                GesturePerformer(),
                JFrameDriver.topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), showingOnScreen()),
                AWTEventQueueProber(timeoutMillis.toLong(), 100)
        ) {

    fun showsSniperStatus(itemId: String, lastPrice: Int, lastBid: Int, statusText: String) {
        JTableDriver(this)
                .hasRow(matching(
                        withLabelText(equalTo(itemId)),
                        withLabelText(equalTo(lastPrice.toString())),
                        withLabelText(equalTo(lastBid.toString())),
                        withLabelText(equalTo(statusText))
                ))
    }
}
