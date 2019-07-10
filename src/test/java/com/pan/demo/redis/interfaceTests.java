package com.pan.demo.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class interfaceTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void lookRedis() {

    }

    /**
     * 集群多机情况下，某个接口的返回值，采用redis完成接口缓存，缓存时间可以灵活设置，如何设计？
     *
     *  因为是集群的情况下，可能会出现数据脏读的情况，所以要加锁，保证所有接口返回数据的一致性。
     *
     *  考虑集群因素，在进入接口是去缓存中获取返回值，若没有则为获取数据的代码块加事务锁。锁时间则看程序执行情况，
     *  为避免死锁则为锁设置过期时间。
     * */
    @Test
    public void getValue() {
        String result = "";

        String key = "key";
        String lock = "lock";
        int LOCK_EXPIRE = 300; // ms
        //从缓存获取数据
        result = stringRedisTemplate.opsForValue().get(key);
        if(!"".equals(result) && null != result){
            System.out.println("成功从缓存中获取结果，并返回,结果["+result+"]");
        }else{
            System.out.println("缓存中没有结果，开始执行程序");

            redisTemplate.execute((RedisCallback) connection -> {

                long expireAt = System.currentTimeMillis() + LOCK_EXPIRE + 1;

                //获取锁  true成功  false失败
                Boolean acquire = connection.setNX(lock.getBytes(), String.valueOf(expireAt).getBytes());

                if (acquire) {
                    //获取数据放入缓存
                    stringRedisTemplate.opsForValue().set(key,"123",1,TimeUnit.HOURS);
                    return true;
                } else {

                    byte[] value = connection.get(lock.getBytes());

                    if (Objects.nonNull(value) && value.length > 0) {

                        long expireTime = Long.parseLong(new String(value));

                        if (expireTime < System.currentTimeMillis()) {
                            // 如果锁已经过期
                            byte[] oldValue = connection.getSet(lock.getBytes(), String.valueOf(System.currentTimeMillis() + LOCK_EXPIRE + 1).getBytes());
                            // 防止死锁
                            return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                        }
                    }
                }
                return false;
            });

            result = stringRedisTemplate.opsForValue().get(key);
            System.out.println("程序执行完成，结果["+result+"]");
        }
    }

}
