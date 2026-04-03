package com.toyproject.backend.common;

import jakarta.persistence.*;
import lombok.Getter;

// 시간 정보 + 사용 여부를 가진 베이스 엔티티
@Getter
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity {
    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private UseStatus useYn = UseStatus.Y;

    protected void changeUseStatus(UseStatus status) {
        this.useYn = status;
    }
}
