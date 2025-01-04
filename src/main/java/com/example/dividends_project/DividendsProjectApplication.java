package com.example.dividends_project;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

//@SpringBootApplication
public class DividendsProjectApplication {

    public static void main(String[] args) {
//        SpringApplication.run(DividendsProjectApplication.class, args);

//        try {
//            // 그냥 돌릴시 오류 -> User-Agent 추가 (.header ~)
//            Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history/?frequency=1mo&period1=1704288048&period2=1735898922&filter=history")
//                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
//            Document document = connection.get();
//
//            System.out.println(document.html());
//
//            Elements eles = document.getElementsByAttributeValue("data-testid", "history-table");
//            if (!eles.isEmpty()) {
//                Element ele = eles.get(0); // table 전체
//
//                Element tbody = ele.children().get(1);
//                for (Element e : tbody.children()) {
//                    String txt = e.text();
//                    if (!txt.endsWith("Dividend")) {
//                        continue;
//                    }
//                    String[] splits = txt.split(" ");
//                    String month = splits[0];
//                    int day = Integer.valueOf(splits[1].replace(",", ""));
//                    int year = Integer.valueOf(splits[2]);
//                    String dividend1 = splits[3];
//                }
//
////                System.out.println(ele);
//            } else {
//                System.out.println("데이터를 찾을 수 없습니다.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        try {
            // User-Agent 헤더 추가
            Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history/?frequency=1mo&period1=1704288048&period2=1735898922&filter=history")
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            Document document = connection.get();

            // 테이블 가져오기
            Elements eles = document.getElementsByAttributeValue("data-testid", "history-table");
            if (!eles.isEmpty()) {
                Element ele = eles.get(0); // 테이블 전체

                // tbody 가져오기 (첫 번째 tbody만 사용)
                Elements tbodyList = ele.select("tbody");
                if (!tbodyList.isEmpty()) {
                    Element tbody = tbodyList.get(0); // 첫 번째 tbody

                    // tbody 내부의 모든 tr 태그 순회
                    for (Element tr : tbody.children()) {
                        // "Dividend" 텍스트를 포함한 tr 태그 필터링
                        Element eventCell = tr.select("td.event").first();
                        if (eventCell != null && eventCell.text().contains("Dividend")) {
                            // 날짜 및 배당금 추출
                            String date = tr.child(0).text(); // 첫 번째 td에서 날짜 추출
                            String dividend = eventCell.select("span").text(); // 배당금 추출

                            String txt = date + " " + dividend + " Dividend";

                            String[] splits = txt.split(" ");
                            String month = splits[0];
                            int day = Integer.valueOf(splits[1].replace(",", ""));
                            int year = Integer.valueOf(splits[2]);
                            String dividend1 = splits[3];

                            System.out.println(year + "/" + month + "/" + day + " -> " + dividend1);
                        }
                    }
                } else {
                    System.out.println("tbody를 찾을 수 없습니다.");
                }
            } else {
                System.out.println("테이블을 찾을 수 없습니다.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
