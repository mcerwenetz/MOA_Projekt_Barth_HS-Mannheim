package de.pbma.moa.createroomdemo.Activity;

import android.app.Activity;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TimeoutRefresherThread {
    private final Activity activity;
    private final TextView tvtimeout;
    private long endTime;
    private RefresherThread refreshThread;
    private final AtomicBoolean keepRefreshing = new AtomicBoolean();

    public TimeoutRefresherThread(Activity activity, TextView tv) {
        this.tvtimeout = tv;
        this.activity = activity;
        refreshThread = new RefresherThread(activity,tv);
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
        refreshThread=null;
        refreshThread = new RefresherThread(activity,tvtimeout);
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

    private class RefresherThread extends Thread{
        private final Activity activity;
        private final TextView tvtimeout;


        RefresherThread(Activity activity, TextView tvtimeout){
            this.activity=activity;
            this.tvtimeout=tvtimeout;
        }

        @Override
        public void run() {
            super.run();
            while (keepRefreshing.get()) {
                this.activity.runOnUiThread(() -> tvtimeout.setText(formatTimeout(endTime)));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
