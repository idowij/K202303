package com.learn.study.domain;

import lombok.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@DynamicUpdate  // 변경된 필드만 적용
@DynamicInsert  // 같음
public class Pop {

	@Id
	@Column(name = "keyword")
	private String keyword;

    @Column(name = "view")
    private int view;

    
    @Builder
    public Pop(String keyword, int view) {
        this.keyword = keyword;
        this.view = view;
    }
}