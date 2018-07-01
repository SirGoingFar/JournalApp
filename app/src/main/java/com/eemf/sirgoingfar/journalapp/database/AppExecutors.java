package com.eemf.sirgoingfar.journalapp.database;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor onMainThread;
    private final Executor networkIO;
    private final Executor offMainThread;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor onMainThread, Executor offMainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.onMainThread = onMainThread;
        this.offMainThread = offMainThread;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor(),
                        Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor onMainThread() {
        return onMainThread;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor offMainThread(){
        return offMainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
