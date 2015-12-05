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
    JdbcRecipeDao recipeDao;
    
    @Before
    public void setup() {
        recipeDao = new JdbcRecipeDao(dbHelper.getConnection());
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
        int id = recipeDao.save(actual);
        assertThat(id, is(greaterThan(0)));
        Recipe expected = dbHelper.selectRecipe(id);
        assertThat(expected, is(equalTo(actual)));
    }
    
    @Test
    public void getRecipeShouldReturnARecipe() throws SQLException {
        Recipe expected = insertTestRecipe();
        Recipe actual = recipeDao.get(expected.getId());
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void listRecipesShouldReturnAList() throws SQLException {
        Recipe expected = insertTestRecipe();
        List<Recipe> actual = recipeDao.list();
        assertThat(actual.size(), is(equalTo(1)));
        assertThat(actual.get(0), is(equalTo(expected)));
    }

    @Test
    public void updateRecipeShouldModifyDirections() throws SQLException {
        Recipe expected = insertTestRecipe();
        String directions = "Add egg to 2 cups of boiling water for 15 minutes.";
        expected = new Recipe(expected.getId(), expected.getName(), directions, 
                    expected.getIngredients());
        recipeDao.update(expected);
        Recipe actual = dbHelper.selectRecipe(expected.getId());
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void deleteRecipeShouldRemoveARecipe() throws SQLException {
        Recipe expected = insertTestRecipe();
        int oldCount = dbHelper.countRecipes();
        recipeDao.delete(expected.getId());
        int newCount = dbHelper.countRecipes();
        assertThat((oldCount - newCount), is(equalTo(1)));
    }

    private Recipe insertTestRecipe() {
        String name = "boiled egg"; 
        String directions = "Boil 2 cups of water. Add egg for 15 minutes.";
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("each", "egg", 1, Fraction.zero));
        Recipe recipe = new Recipe(name, directions, ingredients);
        int id = dbHelper.insertRecipe(recipe);       
        return new Recipe(id, name, directions, ingredients);
    }
}
