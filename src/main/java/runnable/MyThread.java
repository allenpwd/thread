package runnable;

import java.util.Map;

/**
 * @author pwd
 * @create 2018-12-16 10:30
 **/
public class MyThread extends Thread {

    private Map<String, Object> params;
    private Object rel;

    public MyThread(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public void run() {
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
    }

    public Object getRel() {
        return rel;
    }
}
