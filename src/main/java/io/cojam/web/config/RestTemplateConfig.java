package io.cojam.web.config;

import io.cojam.web.auth.HMACService;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import static java.time.temporal.ChronoField.MILLI_OF_SECOND;

@Configuration
public class RestTemplateConfig {
    // Determines the timeout in milliseconds until a connection is established.
    private static final int CONNECT_TIMEOUT = 30000;
    // The timeout when requesting a connection from the connection manager.
    private static final int REQUEST_TIMEOUT = 30000;

    public static final String X_HENESIS_SIGNATURE = "X-Henesis-Signature";
    public static final String X_HENESIS_TIMESTAMP = "X-Henesis-Timestamp";
    public static final String X_HENESIS_SECRET = "X-Henesis-Secret";
    @Autowired
    private HMACService hmacService;

    @Value("${app.sdk-enclave.accessToken}")
    private String sdkToken;
    @Value("${app.sdk-enclave.apiSecret}")
    private String sdkApiSecret;
    @Value("${app.sdk-enclave.url}")
    private String sdkEnclaveUrl;
    @Value("${app.sdk-enclave.masterWalletId}")
    private String sdkMasterWalletId;

    @Value("${app.sdk-enclave.recommendWalletId}")
    private String recommendWalletId;

    @Value("${app.sdk-enclave.eventWalletId}")
    private String eventWalletId;

    @Value("${firebase.key}")
    private String FCM_KEY;

    @Value("${firebase.url}")
    private String FCM_URL;

    @Bean
    public RestTemplate defaultRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory());
        return restTemplate;
    }

    @Bean
    @Qualifier("walletClient")
    public RestTemplate walletRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(
                String.format("%s/api/v2/klay/wallets/%s", sdkEnclaveUrl, sdkMasterWalletId)));
        restTemplate.setInterceptors(
                Collections.singletonList(new HeaderInterceptor())
        );
        return restTemplate;
    }

    @Bean
    @Qualifier("recommendWalletClient")
    public RestTemplate recommendWalletRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(
                String.format("%s/api/v2/klay/wallets/%s", sdkEnclaveUrl, recommendWalletId)));
        restTemplate.setInterceptors(
                Collections.singletonList(new HeaderInterceptor())
        );
        return restTemplate;
    }

    @Bean
    @Qualifier("eventWalletClient")
    public RestTemplate eventWalletRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(
                String.format("%s/api/v2/klay/wallets/%s", sdkEnclaveUrl, eventWalletId)));
        restTemplate.setInterceptors(
                Collections.singletonList(new HeaderInterceptor())
        );
        return restTemplate;
    }


    @Bean
    @Qualifier("transactionClient")
    public RestTemplate transactionClient() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(
                String.format("%s/api/v2/klay", sdkEnclaveUrl)
        ));
        restTemplate.setInterceptors(
                Collections.singletonList(new HeaderInterceptor())
        );
        return restTemplate;
    }

    @Bean
    @Qualifier("coinoneClient")
    public RestTemplate coinoneRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(
                String.format("https://api.coinone.co.kr")));
        return restTemplate;
    }

    @Bean
    @Qualifier("burnClient")
    public RestTemplate burnRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(
                String.format("%s/api/v2/klay/wallets/%s", sdkEnclaveUrl, "9541385ad29cf60d347e39f991f497f7")));
        restTemplate.setInterceptors(
                Collections.singletonList(new HeaderInterceptor())
        );
        return restTemplate;
    }

    @Bean
    @Qualifier("fcmClient")
    public RestTemplate fcmRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(
                String.format("%s", FCM_URL)));
        restTemplate.setInterceptors(
                Collections.singletonList(new FcmHeaderInterceptor())
        );
        return restTemplate;
    }


    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient());
        return clientHttpRequestFactory;
    }

    private CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private class HeaderInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            HttpHeaders headers = request.getHeaders();
            String timestamp = String.valueOf(new Date().getTime());
            headers.add(X_HENESIS_SECRET, sdkApiSecret);
            headers.add(X_HENESIS_TIMESTAMP, timestamp);
            headers.add(X_HENESIS_SIGNATURE, createSignature(request, body, timestamp));
            headers.add("Authorization", "Bearer " + sdkToken);
            return execution.execute(request, body);
        }
    }

    private class FcmHeaderInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            HttpHeaders headers = request.getHeaders();
            headers.add("Content-Type","application/json");
            headers.add("Authorization", "key=" + FCM_KEY);
            return execution.execute(request, body);
        }
    }

    private String createSignature(HttpRequest request, byte[] body, String timestamp) {
        String path = request.getURI().toString();
        String msg = request.getMethod() + path + new String(body) + timestamp;
        return hmacService.calculateHMAC(sdkApiSecret, msg);
    }

}



