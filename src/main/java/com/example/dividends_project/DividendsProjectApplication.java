package com.example.dividends_project;

import com.example.dividends_project.model.Company;
import com.example.dividends_project.model.ScrapedResult;
import com.example.dividends_project.scraper.Scraper;
import com.example.dividends_project.scraper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DividendsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(DividendsProjectApplication.class, args);

//        Scraper scraper = new YahooFinanceScraper();
//        var result = scraper.scrap(Company.builder().ticker("COKE").build());
//        var result = scraper.scrapCompanyByTicker("MMM");
//        System.out.println(result);

    }
}
