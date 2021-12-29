import org.junit.Assert;
import org.junit.Test;
import org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class LRUTest {
    @Test
    public void addSomeDataToCache_WhenGetData_ThenIsEqualWithCacheElement(){
        LRUCache<String,String> lruCache = new LRUCache<>(3);
        lruCache.put("1","test1");
        lruCache.put("2","test2");
        lruCache.put("3","test3");
        Assert.assertEquals("test1",lruCache.get("1").get());
        Assert.assertEquals("test2",lruCache.get("2").get());
        Assert.assertEquals("test3",lruCache.get("3").get());
    }
    @Test
    public void runMultiThreadTask_WhenPutDataInConcurrentToCache_ThenNoDataLost() throws Exception {
        final int size = 50;
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        Cache<Integer, String> cache = new LRUCache<>(size);
        CountDownLatch countDownLatch = new CountDownLatch(size);
        try {
            IntStream.range(0, size).<Runnable>mapToObj(key -> () -> {
                cache.put(key, "value" + key);
                countDownLatch.countDown();
            }).forEach(executorService::submit);
            countDownLatch.await();
        } finally {
            executorService.shutdown();
        }
        Assert.assertEquals(cache.size(), size);
        IntStream.range(0, size).forEach(i -> Assert.assertEquals("value" + i,cache.get(i).get()));
    }
    @Test
    public void addDataToCacheToTheNumberOfSize_WhenAddOneMoreData_ThenLeastRecentlyDataWillEvict(){
        LRUCache<String,String> lruCache = new LRUCache<>(3);
        lruCache.put("1","test1");
        lruCache.put("2","test2");
        lruCache.put("3","test3");
        lruCache.put("4","test4");
        Assert.assertFalse(lruCache.get("1").isPresent());
    }
}
