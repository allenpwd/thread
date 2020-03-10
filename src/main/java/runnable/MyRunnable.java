package runnable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pwd
 * @create 2018-12-15 23:38
 **/
public class MyRunnable implements Runnable {

    private Map<String, Object> map_result;

    public MyRunnable() {
        map_result = new HashMap<String, Object>();
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
        long rel = endTime - beginTime;
        map_result.put("rel", rel);
        System.out.println("thread[" + name + "] end--" + rel);
    }

    public Map<String, Object> getMap_result() {
        return map_result;
    }
}
