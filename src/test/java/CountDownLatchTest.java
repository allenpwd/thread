import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * CountDownLock：让线程都等待直到其他线程都完成工作，不可循环使用
 * 使用场景：多线程计算数据，最后合并计算结果
 * @author 门那粒沙
 * @create 2020-03-12 9:36
 **/
public class CountDownLatchTest {

    /**
     *
     */
    @Test
    public void test() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(5);

        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                System.out.println(String.format("【%s】\t上完自习，离开教室", Thread.currentThread().getName()));
                countDownLatch.countDown();
            }, "t" + i).start();
        }

        //这里会被阻塞直到countDown被执行了5次
        countDownLatch.await();
        System.out.println(String.format("【%s】\t班长最后关门走人", Thread.currentThread().getName()));
    }
}
