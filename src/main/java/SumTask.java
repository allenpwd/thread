import java.util.concurrent.RecursiveTask;

/**
 * 实现数字之和
 *
 * @author 门那粒沙
 * @create 2020-03-10 20:50
 **/
public class SumTask extends RecursiveTask<Integer> {

    /**
     * fork的阀值
     */
    private int threshold = 10;
    /**
     * 表示我们要实际统计的数组
     */
    private int[] src;
    /**
     * 开始统计的下标
     */
    private int fromIndex;
    /**
     * 统计到哪里结束的下标
     */
    private int toIndex;

    public SumTask(int[] src, int fromIndex, int toIndex) {
        this.src = src;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.threshold = src.length / 10;
    }

    @Override
    protected Integer compute() {
        //小于阀值 则 直接计算并返回结果
        if (toIndex - fromIndex < threshold) {
            int count = 0;
            for (int i = fromIndex; i <= toIndex; i++) {
                count = count + src[i];
            }
            return count;
        } else {//拆分成小任务
            int mid = (fromIndex + toIndex) / 2;
            SumTask left = new SumTask(src, fromIndex, mid);
            SumTask right = new SumTask(src, mid + 1, toIndex);
            invokeAll(left, right);
            return left.join() + right.join();
        }
    }
}
