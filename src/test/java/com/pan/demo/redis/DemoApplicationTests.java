package com.pan.demo.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //红包名称
    private String hb_key = "hb";// + System.currentTimeMillis();

    @Test
    public void contextLoads() {
        //查看红包
        List<BigDecimal> range = redisTemplate.opsForList().range(hb_key, 0, -1);

        System.out.println(range);
    }

    /**
     * 100元发红包，生成N个红包，每个用户每次只能抢到一个红包，红包10分钟过期，如何设计
     *
     *  生成红包
     * */
    @Test
    public void hb() {
        long start = System.currentTimeMillis();
        System.out.println("开始时间："+System.currentTimeMillis());

        //红包总金额
        BigDecimal hbAmount = new BigDecimal("100");

        //红包数量
        Integer hbCount = 5;

        //红包名称
        //String hb_key = "hb" + System.currentTimeMillis();

        //红包最小金额
        BigDecimal hbMinAmount = new BigDecimal("0.1");

        //初始化红包
        List<BigDecimal> list = new ArrayList();

        //红包剩余金额
        BigDecimal hbRemainAmount = hbAmount.subtract(hbMinAmount.multiply(new BigDecimal(hbCount)));

        //允许红包分配的最小金额
        BigDecimal min =new BigDecimal("0.01");

        for (int i = 0; i < hbCount; i++) {

            //随机分配红包金额  最后一个红包不随机
            BigDecimal result = BigDecimal.ZERO;
            if(i != hbCount-1){
                int randomNumber = new Random().nextInt((hbRemainAmount.multiply(new BigDecimal(100))).intValue());
                result = new BigDecimal(randomNumber + 1).divide(new BigDecimal(100));
            }
            hbRemainAmount = hbRemainAmount.subtract(result);
            if(i != hbCount-1){
                list.add(hbMinAmount.add(result));
            }else{
                list.add(hbMinAmount.add(hbRemainAmount));
            }
        }

        System.out.println(list);

        //将红包放入缓存
        Long index = redisTemplate.opsForList().leftPushAll(hb_key, list);
        redisTemplate.expire(hb_key,10, TimeUnit.MINUTES);

        long end = System.currentTimeMillis();
        System.out.println("结束时间："+end +"；耗时["+(end-start)+"]毫秒");

    }

    /**
     * 100元发红包，生成N个红包，每个用户每次只能抢到一个红包，红包10分钟过期，如何设计
     *
     *  领取红包
     *
     * */
    @Test
    public void getHb() {
        long start = System.currentTimeMillis();
        System.out.println("开始时间："+System.currentTimeMillis());
        //用户id
        Long userId = 000003L;

        //判断用户是否第一次领取红包
        Long increment = stringRedisTemplate.opsForValue().increment(hb_key + "_" + userId,1);
        System.out.println("计数：" + increment);

        stringRedisTemplate.expire(hb_key + "_" + userId,10, TimeUnit.MINUTES);
        if(increment == 1){
            BigDecimal amount = (BigDecimal)redisTemplate.opsForList().rightPop(hb_key);

            System.out.println("红包金额：" + amount);
        }else{

            System.out.println("已经领取过了");
        }

        long end = System.currentTimeMillis();
        System.out.println("结束时间："+end +"；耗时["+(end-start)+"]毫秒");
    }

}
