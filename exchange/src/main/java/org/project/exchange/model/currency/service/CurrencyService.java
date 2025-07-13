package org.project.exchange.model.currency.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.exchange.config.CurrencyApiProperties;
import org.project.exchange.model.currency.Currency;
import org.project.exchange.model.currency.Dto.CurrencyFetchResponseDto;
import org.project.exchange.model.currency.Dto.CurrencyInfoResponseDto;
import org.project.exchange.model.currency.Dto.CurrencyResponseDto;
import org.project.exchange.model.currency.repository.CurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

// 환율 데이터 가져오기
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyApiProperties currencyApiProperties;

    public List<Currency> fetchAndSaveCurrency() {
        trustAllCertificates(); // HTTPS 인증 우회 (기존 유지)

        List<Currency> currencyList = new ArrayList<>();
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        StringBuilder responseContent = new StringBuilder();
        JSONParser parser = new JSONParser();

        LocalDate myDate = LocalDate.now();
        String formattedNow = myDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String urlStr = String.format(
                "https://oapi.koreaexim.go.kr/site/program/financial/exchangeJSON?data=AP01&authkey=%s&searchdate=20250430",
                currencyApiProperties.getKey(), formattedNow
        );

        log.info("Fetching currency data from URL: {}", urlStr);

        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false); // 리디렉션 자동 처리
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int status = connection.getResponseCode();
            log.info("HTTP status: {}", status);

            InputStreamReader streamReader = null;

            if (status != 200) {
                log.error("HTTP 상태 코드 200 아님: {}", status);
                throw new RuntimeException("HTTP 오류 상태: " + status);
            }

            if (connection.getInputStream() == null) {
                log.warn("HTTP 200인데 inputStream이 null, 데이터 없음");
                return Collections.emptyList();  // 조용히 성공 종료 (배치 실패 아님, 휴일인 경우)
            }

            streamReader = new InputStreamReader(connection.getInputStream());
            reader = new BufferedReader(streamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }

            // JSON 파싱
            JSONArray jsonArray = (JSONArray) parser.parse(responseContent.toString());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            for (Object obj : jsonArray) {
                String jsonString = objectMapper.writeValueAsString(obj);
                CurrencyFetchResponseDto dto = objectMapper.readValue(jsonString, CurrencyFetchResponseDto.class);

                // 유효성 체크
                if (dto.getDealBasR() == null || dto.getCurUnit() == null || dto.getCurNm() == null) {
                    log.warn("불완전한 데이터: {}", dto);
                    continue;
                }

                Currency currency = currencyRepository.findByCurUnit(dto.getCurUnit())
                        .map(existing -> {
                            existing.updateDealBasR(getParsedDealBasR(dto.getDealBasR()));
                            existing.updateCreatedAt(myDate);
                            return existing;
                        })
                        .orElseGet(() -> Currency.builder()
                                .curUnit(dto.getCurUnit())
                                .dealBasR(getParsedDealBasR(dto.getDealBasR()))
                                .curNm(dto.getCurNm())
                                .createdAt(myDate)
                                .build());

                currencyList.add(currency);
            }

        } catch (IOException | ParseException e) {
            log.error("환율 데이터 수집 중 오류 발생", e);
            throw new RuntimeException("환율 API 응답 파싱에 실패했습니다.", e);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) {}

            if (connection != null) connection.disconnect();
        }

        return currencyRepository.saveAll(currencyList);
    }


    public List<CurrencyResponseDto> findAllCurrency(){
        List<Currency> currencies = currencyRepository.findAll();
        return currencies.stream()
                .map(CurrencyResponseDto::new)
                .collect(Collectors.toList());
    }

    public Double getParsedDealBasR(String dealBasR) {
        try {
            return Double.valueOf(dealBasR.replace(",", ""));
        } catch (NumberFormatException e) {
            log.error("Failed to parse dealBasR: {}", dealBasR, e);
            return 0.0; // 기본값 처리
        }
    }

    public CurrencyInfoResponseDto getDealBasR(String curUnit) {
        Currency currency = currencyRepository.findByCurUnit(curUnit)
                .orElseThrow(() -> new IllegalArgumentException("해당 통화가 존재하지 않습니다."));
        return new CurrencyInfoResponseDto(curUnit, currency.getDealBasR());
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            throw new RuntimeException("SSL 설정 실패", e);
        }
    }

}
