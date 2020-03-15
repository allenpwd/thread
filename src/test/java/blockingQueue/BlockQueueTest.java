package blockingQueue;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author 门那粒沙
 * @create 2020-03-13 14:58
 **/
public class BlockQueueTest {

    /**
     * 测试 ArrayBlockingQueue：由数组支持的有界队列
     *  add(e)：当队列满时会抛异常（java.lang.IllegalStateException: Queue full）
     *  remove()：当队列空时会跑异常：（java.util.NoSuchElementException）
     *  element()：返回队首元素，当队列为空，抛异常（java.util.NoSuchElementException）
     *  offer(e)：当队列满时返回false
     *  peek()：返回队首元素，当队列为空时返回null
     *  poll()：当队列空时返回null
     */
    @Test
    public void ArrayBlockingQueue() throws InterruptedException {
        ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(2);

        System.out.println(blockingQueue.add("a"));//设值，队列满时抛异常
        blockingQueue.element();//返回队首元素，队列空时抛异常
        System.out.println(blockingQueue.remove("a"));
        System.out.println(blockingQueue.remove("b"));//对象不存在时返回false，而不是抛异常

        System.out.println(blockingQueue.peek());//返回队首元素，队列空时返回null
        System.out.println(blockingQueue.offer("b"));//设值，队列满时返回false
        blockingQueue.offer("c", 1, TimeUnit.SECONDS);//设值，队列满时等待1s，若仍然满时返回false
        System.out.println(blockingQueue.poll());//取出队首元素，队列为空时返回null

        blockingQueue.put("c");//设值，队列满时阻塞
        System.out.println(blockingQueue.take());//取出队首元素，队列为空时阻塞
    }

    /**
     * 测试Synchronousqueue：不存储元素的队列，同时有插入和取出时才不会阻塞，即有消费才生产，有生产才消费
     * 结果：put(e)会阻塞直到take()被调用；take()也会阻塞直到put(e)被调用；
     * @throws InterruptedException
     */
    @Test
    public void Synchronousqueue() throws InterruptedException {
        SynchronousQueue<String> queue = new SynchronousQueue<>();

        //t1线程put3次
        new Thread(() -> {
            try {
                queue.put("a");
                System.out.println("put：a");
                queue.put("b");
                System.out.println("put：b");
                queue.put("c");
                System.out.println("put：c");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        //t12线程take3次
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println("take:" + queue.take());
                TimeUnit.SECONDS.sleep(2);
                System.out.println("take:" + queue.take());
                TimeUnit.SECONDS.sleep(2);
                System.out.println("take:" + queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();

        TimeUnit.SECONDS.sleep(7);
    }
}
