package com.learn.study.infrastructure.persistence;

import com.learn.study.domain.Pop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlogRepository extends JpaRepository<Pop, String> {
	
    @Modifying
    @Query("update Pop p set p.view = p.view + 1 where p.keyword = :keyword")
    int incView(@Param("keyword") String keyword);

    List<Pop> findTop10ByOrderByViewDesc();
}
