package com.toyproject.backend.Emitter;

import com.toyproject.backend.config.properties.SseProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class SseEmitterService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 30;

    private final EmitterRepository emitterRepository;
    private final SseProperties sseProperties;

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

    public void sendEvent(String taskId, String resultPath,String status) {
        String filePath = Paths.get(resultPath).getFileName().toString();
        record PdfCompleteEvent(String taskId, String filePath , String status) {}
        SseEmitter emitter = emitterRepository.get(taskId);
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .id(taskId)
                                //.name("pdf완료")
                                .name(sseProperties.getPdfComplete())
                                .data(new PdfCompleteEvent(taskId, filePath,status), MediaType.APPLICATION_JSON)
                );
            } catch (IOException exception) {
                emitterRepository.deleteById(taskId);
                emitter.completeWithError(exception);
            }
        }
    }
    public void sendErrorMessage(String taskId, String status , String errorMessage) {
        record PdfCompleteEvent(String taskId, String status, String errorMessage) {
        }
        SseEmitter emitter = emitterRepository.get(taskId);
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .id(taskId)
                                .name(sseProperties.getPdfComplete())
                                .data(new PdfCompleteEvent(taskId, status, errorMessage), MediaType.APPLICATION_JSON)
                );
            } catch (IOException exception) {
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
                    //.name("connect")
                    .name(sseProperties.getConnect())
                    .data("connected"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }
//    // 503 Service Unavailable 방지용 dummy event 전송
//    sendEventToClient(sseEmitter, emitterId, "EventStream Created. [email = " + email + " ]");

}
