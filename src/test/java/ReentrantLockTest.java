import org.junit.Test;

import java.util.concurrent.TimeUnit;
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

        public void doOne() {
            lock.lock();
//            lock.lock();
            try {
                //线程可以进入任何一个它已经拥有的锁所同步的代码块
                System.out.println(String.format("【%s】doOne", Thread.currentThread().getName()));
                doTwo();
            } finally {
                lock.unlock();
//                lock.unlock();
            }
        }
        public void doTwo() {
            lock.lock();
            try {
                System.out.println(String.format("【%s】doTwo", Thread.currentThread().getName()));
            } finally {
                lock.unlock();
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
            demo.doOne();
        }, "t1").start();
        new Thread(() -> {
            demo.doOne();
        }, "t2").start();

        TimeUnit.SECONDS.sleep(1);
    }
}
