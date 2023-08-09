package com.test;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

/**
 * @program: springboot-tdengine
 * @ClassName MapDBTest
 * @author: ayetony miao
 * @create: 2023-08-09 10:13
 **/
public class MapDBTest {

    public static void main(String[] args) {
        DB db = DBMaker.memoryDB().make();
        ConcurrentMap map = db.hashMap("map").createOrOpen();
//        map.put("something", "here");
        System.out.println(map.get("something"));
//        db.close();
    }

}
