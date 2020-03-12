import org.junit.Test;
import other.MyCache;

import java.util.concurrent.TimeUnit;

/**
 * 多个线程同时读一个资源类没有任何问题，所以为了满足并发量，读取共享资源应该可以同时进行。
 * 但是如果有一个线程象取写共享资源来，就不应该自由其他线程可以对资源进行读或写
 * 总结
 *  读读能共存
 *  读写不能共存
 *  写写不能共存
 */
public class ReadWriteLockTest {

    /**
     * 测试MyCache的读写
     * 说明：开多线程同时读，同时写
     * 结果：写只能一个一个来；当没有线程在写时，读能多个同时读，否则会阻塞
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        MyCache myCache = new MyCache();
        for (int i = 1; i <= 5; i++) {
            final int tempInt = i;
            new Thread(() -> {
                myCache.put(tempInt + "", tempInt + "");
            }, "Thread " + i).start();
        }
        for (int i = 1; i <= 5; i++) {
            final int tempInt = i;
            new Thread(() -> {
                myCache.get(tempInt + "");
            }, "Thread " + i).start();
        }
        for (int i = 1; i <= 5; i++) {
            final int tempInt = i;
            new Thread(() -> {
                myCache.put(tempInt + "", tempInt * 2);
            }, "Thread====" + i).start();
        }

        TimeUnit.SECONDS.sleep(2000);
    }

}

