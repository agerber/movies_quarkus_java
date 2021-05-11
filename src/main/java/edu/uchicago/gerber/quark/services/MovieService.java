package edu.uchicago.gerber.quark.services;

import edu.uchicago.gerber.quark.models.Movie;
import edu.uchicago.gerber.quark.repositories.MovieMongodbRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class MovieService {

    //to use MongoDB, read the README.md first, then uncomment MovieMongodbRepo
    @Inject
    MovieMongodbRepo movieRepo;

    //to use DynamoDB, read the README.md first, also comment MovieMongodbRepo above, and uncomment MovieDynamodbRepo below
    // @Inject
    //MovieDynamodbRepo movieRepo;

    public List<Movie> findAll(){
       return movieRepo.findAll();
    }
    public List<Movie> add(Movie movie){
        return movieRepo.add(movie);
    }
    public Movie get(String id){
        return movieRepo.get(id);
    }
    public List<Movie> paged(int page){ return  movieRepo.paged(page);}




}
