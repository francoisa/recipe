package net.jmf.recipe;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import java.sql.SQLException;
import org.junit.After;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import net.jmf.recipe.helpers.RecipeDbHelper;
import org.junit.Before;

/**
 *
 * @author francoisa
 */
public class RecipeDbTests {
    private static final Logger LOG = Logger.getLogger(RecipeDbTests.class.getName());
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";    
    private static final String CONNECTIONURL = "jdbc:derby:memory:myDB;create=true";    
    private static RecipeDbHelper dbHelper;
    
    @Before
    public void setup() {
    }

    @BeforeClass
    public static void setupClass() {
        Handler ch = new ConsoleHandler();
        ch.setLevel (Level.FINE);
        dbHelper = new RecipeDbHelper(CONNECTIONURL, DRIVER);
        dbHelper.createTables();
    }
    
    @After
    public void teardown() {
        dbHelper.cleanup();
    }
    
    @AfterClass
    public static void teardownClass() {  
    }
    
    @Test
    public void saveRecipeShouldAddARecipe() throws SQLException {
        String name = "boiled egg"; 
        String directions = "Boil 2 cups of water. Add egg for 15 minutes.";
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("each", "egg", 1, Fraction.zero));
        Recipe actual = new Recipe(name, directions, ingredients);
        RecipeDao recipeDao = new RecipeDao(dbHelper.getConnection());
        int id = recipeDao.save(actual);
        assertThat(id, is(greaterThan(0)));
        Recipe expected = dbHelper.selectRecipe(id);
        assertThat(expected, is(equalTo(actual)));
    }
    
    @Test
    public void getRecipeShouldReturnARecipe() throws SQLException {
        String name = "boiled egg"; 
        String directions = "Boil 2 cups of water. Add egg for 15 minutes.";
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("each", "egg", 1, Fraction.zero));
        Recipe expected = new Recipe(name, directions, ingredients);
        RecipeDao recipeDao = new RecipeDao(dbHelper.getConnection());
        int id = dbHelper.insertRecipe(expected);
        Recipe actual = recipeDao.get(id);
        assertThat(actual, is(equalTo(expected)));
    }

}
