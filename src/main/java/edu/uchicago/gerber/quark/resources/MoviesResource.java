package edu.uchicago.gerber.quark.resources;

import com.github.javafaker.Faker;
import edu.uchicago.gerber.quark.models.Movie;
import edu.uchicago.gerber.quark.services.MovieService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    @GET
    @Path("/test")
    public List<Movie> testMe(){
        Faker faker = new Faker();
       return Stream.generate(() -> new Movie(faker.chuckNorris().fact(), faker.beer().name(), faker.hashCode() )).limit(5).collect(Collectors.toList());
    }

    //https://www.technicalkeeda.com/java-mongodb-tutorials/java-mongodb-driver-3-3-0-pagination-example
    @GET
    @Path("/paged/{page}")
    public List<Movie> paged(@PathParam("page") int page){
       return movieService.paged(page);
    }
}
