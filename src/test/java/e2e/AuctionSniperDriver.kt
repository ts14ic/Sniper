package e2e

import com.objogate.wl.swing.AWTEventQueueProber
import com.objogate.wl.swing.driver.JFrameDriver
import com.objogate.wl.swing.driver.JLabelDriver
import com.objogate.wl.swing.gesture.GesturePerformer
import md.ts14ic.sniper.Main

import org.hamcrest.Matchers.equalTo

class AuctionSniperDriver(timeoutMillis: Int) :
        JFrameDriver(
                GesturePerformer(),
                JFrameDriver.topLevelFrame(named(Main.MAIN_WINDOW_NAME), showingOnScreen()),
                AWTEventQueueProber(timeoutMillis.toLong(), 100)
        ) {

    fun showsSniperStatus(statusText: String) {
        JLabelDriver(this, named(Main.SNIPER_STATUS_NAME))
                .hasText(equalTo(statusText))
    }
}
