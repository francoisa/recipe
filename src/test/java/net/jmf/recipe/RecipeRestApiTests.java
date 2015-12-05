package net.jmf.recipe;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Application;
import net.jmf.recipe.helpers.MockRecipeDao;
import net.jmf.recipe.helpers.RecipeDbHelper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author francoisa
 */
public class RecipeRestApiTests extends JerseyTest {
    private static final Logger LOG = Logger.getLogger(RecipeDbTests.class.getName());
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";    
    private static final String CONNECTIONURL = "jdbc:derby:memory:RestApi;create=true";    
    private static RecipeDbHelper dbHelper;
    private static Gson gson;
    private Recipe expectedRecipe;
    private static final int ID = 1;

    @BeforeClass
    public static void setupClass() {
        System.setProperty("jersey.test.containerFactory", "com.sun.jersey.test.framework.spi.container.inmemory.InMemoryTestContainerFactory");
        Handler ch = new ConsoleHandler();
        ch.setLevel (Level.FINE);
        dbHelper = new RecipeDbHelper(CONNECTIONURL, DRIVER);
        dbHelper.createTables();
        gson = new Gson();
    }
    
    @Before
    public void setup() {
        String name = "boiled egg"; 
        String directions = "Boil 2 cups of water. Add egg for 15 minutes.";
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("each", "egg", 1, Fraction.zero));
        
        expectedRecipe = new Recipe(ID, name, directions, ingredients);
        Map<Integer, Recipe> recipeMap = new HashMap<>();
        recipeMap.put(ID, expectedRecipe);
        RecipeDao dao = new MockRecipeDao(recipeMap);
        RecipeResource.setRecipeDao(dao);
    }
    
    @Override
    protected Application configure() {
        return new ResourceConfig(RecipeResource.class);
    }
    
    @Test
    public void recipePathShouldReturnARecipe() {
        final String json = target("/recipe")
                .path("/" + String.valueOf(ID))
                .request()
                .get(String.class);
        Recipe recipe = gson.fromJson(json, Recipe.class);
        assertThat(recipe, is(equalTo(expectedRecipe)));
    }     

    @Test
    public void recipesPathShouldReturnARecipeList() {
        final String json = target("/recipes")
                .request()
                .get(String.class);
        Recipe[] recipes = gson.fromJson(json, Recipe[].class);
        assertThat(recipes.length, is(equalTo(1)));
        assertThat(recipes[0], is(equalTo(expectedRecipe)));
    }     
}
