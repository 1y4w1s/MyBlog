package com.my.blog.utils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {
    private BeanCopyUtils(){}
    
    public static <V> V copyBean(Object source, Class<V> clazz){
        V result = null;
        try{
            result = clazz.newInstance();
            BeanUtils.copyProperties(source, result);
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    
    public static <O, V> List<V> copyBeanList(List<O> list, Class<V> clazz){
        return list.stream()
            .map(item -> copyBean(item, clazz))
            .collect(Collectors.toList());
    }
    
    public static <V> List<V> copyList(List<?> list, Class<V> clazz){
        return list.stream()
            .map(item -> copyBean(item, clazz))
            .collect(Collectors.toList());
    }
}
