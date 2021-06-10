import callable.MyCallable;
import org.junit.Test;
import other.SumTask;
import rejectedExecutionHandler.BlockingRejectedExecutionHandler;
import runnable.MyRunnable;
import runnable.MyThread;

import java.sql.Time;
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

    /**
     * 测试常用的线程池
     *
     * newFixedThreadPool：固定线程数，多余任务几乎可以无限存储，阻塞队列为LinkedBlockingQueue
     * newSingleThreadExecutor：只有一个线程，一次处理一个任务
     * newCachedThreadPool：无限分配线程去处理任务，阻塞队列为SynchronousQueue
     * @throws InterruptedException
     */
    @Test
    public void threadPool() throws InterruptedException {
        ExecutorService threadPool = null;
//        threadPool = Executors.newFixedThreadPool(5);
//        threadPool = Executors.newSingleThreadExecutor();
        threadPool = Executors.newCachedThreadPool();

        final long begin = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            final int int_temp = i;
            threadPool.execute(() -> {
                System.out.println(String.format("【%s】处理开始:%s", Thread.currentThread().getName(), int_temp));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {}
                System.out.println(String.format("【%s】处理完成:%s 毫秒:%s", Thread.currentThread().getName(), int_temp, System.currentTimeMillis() - begin));
            });
        }

        //等待其他线程完成
        while (((ThreadPoolExecutor) threadPool).getActiveCount() > 0) {
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("任务执行完毕");
    }

    /**
     * 自定义线程池属性，拒绝策略设置成阻塞
     * 结果：11个任务中，前两个先由核心线程执行，中间5个排满阻塞队列，然后接下来3个继续分配3个额外线程去执行，最后一个会因为拒绝策略而阻塞
     */
    @Test
    public void customRejectedExcutionHandler() throws InterruptedException {
        ExecutorService threadPool = new ThreadPoolExecutor(2, 5, 1, TimeUnit.SECONDS
                , new LinkedBlockingDeque<Runnable>(5)
                , Executors.defaultThreadFactory()
                , new BlockingRejectedExecutionHandler());

        final long begin = System.currentTimeMillis();

        //
        for (int i = 0; i < 11; i++) {
            final int int_temp = i;
            threadPool.execute(() -> {
                System.out.println(String.format("【%s】处理开始:%s", Thread.currentThread().getName(), int_temp));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {}
                System.out.println(String.format("【%s】处理完成:%s 毫秒:%s", Thread.currentThread().getName(), int_temp, System.currentTimeMillis() - begin));
            });
        }

        //等待其他线程完成
        while (((ThreadPoolExecutor) threadPool).getActiveCount() > 0) {
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("任务执行完毕");
    }

    /**
     * 测试死锁
     * 拓展：可以用jstack工具检测死锁情况，打印出：Found one Java-level deadlock
     */
    @Test
    public void testDeadLock() throws InterruptedException {
        Object lock1 = new Object();
        Object lock2 = new Object();

        new Thread(() -> {
            synchronized (lock1) {
                System.out.println(String.format("【%s】已获得lock1，1s后尝试获取lock2", Thread.currentThread().getName()));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {}
                synchronized (lock2) {
                    System.out.println(String.format("【%s】已获得lock1和lock2", Thread.currentThread().getName()));
                }
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (lock2) {
                System.out.println(String.format("【%s】已获得lock2，1s后尝试获取lock1", Thread.currentThread().getName()));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {}
                synchronized (lock1) {
                    System.out.println(String.format("【%s】已获得lock2和lock1", Thread.currentThread().getName()));
                }
            }
        }, "t2").start();

        //等待线程执行完毕
        while (Thread.activeCount() > 2) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 测试FutureTask.cancel(boolean)方法
     * 结论：并不能取消正在执行中的任务
     */
    @Test
    public void cancel() throws ExecutionException, InterruptedException {
        //准备线程池
        ExecutorService es = Executors.newFixedThreadPool(10);

        long beginTime = System.currentTimeMillis();

        FutureTask<Long> task = new FutureTask<Long>(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                System.out.println(String.format("【%s】call begin", Thread.currentThread().getName()));
                long begin = System.currentTimeMillis();
                while (System.currentTimeMillis() - begin < 5000) {
                }
                System.out.println(String.format("【%s】call end", Thread.currentThread().getName()));
                return 100L;
            }
        });
        es.execute(task);

        System.out.println("已提交");

        //<editor-fold desc="弄一个线程来后取消task">
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println("取消task");
//                    task.cancel(true);  // 这个不能取消正在执行的任务，会使get阻塞时抛出CancellationException异常
                    task.cancel(false);  // 这个不能取消正在执行的任务，会使get阻塞时抛出CancellationException异常
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //</editor-fold>

        try {
            System.out.println(String.format("结果：%s", task.get()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("被中断");
        } catch (CancellationException e) {
            e.printStackTrace();
            System.out.println("被取消");
        }

        //等待线程执行完毕
        TimeUnit.SECONDS.sleep(4);
    }

}

