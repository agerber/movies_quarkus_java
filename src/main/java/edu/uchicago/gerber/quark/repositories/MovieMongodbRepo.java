package edu.uchicago.gerber.quark.repositories;


import com.github.javafaker.Faker;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import edu.uchicago.gerber.quark.models.Movie;
import io.quarkus.runtime.StartupEvent;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@ApplicationScoped
public class MovieMongodbRepo implements MovieRepoInterface {
    @Inject
    MongoClient mongoClient;

    public final int PAGE_SIZE = 20;
    //id is auto-generated by mongodb
    public static final String MOVIE_TITLE_COL = "title";
    public static final String MOVIE_YEAR_COL = "year";

    //this will get fired when the quarkus microservice starts
    void onStart(@Observes StartupEvent ev) {

        long collectionSize = getCollection().countDocuments();
        if (collectionSize > 0) return;

        Faker faker = new Faker();

        getCollection().insertMany(
                Stream.generate(() ->

                        //mongo will auto-geneate the _id field for us.
                         new Document()
                                    .append(MOVIE_TITLE_COL, faker.beer().name())
                                    .append(MOVIE_YEAR_COL, String.valueOf(faker.chuckNorris().fact().hashCode())))
                        .limit(1000)
                        .collect(Collectors.toList()));
    }
    @Override
    public List<Movie> findAll() {

        List<Movie> list = new ArrayList<>();
        //this is the try-with-resources syntax from Java7
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                Movie movie = new Movie();
                movie.setId(document.getObjectId("_id").toHexString());
                movie.setTitle(document.getString(MOVIE_TITLE_COL));
                movie.setYear(Integer.parseInt(document.getString(MOVIE_YEAR_COL)));
                list.add(movie);
            }
        }
        return list;
    }

   @Override
    public List<Movie> add(Movie movie) {
        Document document = new Document()
                .append(MOVIE_TITLE_COL, movie.getTitle())
                .append(MOVIE_YEAR_COL, String.valueOf(movie.getYear()));
        getCollection().insertOne(document);
        return findAll();
    }

    @Override
    public Movie get(String id) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        FindIterable<Document> documents = getCollection().find(query);

        List<Movie> movies = new ArrayList<>();
        for (Document document : documents) {
            movies.add(transform(document));
        }

        //this will produce a 404 not found
        if (movies.size() != 1) return null;

        return movies.get(0);
    }
    @Override
    public List<Movie> paged(int pageNumber) {

        List<Movie> list = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = getCollection().find().skip(PAGE_SIZE * (pageNumber - 1)).limit(PAGE_SIZE).iterator();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                list.add(transform(document));
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //for mongo
    private Movie transform(org.bson.Document document) {
        Movie movie = new Movie();
        if (document != null && !document.isEmpty()) {

            movie.setId(document.getObjectId("_id").toHexString());
            movie.setTitle(document.getString(MOVIE_TITLE_COL));
            movie.setYear(Integer.parseInt(document.getString(MOVIE_YEAR_COL)));
        }
        return movie;

    }


    private MongoCollection getCollection() {
        return mongoClient.getDatabase("movies_db").getCollection("movies_collection");
    }


}
