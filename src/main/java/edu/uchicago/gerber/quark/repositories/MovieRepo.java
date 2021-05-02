package edu.uchicago.gerber.quark.repositories;


import edu.uchicago.gerber.quark.models.Movie;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class MovieRepo extends AbstractRepo {

    @Inject
    DynamoDbClient dynamoDB;

    public List<Movie> findAll() {

        return dynamoDB.scanPaginator(scanRequest()).items().stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }

    public List<Movie> add(Movie movie) {
        dynamoDB.putItem(putRequest(movie));
        return findAll();
    }

    public Movie get(String id) {
        return transform(dynamoDB.getItem(getRequest(id)).item());
    }

    private Movie transform(Map<String, AttributeValue> item){
        Movie movie = new Movie();
        if (item != null && !item.isEmpty()) {

            movie.setId(item.get(AbstractRepo.MOVIE_ID_COL).s());
            movie.setTitle(item.get(AbstractRepo.MOVIE_TITLE_COL).s());
            movie.setYear(Integer.parseInt(item.get(AbstractRepo.MOVIE_YEAR_COL).n()));
        }
        return movie;
    }
}