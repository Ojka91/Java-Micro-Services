package com.oscar.moviecatalogservice.resources;

import com.oscar.moviecatalogservice.models.CatalogItem;
import com.oscar.moviecatalogservice.models.Movie;
import com.oscar.moviecatalogservice.models.UserRating;
import com.oscar.ratingsdataservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        UserRating ratings = restTemplate.getForObject("http://RATINGS-RESOURCES/ratingsData/users/"+userId, UserRating.class);

       return ratings.getUserRating().stream().map(rating -> {
           Movie movie = restTemplate.getForObject("http://MOVIE-INFO-SERVICE/movies/" + rating.getMovieId(), Movie.class);
            //Same code above and below. Above using resttemplate (will be deprecated)
           //below using WebClient, async calls
         /*  Movie movie = webClientBuilder.build()
                   .get()
                   .uri("http://localhost:8081/movies/" + rating.getMovieId())
                   .retrieve()
                   .bodyToMono(Movie.class)
                   .block();
          */
           return new CatalogItem(movie.getName(), "Test", rating.getRating());
       }).collect(Collectors.toList());

    }
}
