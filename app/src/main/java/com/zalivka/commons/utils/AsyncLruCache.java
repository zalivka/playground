package com.zalivka.commons.utils;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.LruCache;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AsyncLruCache<T, E> extends LruCache<T, E> {
    private final String mBroadcast;

    private final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
    private final ThreadFactory sThreadFactory = new Fact();

    private SerialExecutor mExecutor = new SerialExecutor();

    /**
     * @param maxSize Maximum cache size. For now should always be {@link Integer#MAX_VALUE}.
     */
    public AsyncLruCache(int maxSize, String broadcast) {
        super(maxSize);
        mBroadcast = broadcast;
    }

    public abstract E createValue(T key);

    public abstract E createStub(T key);

    @Override
    protected E create(final T key) {
        mExecutor.execute(new Runnable() {
            @Override public void run() {
                put(key, createValue(key));

                LocalBroadcastManager.getInstance(StaticContextHolder.mCtx)
                        .sendBroadcast(new Intent(mBroadcast));
            }
        });
        return createStub(key);
    }

    public void createSync(final T key) {
        put(key, createValue(key));
    }

    private class SerialExecutor extends ThreadPoolExecutor {
        public SerialExecutor() {
            super(1, 1, 0, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory, new CallerRunsPolicy());
        }
    }

    private static class Fact implements ThreadFactory {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    }
}
