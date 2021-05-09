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
public class MovieDdbRepo extends AbstractDdbRepo {

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
        Map<String, AttributeValue> item;
        item =dynamoDB.getItem(getRequest(id)).item();
        if (null == item || item.size() == 0){
            return null;
        }
        return transform(item);
    }


}