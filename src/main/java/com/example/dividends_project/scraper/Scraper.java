package com.example.dividends_project.scraper;

import com.example.dividends_project.model.Company;
import com.example.dividends_project.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);

    ScrapedResult scrap(Company company);
}
