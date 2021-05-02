package edu.uchicago.gerber.quark.repositories;


import edu.uchicago.gerber.quark.models.Movie;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRepo {

    public static final String MOVIE_ID_COL = "id";
    public static final String MOVIE_TITLE_COL = "title";
    public static final String MOVIE_YEAR_COL = "year";

    public String getTableName() {
        return "Movies";
    }

    protected ScanRequest scanRequest() {
        return ScanRequest.builder().tableName(getTableName())
                .attributesToGet(MOVIE_ID_COL, MOVIE_TITLE_COL,MOVIE_YEAR_COL ).build();
    }

    protected PutItemRequest putRequest(Movie movie) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(MOVIE_ID_COL, AttributeValue.builder().s(movie.getId()).build());
        item.put(MOVIE_TITLE_COL, AttributeValue.builder().s(movie.getTitle()).build());
        item.put(MOVIE_YEAR_COL, AttributeValue.builder().n(movie.getId()).build());

        return PutItemRequest.builder()
                .tableName(getTableName())
                .item(item)
                .build();
    }

    protected GetItemRequest getRequest(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(MOVIE_ID_COL, AttributeValue.builder().s(id).build());

        return GetItemRequest.builder()
                .tableName(getTableName())
                .key(key)
                .attributesToGet(MOVIE_ID_COL, MOVIE_TITLE_COL,MOVIE_YEAR_COL )
                .build();
    }
}
