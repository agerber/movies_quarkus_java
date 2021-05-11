package edu.uchicago.gerber.quark.repositories;


import edu.uchicago.gerber.quark.models.Movie;
import edu.uchicago.gerber.quark.services.MovieServiceInterface;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class MovieDynamodbRepo extends AbstractDynamodbRepo implements MovieServiceInterface {

    @Inject
    DynamoDbClient dynamoDB;

    public List<Movie> findAll() {

        return dynamoDB.scanPaginator(scanRequest()).items().stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }
    @Override
    public List<Movie> add(Movie movie) {
        dynamoDB.putItem(putRequest(movie));
        return findAll();
    }
    @Override
    public Movie get(String id) {
        Map<String, AttributeValue> item;
        item =dynamoDB.getItem(getRequest(id)).item();
        if (null == item || item.size() == 0){
            return null;
        }
        return transform(item);
    }

    //for dynamodb
    protected Movie transform(Map<String, AttributeValue> item){
        Movie movie = new Movie();
        if (item != null && !item.isEmpty()) {

            movie.setId(item.get(AbstractDynamodbRepo.MOVIE_ID_COL).s());
            movie.setTitle(item.get(AbstractDynamodbRepo.MOVIE_TITLE_COL).s());
            movie.setYear(Integer.parseInt(item.get(AbstractDynamodbRepo.MOVIE_YEAR_COL).n()));
        }
        return movie;
    }
    @Override
    public List<Movie> paged(int page) {
        //just return the entire recordset for Ddb for now. See mongoDB implementation of paged.
        return  findAll();
    }


}