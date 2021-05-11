package edu.uchicago.gerber.quark.services;

import edu.uchicago.gerber.quark.models.Movie;

import java.util.List;

public interface MovieServiceInterface {

    List<Movie> findAll();
    List<Movie> add(Movie movie);
    Movie get(String id);
    List<Movie> paged(int page);
}
