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
     *  （1）换成Vector（性能差，直接在方法上加synchronized）
     *  （2）Collections.synchronizedList（可以看看这个工具类里提供了哪些同步封装方法，回首掏之发现这些被封装的玩意是线程不安全的）
     *  （3）CopyOnWriteArrayList：写时复制，读写分离（查询不需加锁，只有在写入/删除的时候，才会从原来的数据复制一个副本出来修改这个副本，最后把原数据替换成当前的副本。修改操作的同时，读操作不会被阻塞，而是继续读取旧的数据。）
     *      适合读多改少的场景，不适合写频繁且对象大的场景
     */
    @Test
    public void listNotSafe() throws InterruptedException {
        List<String> list = null;
        list = new ArrayList<>();//抛出异常：java.util.ConcurrentModificationException
        list = new CopyOnWriteArrayList<>();
        list = new Vector<>();
        list = Collections.synchronizedList(new ArrayList<>());

        final List<String> list_f = list;

        Thread thread = null;

        for (int i = 0; i < 20; i++) {
            final int num = i;

            Thread t = new Thread(() -> {
                list_f.add(String.valueOf(num) + "_" + UUID.randomUUID().toString().substring(0, 4));
                System.out.println(list_f);
            }, String.valueOf(num));
            t.start();

            if (i == 1) thread = t;
        }

        //测试并发环境下的迭代操作，结果：除了CopyOnWriteArrayList，其他三个都要加上自身锁，否则可能抛异常：ConcurrentModificationException
        thread.join();
        synchronized (list) {
            Iterator<String> iterator = list_f.iterator();
            while (iterator.hasNext()) {
                System.out.println(String.format("迭代操作：%s", iterator.next()));
            }
        }

        //让测试方法等待线程跑完再退出
        while (Thread.activeCount() > 2) {
            Thread.sleep(500);
        };

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
