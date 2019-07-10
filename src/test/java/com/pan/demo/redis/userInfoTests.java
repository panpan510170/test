package com.pan.demo.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class userInfoTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void lookRedis() {
        //房间id
        Long roomId = 696969L;

        Set set = redisTemplate.opsForZSet().reverseRangeWithScores(roomId, 0, -1);

        System.out.println(set);
    }

    /**
     * 用户信息三个属性，ID昵称性别，数据缓存，数据变更不删除Redis，并考虑同时修改昵称和性别的情况，如何设计？
     * */
    @Test
    public void selectSendRanking() {
        //用户id
        Long userId = 111113L;

        //初始化数据
        Map<String,Object> map = new HashMap();
        map.put("id",userId.toString());
        map.put("nickName","水范先生");
        map.put("sex","1");
        stringRedisTemplate.opsForHash().putAll(userId.toString(),map);

        LinkedHashSet r = (LinkedHashSet) stringRedisTemplate.opsForHash().keys(userId.toString());
        System.out.println(r);
        //获取用户数据 并修改
        String nickName = (String)stringRedisTemplate.opsForHash().get(userId.toString(),"nickName");
        String sex = (String)stringRedisTemplate.opsForHash().get(userId.toString(),"sex");
        System.out.println(nickName);
        System.out.println(sex);

        stringRedisTemplate.opsForHash().put(userId.toString(),"nickName","水番先生");
        stringRedisTemplate.opsForHash().put(userId.toString(),"sex","2");

        //结果
        String nickName2 = (String)stringRedisTemplate.opsForHash().get(userId.toString(),"nickName");
        String sex2 = (String)stringRedisTemplate.opsForHash().get(userId.toString(),"sex");
        System.out.println(nickName2);
        System.out.println(sex2);
    }

}
