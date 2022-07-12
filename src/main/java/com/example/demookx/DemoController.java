package com.example.demookx;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

@RestController
public class DemoController {

    @GetMapping("/verify")
    public String verify() throws IOException {
        String apiKey = "yourApi";
        String secretKey = "secretKey";
        String passphrase = "passPhrase";
        String timestamp = Instant.now().toString();

        String hmacUri = timestamp + "GET" + "/users/self/verify";
        String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey).hmacHex(hmacUri);
        var okAccessSign = Base64.getEncoder().encodeToString(hmac.getBytes());

//        HttpHeaders headers = new HttpHeaders();
//        headers.set("OK-ACCESS-KEY", apiKey);
//        headers.set("OK-ACCESS-SIGN", okAccessSign);
//        headers.set("OK-ACCESS-TIMESTAMP", timestamp);
//        headers.set("OK-ACCESS-PASSPHRASE", passphrase);
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//
//        RestTemplate restTemplate = new RestTemplate();
//        String okxUri = "https://www.okx.com";
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        ResponseEntity<String> response = restTemplate.exchange(okxUri, HttpMethod.GET, entity, String.class);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://www.okx.com");

        request.addHeader("OK-ACCESS-KEY", apiKey);
        request.addHeader("OK-ACCESS-SIGN", okAccessSign);
        request.addHeader("OK-ACCESS-TIMESTAMP", timestamp);
        request.addHeader("OK-ACCESS-PASSPHRASE", passphrase);

        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        String result = null;
        if (entity != null) {
            result = EntityUtils.toString(entity);
        }
        response.close();
        httpClient.close();

        return result;

    }


}
