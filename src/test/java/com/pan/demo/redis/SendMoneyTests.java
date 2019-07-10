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

import java.math.BigDecimal;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SendMoneyTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void lookRedis1() {
        double aa = 0.0;
        if(aa <= 0){
            System.out.println("123213");
        }

        Map<String,Object> map = new HashMap();
        int userId = 7;
        Long broadId = 56L;
        map.put("userId",userId);
        map.put("broadId",broadId);
        long b = (long)map.get("userId");
        Long c = (Long)map.get("broadId");

        System.out.println(b);
        System.out.println(c);
    }
    @Test
    public void lookRedis() {
        //房间id
        Long roomId = 696969L;

        Set<ZSetOperations.TypedTuple<String>> set = stringRedisTemplate.opsForZSet().rangeWithScores("intersection_696969", 0, -1);
        Long intersection_696969 = stringRedisTemplate.opsForZSet().zCard("intersection_696969");
        System.out.println(intersection_696969);
        System.out.println(set);
        System.out.println("1-------------------------------------------");
        //1.迭代遍历：
        /*Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String str = it.next();
            System.out.println(str);
        }*/
        System.out.println("2-------------------------------------------");
        //2.for循环遍历：
        for (ZSetOperations.TypedTuple<String> str : set) {
            System.out.println(str.getScore());
            System.out.println(str.getValue());
            System.out.println("**************");
        }

        if(!set.isEmpty()){
            set.iterator().next().getValue();
            System.out.println(set.iterator().next().getValue());// 1.2
            System.out.println(set.iterator().next().getScore());// 1.2
        }

        List list = new ArrayList(set);
        DefaultTypedTuple d = (DefaultTypedTuple)list.get(0);
        System.out.println(d.getScore());
        System.out.println(d.getValue());
    }

    /**
     * 送礼成功后，房间送礼榜单实时计算用户送礼总金额排名，并且不在房间的用户无排名，如何设计？
     *
     *  2部分：
     * 1.用户送礼成功，根据金额将用户送礼数据存入redis中。
     * 2.前端获取用户送礼总金额排名，先获取user_on_房间id redis缓存，
     * 然后获取send_money_房间id redis缓存，获取它们的交集，在根据金额倒叙排列，返回给前端。
     *
     *
     *
     * 方法二   权重匹配
     * 当用户进入直播间先增加1亿的score值
     *
     * */
    @Test
    public void contextLoads() {
        //房间id
        Long roomId = 696969L;
        //用户id
        Long userId = 111116L;
        //送礼金额
        BigDecimal sendAmount = new BigDecimal(150);

        //判断用户是否送过礼
        Double score = stringRedisTemplate.opsForZSet().score(roomId.toString(), userId.toString());
        if(score != null){
            stringRedisTemplate.opsForZSet().incrementScore(roomId.toString(),userId.toString(),Double.parseDouble(sendAmount.toString()));
        }else{
            stringRedisTemplate.opsForZSet().add(roomId.toString(),userId.toString(),Double.parseDouble(sendAmount.toString()));
        }

        //按照score值倒叙排列
        Set set = stringRedisTemplate.opsForZSet().reverseRange(roomId.toString(), 0, -1);

        System.out.println(set);
    }

    @Test
    public void selectSendRanking() {
        //房间id
        Long roomId = 696969L;

        //创建用户在线缓存
        Set<ZSetOperations.TypedTuple<String>> userSet = new HashSet<>();
        ZSetOperations.TypedTuple<String> tuple0 = new DefaultTypedTuple<String>("111116", 1d);
        userSet.add(tuple0);
        ZSetOperations.TypedTuple<String> tuple1 = new DefaultTypedTuple<String>("111111", 2d);
        userSet.add(tuple1);
        ZSetOperations.TypedTuple<String> tuple2 = new DefaultTypedTuple<String>("111115", 3d);
        userSet.add(tuple2);
        ZSetOperations.TypedTuple<String> tuple3 = new DefaultTypedTuple<String>("111114", 4d);
        userSet.add(tuple3);
        stringRedisTemplate.opsForZSet().add("onLine_"+roomId,userSet);

        stringRedisTemplate.opsForZSet().intersectAndStore(roomId.toString(),"onLine_"+roomId,"intersection_"+roomId);

    }
}
