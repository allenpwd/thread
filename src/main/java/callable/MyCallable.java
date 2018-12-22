package callable;

import java.util.concurrent.Callable;

/**
 * @author pwd
 * @create 2018-12-16 13:09
 **/
public class MyCallable implements Callable<Long> {

    public Long call() throws Exception {
        long rel = 0;
        String name = Thread.currentThread().getName();
        long beginTime = System.currentTimeMillis();
        System.out.println("thread[" + name + "] begin--");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        long endTime = System.currentTimeMillis();
        rel = endTime - beginTime;
        System.out.println("thread[" + name + "] end--" + rel);
        return rel;
    }
}
