package com.example.dividends_project.scraper;

import com.example.dividends_project.model.Company;
import com.example.dividends_project.model.Dividend;
import com.example.dividends_project.model.ScrapedResult;
import com.example.dividends_project.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper{

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history/?frequency=1mo&period1=%d&period2=%d";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s";
    private static final long START_TIME = 86400;  // 60 * 60 * 24

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);


        try {
            long now = System.currentTimeMillis() / 1000;

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            // User-Agent 헤더 추가
            Connection connection = Jsoup.connect(url)
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            Document document = connection.get();

            // 테이블 가져오기
            Elements parsingDivs = document.getElementsByAttributeValue("data-testid", "history-table");
            if (!parsingDivs.isEmpty()) {
                Element tableEle = parsingDivs.get(0); // 테이블 전체

                // tbody 가져오기 (첫 번째 tbody만 사용)
                Elements tbodyList = tableEle.select("tbody");
                if (!tbodyList.isEmpty()) {
                    Element tbody = tbodyList.get(0); // 첫 번째 tbody

                    // tbody 내부의 모든 tr 태그 순회
                    List<Dividend> dividends = new ArrayList<>();
                    for (Element tr : tbody.children()) {
                        // "Dividend" 텍스트를 포함한 tr 태그 필터링
                        Element eventCell = tr.select("td.event").first();
                        if (eventCell != null && eventCell.text().contains("Dividend")) {
                            // 날짜 및 배당금 추출
                            String date = tr.child(0).text(); // 첫 번째 td에서 날짜 추출
                            String dividend = eventCell.select("span").text(); // 배당금 추출

                            String txt = date + " " + dividend + " Dividend";

                            String[] splits = txt.split(" ");
                            // String month 값을 int 형으로 변환(enum -> Month 사용)
                            int month = Month.strToNumber(splits[0]);
                            int day = Integer.valueOf(splits[1].replace(",", ""));
                            int year = Integer.valueOf(splits[2]);
                            String dividend1 = splits[3];

                            if (month < 0) {
                                throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                            }

                            dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend1));


//                            System.out.println(year + "/" + month + "/" + day + " -> " + dividend1);
                        }
                    }
                    scrapResult.setDividends(dividends);
                } else {
                    System.out.println("tbody를 찾을 수 없습니다.");
                }
            } else {
                System.out.println("테이블을 찾을 수 없습니다.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker);

        try {
            Document document = Jsoup.connect(url)
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();

            // h1 태그 내 텍스트 정확히 추출
            Element titleEle = document.select("h1.yf-xxbei9").first();
            if (titleEle == null) {
                throw new IllegalStateException("Company name element not found");
            }

            // 텍스트 추출 및 처리
            String title = titleEle.text(); // 예: "3M Company (MMM)"
            String companyName = title.split(" \\(")[0].trim(); // "3M Company" 추출
            return new Company(ticker, companyName);
//            return Company.builder()
//                    .ticker(ticker)
//                    .name(companyName)
//                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
