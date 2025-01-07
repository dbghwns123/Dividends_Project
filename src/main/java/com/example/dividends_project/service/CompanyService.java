package com.example.dividends_project.service;

import com.example.dividends_project.exception.impl.NoCompanyException;
import com.example.dividends_project.model.Company;
import com.example.dividends_project.model.ScrapedResult;
import com.example.dividends_project.persist.CompanyRepository;
import com.example.dividends_project.persist.DividendRepository;
import com.example.dividends_project.persist.entity.CompanyEntity;
import com.example.dividends_project.persist.entity.DividendEntity;
import com.example.dividends_project.scraper.Scraper;
import com.example.dividends_project.scraper.YahooFinanceScraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }

        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }

    // LIKE 를 사용하여 자동완성 기능 구현하기
    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }


    // 회사명 자동완성 (저장)
    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    // 회사명 자동완성 (조회)
    public List<String> autocomplete(String keyword) {
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                //.limit(10)
                .collect(Collectors.toList());
    }

    // trie 에 저장된 키워드를 삭제
    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoCompanyException());

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        // 자동완성 기능을 위해서 trie 에 저장해놓았던 company 이름도 지워줘야 함
        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }

}
