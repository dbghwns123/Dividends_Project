package com.example.dividends_project.persist;

import com.example.dividends_project.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    boolean existsByTicker(String ticker);

    Optional<CompanyEntity> findByName(String name);

    Optional<CompanyEntity> findByTicker(String ticker);

    // LIKE 연산을 사용하여 자동완성 기능 사용하기 위한 메서드
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);

}
