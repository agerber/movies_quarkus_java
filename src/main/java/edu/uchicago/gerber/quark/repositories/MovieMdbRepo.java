package edu.uchicago.gerber.quark.repositories;


import com.github.javafaker.Faker;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import edu.uchicago.gerber.quark.models.Movie;
import io.quarkus.runtime.StartupEvent;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@ApplicationScoped
public class MovieMdbRepo {
    @Inject
    MongoClient mongoClient;


    void onStart(@Observes StartupEvent ev) {

        Faker faker = new Faker();

        getCollection().insertMany(

                Stream.generate(() ->
                        {
                            Movie m = new Movie();
                            m.setId(faker.chuckNorris().fact());
                            m.setTitle(faker.beer().name());
                            m.setYear(faker.beer().hashCode());
                            return m;
                        }


                )
                        .map(movie-> new Document().append(AbstractDdbRepo.MOVIE_ID_COL, movie.getId())
                                .append(AbstractDdbRepo.MOVIE_TITLE_COL, movie.getTitle())
                                .append(AbstractDdbRepo.MOVIE_YEAR_COL, String.valueOf(movie.getYear())))
                        .limit(1000)
                        .collect(Collectors.toList()));
    }

    public List<Movie> findAll() {

        List<Movie> list = new ArrayList<>();

        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                Movie movie = new Movie();
                movie.setId(document.getString(AbstractDdbRepo.MOVIE_ID_COL));
                movie.setTitle(document.getString(AbstractDdbRepo.MOVIE_TITLE_COL));
                movie.setYear(Integer.parseInt(document.getString(AbstractDdbRepo.MOVIE_YEAR_COL)));
                list.add(movie);
            }
        }
        return list;
    }



    public List<Movie> add(Movie movie) {
        Document document = new Document()
                .append(AbstractDdbRepo.MOVIE_ID_COL, movie.getId())
                .append(AbstractDdbRepo.MOVIE_TITLE_COL, movie.getTitle())
                .append(AbstractDdbRepo.MOVIE_YEAR_COL, String.valueOf(movie.getYear()));
        getCollection().insertOne(document);
        return findAll();
    }




    public Movie get(String id) {
       // getCollection().find({AbstractDdbRepo.MOVIE_ID_COL: id},);
        Faker faker = new Faker();
        return  new Movie(id, faker.beer().name(), faker.hashCode());
    }






    private MongoCollection getCollection(){
        return mongoClient.getDatabase("movies").getCollection("movies");
    }


}
