package com.example.dividends_project;

import com.example.dividends_project.model.Company;
import com.example.dividends_project.model.ScrapedResult;
import com.example.dividends_project.scraper.Scraper;
import com.example.dividends_project.scraper.YahooFinanceScraper;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DividendsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(DividendsProjectApplication.class, args);

//        Scraper scraper = new YahooFinanceScraper();
//        var result = scraper.scrap(Company.builder().ticker("COKE").build());
//        var result = scraper.scrapCompanyByTicker("MMM");
//        System.out.println(result);

        // Trie 예제 확인
//        Trie trie = new PatriciaTrie();
//        AutoComplete autoComplete = new AutoComplete(trie);
//        AutoComplete autoComplete1 = new AutoComplete(trie);
//
//        autoComplete.add("hello");
//        autoComplete1.add("hello");
//
//        System.out.println(autoComplete.get("hello"));
//        System.out.println(autoComplete1.get("hello"));

    }
}
