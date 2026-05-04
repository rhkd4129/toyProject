package com.toyproject.backend.Emitter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {
    // 모든 Emitters를 저장하는 ConcurrentHashMap -> 여기서 이걸 쓴 이유 .
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    //Emitters 저장
    public void save(String id, SseEmitter emitter) {
        emitters.put(id, emitter);
    }
    //Emitter 제거
    public void deleteById(String id) {
        emitters.remove(id);
    }
    //Emitter 가져오기
    public SseEmitter get(String id) {
        return emitters.get(id);
    }

}
