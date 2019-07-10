package com.pan.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static java.lang.System.out;

/**
 * 算法测试类
 *
 * Created by Pangaofeng on 2018/9/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AlgorithmTests {

    @Test
    public void testJia(){
        int i = 12;
        //i + (i - i * i)
        //(i + i) - (i * i)
        //先算运算符右边的
        System.out.println(i+=i-=i*=i);
    }

    @Test
    public void testWei() {
        int a = 1 << 2;
        out.println(a);

        int b = 2 & 2;
        out.println(b);

        int c = 4 & 2;
        out.println(c);

        int d = 1 & 2;
        out.println(d);
    }
}
