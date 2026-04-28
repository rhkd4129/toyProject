package com.toyproject.backend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisService {
    private  final RedisTemplate<String,Object>redisTemplate;


    public void addEvent(String key , String value){
        redisTemplate.opsForList().rightPush("producer:group", value);
        return;
    }

    public void  getEvent(String key){
        return;

    }
}
