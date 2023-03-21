package com.learn.study.apllication;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.study.domain.Pop;
import com.learn.study.infrastructure.persistence.BlogRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    /* search */
    @Transactional
    public Pop incView(Pop pop) {
    	int result = blogRepository.incView(pop.getKeyword());
        if(result == 0){//기존 키워드 없으면 insert
        	blogRepository.save(pop);
        }
        return pop;
    }
    
    @Transactional
    public List<Pop> rank() {
        return blogRepository.findTop10ByOrderByViewDesc();
    }
}