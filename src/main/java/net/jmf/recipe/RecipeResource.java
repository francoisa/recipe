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
@Path("/")
public class RecipeResource {
    private static RecipeDao dao;
    private final Gson gson;
    private final CopyOnWriteArrayList<Recipe> rList;
    
    public RecipeResource() {
        rList = new CopyOnWriteArrayList<>();
        gson = new Gson();
    }
    
    public static void setRecipeDao(RecipeDao myDao) {
        dao = myDao;
    }
    
    @GET
    @Path("recipe/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRecipe(@PathParam("id") int id) {
        return gson.toJson(dao.get(id));
    }    
    
    @GET
    @Path("recipes")
    @Produces(MediaType.APPLICATION_JSON)
    public String getRecipes() {
        return gson.toJson(dao.list());
    }    
}