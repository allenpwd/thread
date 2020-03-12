import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * 循环栅栏：让线程都等待直到大家都到齐了才会继续下一步行动，可循环使用
 * 使用场景：多线程计算数据，最后合并计算结果
 * @author 门那粒沙
 * @create 2020-03-12 15:17
 **/
public class CyclicBarrierTest {

    @Test
    public void test() throws InterruptedException {
        //需要await被调用7次才能trip掉栅栏，第二个参数指定栅栏triped后回调的命令
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> {
            System.out.println("集齐七颗龙珠，召唤神龙");
        });

        //CyclicBarrier是可重复使用的，这里重复使用两次
        for (int n = 0; n < 2; n++) {
            for (int i = 1; i <= 7; i++) {
                final int int_temp = i;
                new Thread(() -> {
                    try {
                        System.out.println(String.format("【%s】收集到龙珠%s", Thread.currentThread().getName(), int_temp));
                        cyclicBarrier.await();
                        System.out.println(String.format("【%s】召唤神龙后，做些事情", Thread.currentThread().getName()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }, "t" + int_temp).start();
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
