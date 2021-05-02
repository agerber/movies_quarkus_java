package edu.uchicago.gerber.quark.resources;

import edu.uchicago.gerber.quark.models.Movie;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MoviesResource {

    @GET
    public List<Movie> getAll() {
        //use service to get movies
        return null;
    }

    @POST
    public List<Movie> add(Movie movie){
        //use service to add movie
        return getAll();
    }

    @GET
    @Path("{id}")
    public Movie getFromId(@PathParam("id") String id){
        //use service to return movie based on id
        return null;
    }

}
