package com.service;

import com.entity.Weather;
import com.mapper.tdengine.WeatherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author CuiXi
 * @version 1.0
 * @Description:
 * @date 2021/3/11 15:18
 */
@Service
@Transactional(transactionManager = "tdengineTransactionManager")
public class WeatherService {

    private SimpleDateFormat simpleDateFormat;

    @Autowired
    private WeatherMapper weatherMapper;

    public boolean init() {

        weatherMapper.createDB();
        weatherMapper.createTable();

        return true;
    }

    public List<Weather> query(Long limit, Long offset) {
        return weatherMapper.select(limit, offset);
    }

    public int save(int temperature, float humidity) {
        Weather weather = new Weather();
        weather.setTemperature(temperature);
        weather.setHumidity(humidity);
        return weatherMapper.insert(weather);
    }

    public int save(List<Weather> weatherList) {
        return weatherMapper.batchInsert(weatherList);
    }

    public int del(String time){
        try {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date parseDate = simpleDateFormat.parse(time);
            return weatherMapper.delByTs(parseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

}

