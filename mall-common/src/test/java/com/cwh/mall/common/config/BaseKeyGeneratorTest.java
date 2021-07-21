package com.cwh.mall.common.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cwh
 * @date 2021/7/20 10:47
 */
class BaseKeyGeneratorTest {

    @Test
    public void testStringBuilder(){
        ArrayList<Integer> arrayList = new ArrayList();
        for (int i = 0; i <10; i++){
            arrayList.add(i);
        }
        System.out.println(arrayList.toString());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("begin:");
        arrayList.stream().map(stringBuilder::append).collect(Collectors.toList());
        System.out.println(stringBuilder.toString());
    }

}