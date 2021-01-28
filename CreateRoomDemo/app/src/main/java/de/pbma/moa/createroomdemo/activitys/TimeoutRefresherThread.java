package de.pbma.moa.createroomdemo.activitys;

import android.app.Activity;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Ändert sekündlich das Timeout in der UI. Ändert sich das Timeout muss er nicht
 * neu gestartet werden, da er mit Atomics arbeitet. Sein Lebenszyklus wird von außen verwaltet.
 */
public class TimeoutRefresherThread {
    private final Activity activity;
    private final TextView tvtimeout;
    private final AtomicBoolean keepRefreshing = new AtomicBoolean();
    private final AtomicLong endTime, startTime;
    private final Thread refreshThread;

    public TimeoutRefresherThread(Activity activity, TextView tv, AtomicLong endTime, AtomicLong startTime) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.tvtimeout = tv;
        this.activity = activity;
        refreshThread = new Thread(() -> {
            while (keepRefreshing.get()) {
                if (this.startTime.get() == 0)
                    TimeoutRefresherThread.this.activity.runOnUiThread(() ->
                            TimeoutRefresherThread.this.tvtimeout.setText(""));
                else if (this.endTime.get() <= System.currentTimeMillis())
                    TimeoutRefresherThread.this.activity.runOnUiThread(() ->
                            TimeoutRefresherThread.this.tvtimeout.setText(""));
                else if (startTime.get() > System.currentTimeMillis())
                    TimeoutRefresherThread.this.activity.runOnUiThread(() ->
                            TimeoutRefresherThread.this.tvtimeout.setText(formatTimeoutToOpen(startTime.get())));
                else
                    TimeoutRefresherThread.this.activity.runOnUiThread(() ->
                            TimeoutRefresherThread.this.tvtimeout.setText(formatTimeoutToClose(endTime.get())));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            TimeoutRefresherThread.this.activity.runOnUiThread(() ->
                    TimeoutRefresherThread.this.tvtimeout.setText(""));
        });
    }

    /**
     * Stoppt den Thread
     */
    public void stop() {
        keepRefreshing.set(false);
        try {
            refreshThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Startet den Thread wenn er noch nicht läuft.
     */
    public void initialStart() {
        if (!refreshThread.isAlive()) {
            keepRefreshing.set(true);
            this.refreshThread.start();
        }
    }

    /**
     * Formatiert die Unix Epoch zu einem schönen String
     * @param endTime Epoch Zeit die formatiert werden soll
     * @return einen schönen String
     */
    private String formatTimeoutToClose(long endTime) {
        DateTime now = new DateTime();
        DateTime endTimeDateTime = new DateTime(endTime);
        Period period = new Period(now, endTimeDateTime);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix("d ")
                .appendHours().appendSuffix("h ")
                .appendMinutes().appendSuffix("m ")
                .appendSeconds().appendSuffix("s ")
                .printZeroNever()
                .toFormatter();
        return formatter.print(period);
    }

    //Todo: Rausschmeißen. Doppelt, oder?
    private String formatTimeoutToOpen(long startTime) {
        DateTime now = new DateTime();
        DateTime startTimeDateTime = new DateTime(startTime);
        Period period = new Period(now, startTimeDateTime);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix("d ")
                .appendHours().appendSuffix("h ")
                .appendMinutes().appendSuffix("m ")
                .appendSeconds().appendSuffix("s ")
                .printZeroNever()
                .toFormatter();
        return formatter.print(period);
    }

    public boolean isAlive() {
        return this.refreshThread.isAlive();
    }
}
