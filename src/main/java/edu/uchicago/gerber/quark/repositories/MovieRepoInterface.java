package edu.uchicago.gerber.quark.repositories;

import edu.uchicago.gerber.quark.models.Movie;

import java.util.List;

public interface MovieRepoInterface {

    List<Movie> findAll();
    List<Movie> add(Movie movie);
    Movie get(String id);
    List<Movie> paged(int page);
}
