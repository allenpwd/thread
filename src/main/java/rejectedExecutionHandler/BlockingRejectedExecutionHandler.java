package rejectedExecutionHandler;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义拒绝执行策略：阻塞式地将任务添加至工作队列中
 *
 * @author hasee
 *
 */
public class BlockingRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            try {
                // 使用阻塞方法向工作队列中添加任务
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                executor.execute(r);
            }

        }
    }
}
