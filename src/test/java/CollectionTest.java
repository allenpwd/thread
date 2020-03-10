import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 门那粒沙
 * @create 2020-03-10 23:16
 **/
public class CollectionTest {

    /**
     * 测试ArrayList线程不安全
     * 说明：多个线程同时List.add，结果是不确定的，可能抛出异常：java.util.ConcurrentModificationException
     * 解决方案：
     *  换成Vector（性能差，synchronized）
     *  Collections.synchronizedList（可以看看这个工具类里提供了哪些同步封装方法，回首掏之发现这些被封装的玩意是线程不安全的）
     */
    @Test
    public void testList() throws InterruptedException {
//        List<String> list = new ArrayList<>();
//        List<String> list = new Vector<>();
        List<String> list = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0, 8));
                System.out.println(list);
            }, String.valueOf(i)).start();
        }

        //保证线程跑完
        TimeUnit.SECONDS.sleep(1);
    }
}
