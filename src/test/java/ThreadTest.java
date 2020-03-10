import callable.MyCallable;
import org.junit.Test;
import runnable.MyRunnable;
import runnable.MyThread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author pwd
 * @create 2018-12-15 20:24
 **/
public class ThreadTest {

    //region 多线程执行10个任务
    /**
     * Runnable + Thread
     * @throws InterruptedException
     */
    @Test
    public void threadWithReturn() throws InterruptedException {
        long beginTime = System.currentTimeMillis();
        List<MyThread> list_thread = new ArrayList<MyThread>();

        //多线程跑任务
        for (int i = 0; i < 10; i++) {
            MyThread myThread = new MyThread(null);
            myThread.start();
            list_thread.add(myThread);
        }

        //等待任务跑完 方式一
//        boolean ifEnd = false;
//        while(!ifEnd) {
//            Iterator<MyThread> iterator = list_thread.iterator();
//            while (iterator.hasNext()) {
//                MyThread thread = iterator.next();
//                if (thread.getState() == Thread.State.TERMINATED) {
//                    System.out.println("thread[" + thread.getName() + "]返回值--" + thread.getRel());
//                    iterator.remove();
//                }
//            }
//            if (list_thread.size() == 0) ifEnd = true;
//        }

        //等待任务跑完 方式二
        for (MyThread thread : list_thread) {
            thread.join();
            System.out.println("thread[" + thread.getName() + "]返回值--" + thread.getRel());
        }

        //统计总时长
        long endTime = System.currentTimeMillis();
        System.out.println("总计时长--" + (endTime - beginTime));

    }


    @Test
    /**
     * futureTask + callable
     */
    public void callableWithReturn() {
        //准备线程池
        ExecutorService es = Executors.newFixedThreadPool(10);

        long beginTime = System.currentTimeMillis();
        List<Future> list_task = new ArrayList<Future>();

        //<editor-fold desc="多线程跑任务">
        //方式一
//        for (int i = 0; i < 10; i++) {
//            FutureTask<Long> task = new FutureTask<Long>(new MyCallable());
//            es.execute(task);
//            list_task.add(task);
//
//            /*//另一种添加线程的方法
//            Future<Long> submit = es.submit(new MyCallable());
//            list_task.add(submit);*/
//        }
//        //等待任务跑完
//        for (int i = 0; i < list_task.size(); i++) {
//            Future<Long> task = list_task.get(i);
//            Long rel = null;
//            try {
//                rel = task.get();//阻塞直到有返回值
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            System.out.println("task[" + i + "]返回值--" + rel);
//        }
        //方式二 使用invokeAll一次性提交任务，该方法阻塞
        ArrayList<Callable<Long>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new MyCallable());
        }
        try {
            List<Future<Long>> futures = es.invokeAll(list);
            for (int i = 0; i < 10; i++) {
                Future<Long> future = futures.get(i);
                Long rel = future.get();
                System.out.println("task[" + i + "]返回值--" + rel);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        //统计总时长
        long endTime = System.currentTimeMillis();
        System.out.println("总计时长--" + (endTime - beginTime));
    }


    @Test
    /**
     * runnable + futureTask
     */
    public void runnableWithReturn() {
        //准备线程池
        ExecutorService es = Executors.newFixedThreadPool(10);

        long beginTime = System.currentTimeMillis();
        List<FutureTask> list_task = new ArrayList<FutureTask>();

        //多线程跑任务
        for (int i = 0; i < 10; i++) {
            MyRunnable myRunnable = new MyRunnable();
            FutureTask task = new FutureTask(myRunnable, myRunnable.getMap_result());
            es.execute(task);
            list_task.add(task);
        }

        //等待任务跑完
        for (int i = 0; i < list_task.size(); i++) {
            Future<Map<String, Object>> task = list_task.get(i);
            Map<String, Object> map_result = null;
            try {
                map_result = task.get();//阻塞直到有返回值
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("thread[" + i + "]返回值--" + map_result.get("rel"));

        }

        //统计总时长
        long endTime = System.currentTimeMillis();
        System.out.println("总计时长--" + (endTime - beginTime));
    }
    //endregion


    /**
     * 用fork join方式统计1+2+...1000
     * 说明：好像不常用啊
     */
    @Test
    public void forkAndJoin() {
        int[] arr_int = new int[1000];
        for (int i = 0; i < arr_int.length; i++) {
            arr_int[i] = i + 1;
        }

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        SumTask sumTask = new SumTask(arr_int, 0, arr_int.length - 1);

        long start = System.currentTimeMillis();

        forkJoinPool.invoke(sumTask);//同步调用
        System.out.println("Task is Running.....");

        System.out.println("The count is "+sumTask.join());
        System.out.println("spend time:"+(System.currentTimeMillis()-start)+"ms");
    }
}

