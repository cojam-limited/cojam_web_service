package io.cojam.web.klaytn.service;

import io.cojam.web.klaytn.dto.KlayPagination;
import io.cojam.web.klaytn.dto.KlayTransactionDTO;
import io.cojam.web.klaytn.dto.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Service
public class TransactionApiService {

    @Value("${app.sdk-enclave.masterWalletId")
    private String masterWalletId;

    @Qualifier("transactionClient")
    @Autowired
    private RestTemplate restTemplate;

    public TransactionStatus getTransactionStatusById(String transactionId) {
        return this.restTemplate.getForObject(
                "/transactions/" + transactionId,
                TransactionStatus.class
        );
    }


    public KlayTransactionDTO getTransactionById(String transactionId) {
        // TODO Auto-generated method stub
        return this.restTemplate.getForObject(
                "/transactions/" + transactionId,
                KlayTransactionDTO.class
        );
    }


    public KlayPagination<KlayTransactionDTO> getTxCallEvents(String transactionId, String status, String startTime,
                                                              String endTime, Pageable pagable) {
        // TODO Auto-generated method stub
        KlayPagination<KlayTransactionDTO> result = this.restTemplate.exchange(
                "/api/v2/klay/call-events?masterWalletId={masterWalletId}&statuses={statuses}" +
                        "&updatedAtGte={updatedAtGte}&updatedAtLt={updatedAtLt}" +
                        "&page={page}&size={size}&sort=updatedAt,asc", HttpMethod.GET, null,
                new ParameterizedTypeReference<KlayPagination<KlayTransactionDTO>>() {
                }, Stream.of(
                        new AbstractMap.SimpleEntry<String, Object>("masterWalletId", masterWalletId),
                        new AbstractMap.SimpleEntry<String, Object>("statuses", status),
                        new AbstractMap.SimpleEntry<String, Object>("updatedAtGte", startTime),
                        new AbstractMap.SimpleEntry<String, Object>("updatedAtLt", endTime),
                        new AbstractMap.SimpleEntry<String, Object>("page", pagable.getPageNumber()),
                        new AbstractMap.SimpleEntry<String, Object>("size", pagable.getPageSize())
                ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .getBody();
        return result;
    }

    public KlayPagination<KlayTransactionDTO> getValueTransferEvents(String transactionId, String status, String startTime,
                                                                     String endTime, Pageable pagable) {
        // TODO Auto-generated method stub
        KlayPagination<KlayTransactionDTO> result = this.restTemplate.exchange(
                "/api/v2/klay/value-transfer-events?masterWalletId={masterWalletId}&statuses={statuses}" +
                        "&updatedAtGte={updatedAtGte}&updatedAtLt={updatedAtLt}" +
                        "&page={page}&size={size}&sort=updatedAt,asc", HttpMethod.GET, null,
                new ParameterizedTypeReference<KlayPagination<KlayTransactionDTO>>() {
                }, Stream.of(
                        new AbstractMap.SimpleEntry<String, Object>("masterWalletId", masterWalletId),
                        new AbstractMap.SimpleEntry<String, Object>("statuses", status),
                        new AbstractMap.SimpleEntry<String, Object>("updatedAtGte", startTime),
                        new AbstractMap.SimpleEntry<String, Object>("updatedAtLt", endTime),
                        new AbstractMap.SimpleEntry<String, Object>("page", pagable.getPageNumber()),
                        new AbstractMap.SimpleEntry<String, Object>("size", pagable.getPageSize())
                ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .getBody();
        return result;
    }
}
