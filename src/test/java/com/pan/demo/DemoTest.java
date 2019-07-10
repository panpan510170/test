package com.pan.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author pan
 * @date 2019/6/5 10:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test() {
        List<Integer> integerList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            stringList.add(i + "");
        }
        //滤重  昨天的主播

        //随机抽取一个用户
        Random random = new Random();
        int n = random.nextInt(stringList.size());
        stringList.get(n);

        //缓存记录
        Long increment = redisTemplate.opsForHash().increment("key", "userId", 1);
        Long score = increment <= 10 ? increment : increment % 10;
    }
}
