package edu.uchicago.gerber.quark.services;

import edu.uchicago.gerber.quark.models.Movie;
import edu.uchicago.gerber.quark.repositories.MovieRepo;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class MovieService {

    @Inject
    MovieRepo movieRepo;

    public List<Movie> findAll(){
       return movieRepo.findAll();
    }
    public List<Movie> add(Movie movie){
        return movieRepo.add(movie);
    }
    public Movie get(String id){
        return movieRepo.get(id);
    }


}
