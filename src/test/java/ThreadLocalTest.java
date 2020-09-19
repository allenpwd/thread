import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @author 门那粒沙
 * @create 2020-09-19 18:19
 **/
public class ThreadLocalTest {

    /**
     * ThreadLocal 变量在不同线程中相互独立，互不影响
     * @throws InterruptedException
     */
    @Test
    public void threadLocal() throws InterruptedException {

        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        threadLocal.set("this is parent");

        System.out.println(threadLocal.get());

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                System.out.println(threadLocal.get());
            }).start();
        }

        while (Thread.activeCount() > 2) {
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }


    /**
     * InheritableThreadLocal 变量能在父子线程之间共享
     * 如果使用的是线程池则会有问题，因为线程往往不是新建的而是上次用过的然后缓存起来的
     * @throws InterruptedException
     */
    @Test
    public void InheritableThreadLocal() throws InterruptedException, ExecutionException {

        ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
        threadLocal.set("this is parent");

        System.out.println(threadLocal.get());

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                System.out.println(threadLocal.get());
            }).start();
        }

        while (Thread.activeCount() > 2) {
            TimeUnit.MILLISECONDS.sleep(500);
        }

        System.out.println("使用线程池");

        //准备线程池 后5个用的是已创建的线程，threadLocal拿到的是上次的值（上次用完后没有销毁，重新使用时没有初始化）
        ExecutorService es = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            Future<?> future = es.submit(() -> {
                System.out.println(String.format("[%s]:%s", Thread.currentThread().getName(), threadLocal.get()));
                threadLocal.set(Thread.currentThread().getName());
            });
            future.get();
        }

    }

    /**
     * TransmittableThreadLocal 解决线程池变量传递问题
     * 在使用完这个线程的时候清除所有的localMap，在submit新任务的时候在重新重父线程中copy所有的Entry。然后重新给当前线程的t.inhertableThreadLocal赋值
     * @throws InterruptedException
     */
    @Test
    public void TransmittableThreadLocal() throws InterruptedException, ExecutionException {

        ThreadLocal<String> threadLocal = new TransmittableThreadLocal<>();
        threadLocal.set("this is parent");

        System.out.println(threadLocal.get());

        ExecutorService es = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            // 需要把Runnable包装成TtlRunnable才有效果
            Future<?> future = es.submit(TtlRunnable.get(() -> {
                System.out.println(String.format("[%s]:%s", Thread.currentThread().getName(), threadLocal.get()));
                threadLocal.set(Thread.currentThread().getName());
            }));
            future.get();
        }
    }
}
