package com.pan.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pangaofeng on 2018/10/9
 *
 * 抢红包--简单
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HongBaoTest {

    @Test
    public void testHonbao() {

        //金额，个数，最少值
        List<BigDecimal> hb = hb(6, 4, 0.01);
        System.out.println("list=="+hb);
        //zb();

    }

    List<BigDecimal> hb(double total, int num, double min) {
        List<BigDecimal> list = new ArrayList<>();
        for (int i = 1; i < num; i++) {

            double safe_total = (total - (num - i) * min) / (num - i);
            System.out.println("safe_total==="+safe_total);
            double money = Math.random() * (safe_total - min) + min;

            BigDecimal money_bd = new BigDecimal(money);

            money = money_bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            total = total - money;

            BigDecimal total_bd = new BigDecimal(total);

            total = total_bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            System.out.println("第" + i + "个红包：" + money + ",余额为:" + total + "元");
            list.add(new BigDecimal(money+""));
        }

        System.out.println("第" + num + "个红包：" + total + ",余额为:0元");
        list.add(new BigDecimal(total+""));
        return list;
    }


    void zb() {

        for (int a = 0; a <= 10000; a++) {

            if (a % 1000 == 0)

                System.out.println(a);

        }

    }
}
