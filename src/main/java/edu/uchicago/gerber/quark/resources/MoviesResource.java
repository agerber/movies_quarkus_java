package edu.uchicago.gerber.quark.resources;

import edu.uchicago.gerber.quark.models.Movie;
import edu.uchicago.gerber.quark.services.MovieService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MoviesResource {

    @Inject
    MovieService movieService;

    @GET
    public List<Movie> getAll() {
       return movieService.findAll();
    }

    @POST
    public List<Movie> add(Movie movie){
        movieService.add(movie);
        return getAll();
    }

    @GET
    @Path("{id}")
    public Movie getFromId(@PathParam("id") String id){
        Movie movie = movieService.get(id);
        if (null == movie){
            throw new NotFoundException("The Movie with id " + id + " was not found");
        }
        return movie;
    }

}
