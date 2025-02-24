package org.project.exchange.model.currency.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.exchange.config.CurrencyApiProperties;
import org.project.exchange.model.currency.Currency;
import org.project.exchange.model.currency.Dto.CurrencyResponseDto;
import org.project.exchange.model.currency.repository.CurrencyRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final WebClient webClient;
    private final CurrencyApiProperties currencyApiProperties;

    public List<Currency> fetchAndSaveCurrency() {
        /**
         * 할 것
         * 오늘의 요일 구해서 만약 주말이면 금요일 날짜 반환
         * 만약 현재 시간이 11시 전이면 어제 날짜 반환
         *
         */
        LocalDate myDate = LocalDate.now();
        LocalTime myTime = LocalTime.now();
        DayOfWeek dayOfWeek = myDate.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || myTime.isBefore(LocalTime.of(11, 0))) {
            myDate = myDate.minusDays(1);
        }
        else if (dayOfWeek == DayOfWeek.SUNDAY) {
            myDate = myDate.minusDays(2);
        }
        String formatedNow = myDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String url = String.format("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?data=AP01&authkey=%s&searchdate=%s",
                currencyApiProperties.getKey(),
                formatedNow);
        log.info(url);
        //공공데이터 API에서 JSON 데이터 가져오기
        List<CurrencyResponseDto> responseDtoList = webClient.get()
                .uri(url)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is3xxRedirection()) {
                        return Mono.justOrEmpty(clientResponse.headers().asHttpHeaders().getLocation())
                                .flatMap(location -> {
                                    try {
                                        URI redirectUri = location.isAbsolute() ? location : new URI("https://www.koreaexim.go.kr" + location.toString());
                                        log.info("리다이렉트 URL: {}", redirectUri);
                                        return webClient.get()
                                                .uri(redirectUri)
                                                .retrieve()
                                                .bodyToMono(new ParameterizedTypeReference<List<CurrencyResponseDto>>() {});
                                    } catch (Exception e) {
                                        log.error("리다이렉트 URL 변환 실패: {}", e.getMessage());
                                        return Mono.empty();
                                    }
                                });
                    }
                    return clientResponse.bodyToMono(new ParameterizedTypeReference<List<CurrencyResponseDto>>() {});
                })
                .block();

        if (responseDtoList == null) {
            log.error("API 응답이 null입니다. URL: {}", url);
            throw new RuntimeException("API 응답이 null입니다.");
        }
        log.info(responseDtoList.toString());
        List<Currency> currencyList = responseDtoList.stream()
                .map(CurrencyResponseDto::toEntity)
                .collect(Collectors.toList());
        currencyRepository.deleteAll();
        return currencyRepository.saveAll(currencyList);
    }
    
    public List<CurrencyResponseDto> findAllCurrency(){
        List<Currency> currencies = currencyRepository.findAll();
        return currencies.stream()
                .map(CurrencyResponseDto::new)
                .collect(Collectors.toList());
    }

}
