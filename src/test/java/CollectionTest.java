import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
     *  （1）换成Vector（性能差，synchronized）
     *  （2）Collections.synchronizedList（可以看看这个工具类里提供了哪些同步封装方法，回首掏之发现这些被封装的玩意是线程不安全的）
     *  （3）CopyOnWriteArrayList：写时复制，读写分离
     */
    @Test
    public void listNotSafe() throws InterruptedException {
//        List<String> list = new ArrayList<>();
//        List<String> list = new Vector<>();
//        List<String> list = Collections.synchronizedList(new ArrayList<>());
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 20; i++) {
            final int num = i;
            new Thread(() -> {
                list.add(String.valueOf(num) + "_" + UUID.randomUUID().toString().substring(0, 4));
                System.out.println(list);
            }, String.valueOf(num)).start();
        }

        //保证线程跑完
        TimeUnit.SECONDS.sleep(1);

        System.out.println("----------------result---------------");
        System.out.println(list);
    }

    /**
     * 测试HashMap线程不安全
     * 说明：多个线程同时map.put，结果是不确定的，可能抛出异常：java.util.ConcurrentModificationException
     * 解决方案：
     *  （1）Collections.synchronizedMap（可以看看这个工具类里提供了哪些同步封装方法，回首掏之发现这些被封装的玩意是线程不安全的）
     *  （2）ConcurrentHashMap
     */
    @Test
    public void mapNotSafe() throws InterruptedException {
//        Map<String, Object> map = new HashMap<>();
//        Map<String, Object> map = Collections.synchronizedMap(new HashMap<>());
        Map<String, Object> map = new ConcurrentHashMap<>();

        for (int i = 0; i < 20; i++) {
            final int num = i;
            new Thread(() -> {
                map.put(Thread.currentThread().getName(), String.valueOf(num) + "_" + UUID.randomUUID().toString().substring(0, 4));
                System.out.println(map);
            }, String.valueOf(num)).start();
        }

        //保证线程跑完
        TimeUnit.SECONDS.sleep(1);

        System.out.println("----------------result---------------");
        System.out.println(map);

    }
}
