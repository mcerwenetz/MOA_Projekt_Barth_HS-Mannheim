package de.pbma.moa.createroomdemo.service;

// Das ist unser Listener !!

public interface MyListener {
    void onRecieve(String topic,String msg);
    void onMQTTStatus(boolean connected);
    void log(String message);
}
