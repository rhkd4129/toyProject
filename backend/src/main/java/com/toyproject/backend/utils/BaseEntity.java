package com.toyproject.backend.utils;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
//엔티티의 변화를 감지하여 엔티티가 수정되거나 생성될 때 자동으로 시간을 기록하게 해주는 JPA의 이벤트 리스너

@MappedSuperclass
//이 어노테이션이 붙은 클래스는 실제 테이블로 생성되지 않고, 다른 엔티티 클래스들이 상속받아 사용할 수 있는 공통 매핑 정보를 제공
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

