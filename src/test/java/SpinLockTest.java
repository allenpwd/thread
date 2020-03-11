import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 实现自旋锁
 * 优点：自旋锁不会使线程状态发生切换，而是一直处于用户态，不会使线程进入阻塞状态，减少了不必要的上下文切换
 * 缺点：如果某个线程持有锁的时间过长，就会导致其它等待获取锁的线程进入循环等待，消耗CPU。使用不当会造成CPU使用率极高。
 * <p>
 * 说明：通过CAS操作完成自旋锁
 *
 * @author 门那粒沙
 * @create 2020-03-11 21:49
 **/
public class SpinLockTest {
    //原子引用线程
    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    /**
     * 加锁
     */
    public void lock() {
        Thread thread = Thread.currentThread();
        //利用CAS
        while (!atomicReference.compareAndSet(null, thread)) {
        }
        System.out.println(Thread.currentThread().getName() + " lock success");
    }

    /**
     * 解锁
     */
    public void unlock() {
        Thread thread = Thread.currentThread();
        if (atomicReference.compareAndSet(thread, null)) {
            System.out.println(Thread.currentThread().getName() + " unlock success");
        }
    }

    /**
     * 测试自定义的自旋锁
     * 说明：t1占用锁3秒，然后让t2去获取锁
     * 结果：t2会一直循环获取锁，直到t1释放锁后t2获取锁成功
     */
    @Test
    public void test() throws InterruptedException {
        SpinLockTest spinLockDemo = new SpinLockTest();
        new Thread(() -> {
            spinLockDemo.lock();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            spinLockDemo.unlock();
        }, "t1").start();

        //保证t1已经占用锁
        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            spinLockDemo.lock();
            spinLockDemo.unlock();
        }, "t2").start();

        //保证测试用例执行完
        TimeUnit.SECONDS.sleep(3);
    }
}