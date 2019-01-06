package org.ruminaq.runner.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.ruminaq.runner.RunnerLoggerFactory;
import org.slf4j.Logger;

public class Observable {

    private final Logger logger = RunnerLoggerFactory.getLogger(Observable.class);

    private List<Observer> observers = new ArrayList<>();

    private boolean      parallel = false;
    private int          poolSize = 1;
    private ForkJoinPool pool;

    private ReentrantLock parallelLock      = new ReentrantLock();
    private Condition     parallelCondition = parallelLock.newCondition();

    public void addObserver(Observer ob) {
        observers.add(ob);
        if(observers.size() > 1) {
            parallel = true;
            poolSize = Integer.highestOneBit(observers.size() - 1) << 1;
            logger.trace("pool size: {}", poolSize);
        }
    }

    private volatile int i = 0;

    protected void notifyObservers(final Object obj) {
        logger.trace("notifyObservers");
        if(!parallel) {
            logger.trace("single");
            if(observers.size() > 0) observers.get(0).update(this, obj);
        } else {
            logger.trace("parallel");
            if(pool == null)
                pool = new ForkJoinPool(poolSize);

            parallelLock.lock();
            for(final Observer o : observers) {
                pool.submit(new RecursiveAction() { private static final long serialVersionUID = 1L;
                    @Override protected void compute() { o.update(Observable.this, obj); }
                });
                i++;
            }
            try {
                logger.trace("wait");
                parallelCondition.await();
                logger.trace("wake up");
            } catch (InterruptedException e) { }
            parallelLock.unlock();
        }
    }

    public void done() {
        if(parallel) {
            parallelLock.lock();
            logger.trace("parallel done");
            if(--i == 0) parallelCondition.signal();
            parallelLock.unlock();
        }
    }
}
