package com.toyproject.backend.Emitter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
@RequiredArgsConstructor
public class SseEmitterService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 30;


    private final EmitterRepository emitterRepository;

    public SseEmitter getEmitter(String taskId) {
        return emitterRepository.get(taskId);
    }

    //SSE Emitter를 생성하는 메소드
    private SseEmitter createEmitter(String id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        //생성된 SSE Emitter를 저장소에 저장
        emitterRepository.save(id, emitter);
        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> emitterRepository.deleteById(id));
        emitter.onError((e) -> emitterRepository.deleteById(id)); // 에러 발생 시
//

        return emitter;
    }

    public void sendEvent(String taskId) {
        // 먼저 클라이언트의 SseEmitter를 가져온다
        SseEmitter emitter = emitterRepository.get(taskId);
        if (emitter != null) {
            try {
                // 데이터를 클라이언트에게 실어보낸다.
                emitter.send(SseEmitter.event().id(String.valueOf(taskId)).name("pdf완료").data(taskId));
            } catch (IOException exception) {
                // 데이터 전송 중 오류가 발생하면 Emitter를 삭제하고 에러를 완료 상태로 처리
                emitterRepository.deleteById(taskId);
                emitter.completeWithError(exception);
            }
        }
    }
    public SseEmitter subscribePdf(String taskId) {
        SseEmitter emitter = createEmitter(taskId);
        try {
            // 503 방지 + 연결 확인용 dummy
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }
//    // 503 Service Unavailable 방지용 dummy event 전송
//    sendEventToClient(sseEmitter, emitterId, "EventStream Created. [email = " + email + " ]");

}
