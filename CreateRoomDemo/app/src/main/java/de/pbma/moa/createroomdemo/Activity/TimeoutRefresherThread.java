package de.pbma.moa.createroomdemo.Activity;

import android.app.Activity;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

public class TimeoutRefresherThread {

    private final Activity activity;
    private final TextView tvtimeout;
    private long endTime;
    private final Thread refreshThread;
    private final AtomicBoolean keepRefreshing = new AtomicBoolean();

    public TimeoutRefresherThread(Activity activity, TextView tv) {
        this.activity = activity;
        this.tvtimeout = tv;
        refreshThread = new Thread(()->{
            while (keepRefreshing.get()) {
                    this.activity.runOnUiThread(() -> tvtimeout.setText(formatTimeout(endTime)));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        });

    }

    public void stop(){
        keepRefreshing.set(false);
        try {
            refreshThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void restart(long endTime){
        if(refreshThread.isAlive()){
            keepRefreshing.set(false);
            try {
                refreshThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.endTime = endTime;
        keepRefreshing.set(true);
        refreshThread.start();
    }

    private String formatTimeout(long endTime){
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
}
