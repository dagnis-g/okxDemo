package com.example.demookx;

import lombok.extern.slf4j.Slf4j;
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
import java.util.Arrays;
import java.util.Base64;

@RestController
@Slf4j
public class DemoController {

    @GetMapping("/verify")
    public String verify() throws IOException {
        String apiKey = "";
        String secretKey = "";
        String passphrase = "";

        String instant = Instant.now().toString();

        final String timestamp = instant.substring(0, instant.length() - 7) + "Z";

        String uriPath = "/api/v5/account/balance";

        String hmacUri = timestamp + "GET" + uriPath;
        String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey).hmacHex(hmacUri);
        var okAccessSign = Base64.getEncoder().encodeToString(hmac.getBytes());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://www.okx.com/" + uriPath);

        request.addHeader("OK-ACCESS-KEY", apiKey);
        request.addHeader("OK-ACCESS-SIGN", "sign=" + okAccessSign);
//        request.addHeader("OK-ACCESS-SIGN", okAccessSign);
        request.addHeader("OK-ACCESS-TIMESTAMP", timestamp);
        request.addHeader("OK-ACCESS-PASSPHRASE", passphrase);
//        request.addHeader("Content-type", "application/json");

        log.info(Arrays.toString(request.getAllHeaders()));

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
