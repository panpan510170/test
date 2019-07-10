package com.pan.demo.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class test {
    @Test
    public void contextLoads() {
        Map<Object, Object> objectObjectHashMap = new HashMap<>();
    }

    public static long getLong(Object obj, long defArg) {
        try {
            if(obj instanceof Number){
                return ((Number)obj).longValue();
            }
            return Long.parseLong(getString(obj, defArg));
        } catch (NumberFormatException e) {
            return defArg;
        }
    }

    public static String getString(Object obj, Object defStr) {
        return isEmpty(obj)?String.valueOf(defStr):String.valueOf(obj);
    }

    public static boolean isEmpty(Object object) {
        if (null == object) {
            return true;
        }
        if(object instanceof String){
            return "".equals(((String) object).trim());
        }
        else if(object instanceof Map){
            return ((Map<?, ?>) object).isEmpty();
        }
        else if(object instanceof Collection){
            return ((Collection<?>) object).isEmpty();
        }
        else if(object.getClass().isArray()){
            return Array.getLength(object)==0;
        }
        return false;
    }
}
