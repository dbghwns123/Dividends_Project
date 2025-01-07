package com.example.dividends_project.service;

import com.example.dividends_project.exception.impl.NoCompanyException;
import com.example.dividends_project.model.Company;
import com.example.dividends_project.model.Dividend;
import com.example.dividends_project.model.ScrapedResult;
import com.example.dividends_project.model.constants.CacheKey;
import com.example.dividends_project.persist.CompanyRepository;
import com.example.dividends_project.persist.DividendRepository;
import com.example.dividends_project.persist.entity.CompanyEntity;
import com.example.dividends_project.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 요청이 자주 들어오는가?
    // 자주 변경되는 데이터인가?
    // 캐시 사용 (Redis)
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {

        log.info("search company -> " + companyName);

        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                // .orElseThrow()는 값이 없으면 인자로 넘겨주는 예외를 발생식키고 값이 있으면 Optional이 벗겨진 CompanyEntity를 반환함
                .orElseThrow(() -> new NoCompanyException());


        // 2. 조회된 회사 ID 로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환

        // for 문을 사용하여 값 넣기
//        List<Dividend> dividends = new ArrayList<>();
//        for (var entity : dividendEntities) {
//            dividends.add(Dividend.builder()
//                    .date(entity.getDate())
//                    .dividend(entity.getDividend())
//                    .build());
//        }

        // stream 을 사용하여 값 넣기
        List<Dividend> dividends = dividendEntities.stream()
                .map( e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());

        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);
    }
}
