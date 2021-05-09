package edu.uchicago.gerber.quark.repositories;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import edu.uchicago.gerber.quark.models.Movie;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


@ApplicationScoped
public class MovieMdbRepo {
    @Inject
    MongoClient mongoClient;



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
                .append(AbstractDdbRepo.MOVIE_YEAR_COL, movie.getYear());
        getCollection().insertOne(document);
        return findAll();
    }




//    public Movie get(String id) {
//        Map<String, AttributeValue> item;
//        item =dynamoDB.getItem(getRequest(id)).item();
//        if (null == item || item.size() == 0){
//            return null;
//        }
//        return transform(item);
//    }






    private MongoCollection getCollection(){
        return mongoClient.getDatabase("movies").getCollection("movies");
    }

//
//
//
//
//
//
//
//
//
//    public List<Movie> list(){
//        List<Movie> list = new ArrayList<>();
//        MongoCursor<Document> cursor = getCollection().find().iterator();
//
//        try {
//            while (cursor.hasNext()) {
//                Document document = cursor.next();
//                Movie movie = new Movie();
////                movie.setName(document.getString("name"));
////                movie.setDescription(document.getString("description"));
//                list.add(movie);
//            }
//        } finally {
//            cursor.close();
//        }
//        return list;
//    }
//
//    public void add(Movie fruit){
//        Document document = new Document()
////                .append("name", fruit.getName())
////                .append("description", fruit.getDescription());
//        getCollection().insertOne(document);
//    }
//
//    private MongoCollection getCollection(){
//        return mongoClient.getDatabase("fruit").getCollection("fruit");
//    }

}
