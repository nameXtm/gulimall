package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 1、引入oss-starter
 * 2、配置key，endpoint相关信息即可
 * 3、使用OSSClient 进行相关操作
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired

    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    RedissonClient redissonClient;

    @Autowired
    CategoryService categoryService;

    @Test
    public void testFindPath(){
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径：{}",Arrays.asList(catelogPath));
    }


    @Test
    public void contextLoads() {

//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("华为");

//
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功....");

//        brandService.updateById(brandEntity);


        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item) -> {
            System.out.println(item);
        });


    }
    @Test
    public void textstringRedisTemplate(){

        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        //保存
        ops.set("hello","world_" + UUID.randomUUID().toString());

        //查询
        String helloaa = ops.get("hello");
        System.out.println("之前保存的数据:"+helloaa);
    }


    @Test
    public void  redisson() throws InterruptedException {
      //获取一把锁，名字一样就是一把锁
        RLock lock = redissonClient.getLock("my-lock");

//        lock.lock();//阻塞式等待（一直等）
//        //锁的自动续期，业务只要完成就不会续期，就会释放锁，


        lock.lock(10, TimeUnit.SECONDS);//10秒自动解锁，自动解锁时间一定要大于业务时间  在锁时间时间到了不会自动续期

        //最佳实战
        //1 lock.lock(10, TimeUnit.SECONDS);省掉了续期操作，手动解锁





        try{
            //2加锁

            System.out.println("加锁成功");
            Thread.sleep(30000);
        }finally {
            //3.解锁  假设解锁代码没有执行，redisson会不会出现死锁。
            System.out.println("释放锁" +Thread.currentThread().getId());
            lock.unlock();
        }

        //读写锁(改数据加写锁，读数据加读锁)  保证一定能读到最新数据（修改时间是互斥锁或排它锁）
        //写锁没释放，读锁就读不到
        //1.写锁
        RReadWriteLock lock1 = redissonClient.getReadWriteLock("lock");

        RLock rLock = lock1.writeLock();
        rLock.lock();
        rLock.unlock();

        //2读锁
        RLock rLock1 = lock1.readLock();
        rLock1.lock();
        rLock1.unlock();

        //闭锁
        RCountDownLatch lock2 = redissonClient.getCountDownLatch("lock");
        //   lock2.countDown(); 执行5次后闭锁完成；
        lock2.trySetCount(5);
        lock2.await();//等待闭锁都完成
        lock2.countDown();//每执行一次 lock2.trySetCount(5) 减1   lock2.trySetCount(5); 5次后闭锁完成

        //信号测量



        System.out.printf("redissonClient", redissonClient);


    }
}
