package Lock;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock
 * 特点：可重入，包含了公平锁和非公平锁两种锁（默认非公平锁）
 * @author 门那粒沙
 * @create 2020-03-11 19:54
 **/
public class ReentrantLockTest {

    class Demo {

        private ReentrantLock lock = new ReentrantLock(false);
        private ReentrantLock lock2 = new ReentrantLock(false);

        public void doOne(long time) {
            lock.lock();
//            lock.lock();
            try {
                //线程可以进入任何一个它已经拥有的锁所同步的代码块
                System.out.println(String.format("【%s】doOne beign", Thread.currentThread().getName()));
                doTwo(time);
                System.out.println(String.format("【%s】doOne end", Thread.currentThread().getName()));
            } finally {
                lock.unlock();
//                lock.unlock();
            }
        }
        public void doTwo(long time) {
            try {
                lock.lockInterruptibly();//这里用成lockInterruptibly，可中断，如果中断了lock()并不知道，还是会一直阻塞
                try {
                    System.out.println(String.format("【%s】doTwo begin", Thread.currentThread().getName()));
                    long begin = System.currentTimeMillis();
                    int i = 0;
                    while (System.currentTimeMillis() - begin < time) {
//                        System.out.println(i++);
                    }
                    System.out.println(String.format("【%s】doTwo end", Thread.currentThread().getName()));
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println(String.format("【%s】doTwo：线程已中断", Thread.currentThread().getName()));
                e.printStackTrace();
            }
        }
        public void doThree(long time) {
            lock2.lock();
            try {
                //线程可以进入任何一个它已经拥有的锁所同步的代码块
                System.out.println(String.format("【%s】doThree begin", Thread.currentThread().getName()));
                long begin = System.currentTimeMillis();
                while (System.currentTimeMillis() - begin < time) {
                }
                System.out.println(String.format("【%s】doThree end", Thread.currentThread().getName()));
            } finally {
                lock2.unlock();
            }
        }
    }

    /**
     * 测试reentrantLock的可重入性
     * 注意：lock()和unlock()要成对存在
     * 拓展：synchronized也是可重入的
     */
    @Test
    public void test() throws InterruptedException {
        Demo demo = new Demo();
        new Thread(() -> {
            demo.doOne(0);
        }, "t1").start();
        new Thread(() -> {
            demo.doOne(0);
        }, "t2").start();

        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * condition.await()和condition.signal()需要在取得所属lock的条件下才能使用，否则会抛异常：java.lang.IllegalMonitorStateException
     * @throws InterruptedException
     */
    @Test
    public void testCondition() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition cond = lock.newCondition();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(String.format("【%s】 begin", Thread.currentThread().getName()));
                cond.await();
                System.out.println(String.format("【%s】 end", Thread.currentThread().getName()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t1").start();

        System.out.println("调用signal()方法前");
        TimeUnit.SECONDS.sleep(2);

        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(2);
            cond.signal();
            System.out.println("调用signal()方法后");
        } finally {
            lock.unlock();
        }

        TimeUnit.SECONDS.sleep(1);
    }


    /**
     * 测试中断
     * 问题：测试的两个线程用了同个锁，导致两个线程同步执行，影响结果
     * todo start()后延迟一会儿才interrupt的话，即使是lockInterruptibly也仍然不会被中断
     */
    @Test
    public void intercept() throws InterruptedException {
        Demo demo = new Demo();

        //<editor-fold desc="ReentrantLock.lock方式无法中断，除非处于sleep或者wait状态">
        Thread thread1 = new Thread(() -> {
            demo.doThree(10000);
        });
        thread1.start();
        thread1.interrupt();
        //</editor-fold>

        //<editor-fold desc="ReentrantLock.lockInterruptibly方式即使是运行状态也可以中断">
        Thread thread2 = new Thread(() -> {
//            demo.doTwo(10000); // doTwo可以被中断
            demo.doOne(10000); // doOne里调用doTwo，doTwo可以被中断
        });
        thread2.start();
        thread2.interrupt();
        //</editor-fold>

        while (Thread.activeCount() > 2) {
            Thread.sleep(500);
        }
    }


    @Test
    public void test1() {

        Thread thread = new Thread(() -> {
            ReentrantLock reentrantLock = new ReentrantLock();
            try {
                reentrantLock.lockInterruptibly();

                long time = 10000;

                //模拟业务处理10s
                long begin = System.currentTimeMillis();
                while (System.currentTimeMillis() - begin < time) {
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("线程被中断");
            } finally {
                reentrantLock.unlock();
            }
        });
        thread.start();
        thread.interrupt();

        while (Thread.activeCount() > 2) {
        }
    }
}
