package com.pan.demo.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetUserIdListTests {

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
     * 用户列表，根据用户进入房间时间顺序和消费等级倒序排序，只要求能拿到房间用户ID列表，如何设计？
     *
     *   这里有一个最大时间 MAX_TIME = 9999999999999 （13位）
     *
     *   A 用户 10 + （MAX_TIME - 11111111111111）（ 时间戳），最终分数 888888888888898
     *
     *   B 用户 10 + （MAX_TIME - 22222222222222）（ 时间戳），最终分数 777777777777787
     * */
    @Test
    public void selectSendRanking() {
        //房间id
        Long roomId = 696969L;

        Long MAX_TIME = 9999999999999L;

        //创建用户在线缓存   value:用户id   score：消费等级+时间戳
        Set<ZSetOperations.TypedTuple<String>> userSet = new HashSet<>();

        Date date = new Date("2019/3/18 18:20:00");
        long time = MAX_TIME - date.getTime() + 10L;
        ZSetOperations.TypedTuple<String> tuple0 = new DefaultTypedTuple<String>("111111", Double.parseDouble(time+""));
        userSet.add(tuple0);

        Date date1 = new Date("2019/3/18 18:20:00");
        long time1 = MAX_TIME - date1.getTime() + 11L;
        ZSetOperations.TypedTuple<String> tuple1 = new DefaultTypedTuple<String>("111112", Double.parseDouble(time1+""));
        userSet.add(tuple1);

        Date date2 = new Date("2019/3/19 18:20:00");
        long time2 = MAX_TIME - date2.getTime() + 10L;
        ZSetOperations.TypedTuple<String> tuple2 = new DefaultTypedTuple<String>("111113", Double.parseDouble(time2+""));
        userSet.add(tuple2);

        Date date3 = new Date("2019/3/17 18:20:00");
        long time3 = MAX_TIME - date3.getTime() + 10L;
        ZSetOperations.TypedTuple<String> tuple3 = new DefaultTypedTuple<String>("111114", Double.parseDouble(time3+""));
        userSet.add(tuple3);

        stringRedisTemplate.opsForZSet().add("onLine_ranking_"+roomId,userSet);

        Set<String> strings = stringRedisTemplate.opsForZSet().reverseRange("onLine_ranking_" + roomId, 0, -1);

        System.out.println(strings);
    }

}
