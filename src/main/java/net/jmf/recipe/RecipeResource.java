package net.jmf.recipe;

import com.google.gson.Gson;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author francoisa
 */
public class RecipeResource {
    private final int id;
    private final String name;
    private final String directions;
    private final Gson gson;
    private final CopyOnWriteArrayList<RecipeResource> rList;
  
    private RecipeResource(RecipeBuilder builder) {
        this.gson = new Gson();
        this.rList = new CopyOnWriteArrayList<>();
        this.id = builder.id;
        this.name = builder.name;
        this.directions = builder.directions;
    }

    public RecipeResource(){
        this.gson = new Gson();
        this.rList = new CopyOnWriteArrayList<>();
        RecipeResource cust = new RecipeResource.RecipeBuilder().id().build();
        this.id = cust.getID();
        this.name = cust.getName();
        this.directions = cust.getDirections();
    }
      
    public RecipeResource(long id, String name, String directions) {
        this.gson = new Gson();
        this.rList = new CopyOnWriteArrayList<>();
        RecipeResource cust = new RecipeResource.RecipeBuilder().id()
           .name(name)
           .directions(directions)
           .build();
        this.id = cust.getID();
        this.name = cust.getName();
        this.directions = cust.getDirections();
    }
  
    public int getID(){
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDirections() {
        return this.directions;
    }

    @Override
    public String toString() {
        return "ID: " + id 
            + " name: " + name
            + " directions: " + directions + "\n";
    }
  
    public static class RecipeBuilder {
        private int id;
        private String name = "";
        private String directions = "";
        
        public RecipeBuilder id(){
          //this.id = Recipe.counter.getAndIncrement();
          return this;
        }

        public RecipeBuilder id(int id){
          this.id = id;
          return this;
        }

        public RecipeBuilder name(String name){
          this.name = name;
          return this;
        }

        public RecipeBuilder directions(String directions){
          this.directions = directions;
          return this;
        }

        public RecipeResource build(){
          return new RecipeResource(this);
        }
    }
    
    @GET
    @Path("/recipes")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRecipes() {
        return gson.toJson(rList.toArray());
    }
    
    @GET
    @Path("/recipe/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRecipe(@PathParam("id") int id) {
        Optional<RecipeResource> match
            = rList.stream()
            .filter(c -> c.getID() == id)
            .findFirst();
        if (match.isPresent()) {
            return gson.toJson(match.get());
        } 
        else {
            return "Customer not found";
        }
    }    
}