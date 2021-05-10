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
public class MovieMdbRepo extends AbstractDdbRepo{
    @Inject
    MongoClient mongoClient;
   public final int PAGE_SIZE = 20;

    void onStart(@Observes StartupEvent ev) {

       long collectionSize = getCollection().countDocuments();
       if (collectionSize > 0) return;

        Faker faker = new Faker();

        getCollection().insertMany(

                Stream.generate(() ->
                        {
                            Movie m = new Movie();
                            //allow Mongo to assign the id automatically
                            //m.setId(faker.chuckNorris().fact());
                            m.setTitle(faker.beer().name());
                            m.setYear(faker.beer().name().hashCode());
                            return m;
                        }


                )
                        .map(movie-> new Document()
                                //allow Mongo to assign the id automatically
                                //.append(AbstractDdbRepo.MOVIE_ID_COL, movie.getId())
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
                //set the id to the object-id which was generated upon insert into db
                movie.setId(document.getObjectId("_id").toHexString());
                movie.setTitle(document.getString(AbstractDdbRepo.MOVIE_TITLE_COL));
                movie.setYear(Integer.parseInt(document.getString(AbstractDdbRepo.MOVIE_YEAR_COL)));
                list.add(movie);
            }
        }
        return list;
    }



    public List<Movie> add(Movie movie) {
        Document document = new Document()
                //allow Mongo to assign the id automatically
                //.append(AbstractDdbRepo.MOVIE_ID_COL, movie.getId())
                .append(AbstractDdbRepo.MOVIE_TITLE_COL, movie.getTitle())
                .append(AbstractDdbRepo.MOVIE_YEAR_COL, String.valueOf(movie.getYear()));
        getCollection().insertOne(document);
        return findAll();
    }


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

    public List<Movie> paged( int pageNumber) {

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
    protected Movie transform(org.bson.Document document){
        Movie movie = new Movie();
        if (document != null && !document.isEmpty()) {

            movie.setId(document.getObjectId("_id").toHexString());
            movie.setTitle(document.getString(AbstractDdbRepo.MOVIE_TITLE_COL));
            movie.setYear(Integer.parseInt(document.getString(AbstractDdbRepo.MOVIE_YEAR_COL)));
        }
        return movie;

    }


    private MongoCollection getCollection(){
        return mongoClient.getDatabase("movies").getCollection("movies");
    }



}
