package com.example.controller;

import com.example.dto.Movie;
import com.github.jenspiegsa.wiremockextension.ConfigureWireMock;
import com.github.jenspiegsa.wiremockextension.InjectServer;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.example.constants.MoviesAppConstants.GET_ALL_MOVIES_V1;
import static com.example.constants.TestConstants.HTTP_LOCALHOST;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {RestTemplate.class})
@ExtendWith(WireMockExtension.class)
class Controller1Test {

    @Autowired
    RestTemplate moviesRestClient;

    @ConfigureWireMock
    Options options = wireMockConfig()
            .port(8091)
            .notifier(new ConsoleNotifier(true)) //enables WireMock console output.
            .extensions(new ResponseTemplateTransformer(true)); //enables response templating.

    @InjectServer
    WireMockServer wireMockServer;

    @Test
    void getAllMovies() {

        //given
        stubFor(get(WireMock.anyUrl())
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBodyFile("all-movies.json")));
        //when
        ResponseEntity<List<Movie>> response = moviesRestClient.exchange(
                HTTP_LOCALHOST + GET_ALL_MOVIES_V1,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}); //List<Movie>

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Movie> movieList = response.getBody();
        assertNotNull(movieList);
        assertFalse(movieList.isEmpty());
    }
}
