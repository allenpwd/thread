package Lock;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 信号标：控制线程的并发数量
 * @author 门那粒沙
 * @create 2020-03-13 9:43
 **/
public class SemaphoreTest {

    /**
     * 使用SemapPhore模拟抢车位
     */
    @Test
    public void test() throws InterruptedException {
        Semaphore semaphore = new Semaphore(3);

        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                try {
                    //会阻塞直到permit被允许
                    semaphore.acquire();
                    System.out.println(String.format("【%s】抢到车位", Thread.currentThread().getName()));
                    //模拟占用车位2秒
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(String.format("【%s】离开车位", Thread.currentThread().getName()));
                    //释放permit
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "t" + i).start();
        }

        TimeUnit.SECONDS.sleep(4);
    }
}
