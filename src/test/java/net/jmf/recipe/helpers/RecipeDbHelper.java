/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jmf.recipe.helpers;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jmf.recipe.Fraction;
import net.jmf.recipe.Ingredient;
import net.jmf.recipe.Recipe;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author francoisa
 */
public class RecipeDbHelper {
    private static final Logger LOG = Logger.getLogger(RecipeDbHelper.class.getName());    
    private Connection conn;
    private final Gson gson;
    
    private void createConnection(String url, String driver) {
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url); 
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException except) {
            LOG.log(Level.SEVERE, except.getMessage(), except);
        }
    }
    
    private void createTable(String table_ddl) {
        PreparedStatement stmt;
        stmt = null;
        try {
            stmt = conn.prepareStatement(table_ddl);
            stmt.executeUpdate();
        } 
        catch (SQLException sqe) {
            LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
        }
        finally {
            close(stmt);
        }        
    }
    
    public void createTables() {
        final String recipe_seq_ddl = "create sequence recipe_id_seq as int " + 
                    "start with 1";
        createTable(recipe_seq_ddl);
        final String recipe_ddl = "create table recipes " +
                            "(id int NOT NULL primary key," +
                            " name varchar(80) not null unique," +
                            " directions varchar(2000))";
        createTable(recipe_ddl);
        final String ingredient_seq_ddl = "create sequence ingredient_id_seq as int " +
                    "start with 1";
        createTable(ingredient_seq_ddl);
        final String ingredient_ddl = "create table ingredients " +
                            "(id integer NOT NULL primary key," +
                            " recipe_id integer not null," +                
                            " amount integer not null," +                
                            " ingredient varchar(80) not null," +
                            " unit varchar(80)," +
                            " fraction varchar(80), " + 
                            " unique (recipe_id, id))";
        createTable(ingredient_ddl);
    }
        
    private int getNewRecipeId() {
        PreparedStatement stmt = null;
        int id = -1;
        try {
            stmt = conn.prepareStatement("values (next value for recipe_id_seq)");
            try (ResultSet results = stmt.executeQuery()) {
                ResultSetMetaData rsmd = results.getMetaData();
                int numberCols = rsmd.getColumnCount();
                for (int i=1; i<=numberCols; i++) {
                    System.out.print(rsmd.getColumnLabel(i)+"\t\t");  
                }
                
                while(results.next()) {
                    id = results.getInt(1);
                }
            }
        }
        catch (SQLException sqe) {
            sqe.printStackTrace(System.err);
        }
        finally {
            close(stmt);          
        }
        return id;
    }
    
    private int getNewIngredientId() {
        PreparedStatement stmt = null;
        int id = -1;
        try {
            stmt = conn.prepareStatement("values (next value for ingredient_id_seq)");
            try (ResultSet results = stmt.executeQuery()) {
                ResultSetMetaData rsmd = results.getMetaData();
                int numberCols = rsmd.getColumnCount();
                for (int i=1; i<=numberCols; i++) {
                    System.out.print(rsmd.getColumnLabel(i)+"\t\t");  
                }
                
                while(results.next()) {
                    id = results.getInt(1);
                }
            }
        }
        catch (SQLException sqe) {
            sqe.printStackTrace(System.err);
        }
        finally {
            close(stmt);          
        }
        return id;
    }
    
    public RecipeDbHelper(String connectionUrl, String driver) {
        gson = new Gson();
        createConnection(connectionUrl, driver);
    }
    
    public int insertRecipe(Recipe recipe) {
        int id = getNewRecipeId();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("insert into recipes " + 
                    "(id, name, directions)" +
                    " values (?, ?, ?)");
            stmt.setInt(1, id);
            stmt.setString(2, recipe.getName());
            stmt.setString(3, recipe.getDirections());
            int rowCount = stmt.executeUpdate();
            if (rowCount != 1) {
                throw new RuntimeException("can't insert the recipe.");
            }
        }
        catch (SQLException sqe) {
            sqe.printStackTrace(System.err);
        }
        finally {
            close(stmt);
        }
        recipe.getIngredients().stream().forEach(ingredient -> insertIngredient(id, ingredient));
        return id;
    }    

    private int insertIngredient(int recipeId, Ingredient ingredient) {
        int id = getNewIngredientId();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("insert into ingredients " + 
                    "(id, recipe_id, unit, ingredient, amount, fraction)" +
                    " values (?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, id);
            stmt.setInt(2, recipeId);
            stmt.setString(3, ingredient.getUnit());
            stmt.setString(4, ingredient.getIngredient());
            stmt.setInt(5, ingredient.getAmount());
            stmt.setString(6, ingredient.getFraction().toString());
            int rowCount = stmt.executeUpdate();
            if (rowCount != 1) {
                throw new RuntimeException("failed to insert ingredient");
            }
        }
        catch (SQLException sqe) {
            sqe.printStackTrace(System.err);
        }
        finally {
            close(stmt);
        }
        return id;
    }
    
    public void selectRecipes(List<Recipe> recipes) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("select * from recipe");
            try (ResultSet results = stmt.executeQuery()) {
                ResultSetMetaData rsmd = results.getMetaData();
                int numberCols = rsmd.getColumnCount();
                for (int i=1; i<=numberCols; i++) {
                    System.out.print(rsmd.getColumnLabel(i)+"\t\t");  
                }
                
                while(results.next()) {
                    int id = results.getInt(1);
                    String restName = results.getString(2);
                    String cityName = results.getString(3);
                    System.out.println(id + "\t\t" + restName + "\t\t" + cityName);
                }
            }
        }
        catch (SQLException sqe) {
            sqe.printStackTrace(System.err);
        }
        finally {
            close(stmt);
        }
    }

    public Recipe selectRecipe(int id) {
        List<Ingredient> ingredients = new ArrayList<>();
        selectIngredients(id, ingredients);
        PreparedStatement stmt = null;
        Recipe recipe = null;
        try {
            stmt = conn.prepareStatement("select name, directions from recipes where id = ?");
            stmt.setInt(1, id);
            try (ResultSet results = stmt.executeQuery()) {
                while(results.next()) {
                    String name = results.getString(1);
                    String directions = results.getString(2);
                    recipe = new Recipe(id, name, directions, ingredients);
                }
            }
        }
        catch (SQLException sqe) {
            sqe.printStackTrace(System.err);
        }
        finally {
            close(stmt);
        }
        return recipe;
    }

    public void selectIngredients(int recipeId, List<Ingredient> ingredients) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("select id, unit, ingredient, amount, " + 
                        "fraction from ingredients where recipe_id = ?");
            stmt.setInt(1, recipeId);
            try (ResultSet results = stmt.executeQuery()) {
                while(results.next()) {
                    int id = results.getInt(1);
                    String unit = results.getString(2);
                    String ingredient = results.getString(3);
                    int amount = results.getInt(4);
                    String fractionStr = results.getString(5);
                    Fraction fraction = Fraction.fromString(fractionStr);
                    
                    if (fraction == Fraction.zero) {
                        ingredients.add(new Ingredient(id, recipeId, unit, ingredient, amount));
                    }
                    else {
                        ingredients.add(new Ingredient(id, recipeId, unit, ingredient, amount, fraction));
                    }
                }
            }
        }
        catch (SQLException sqe) {
            sqe.printStackTrace(System.err);
        }
        finally {
            close(stmt);
        }
    }

    private void close(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } 
            catch (SQLException sqe) {
                LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
            }
        }
    }
    
    private void deleteIngredients() {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("delete from ingredients");
            stmt.executeUpdate();
        }
        catch (SQLException sqe) {
            sqe.printStackTrace(System.err);
        }
        finally {
            close(stmt);
        }
    }
    
    private void deleteRecipes() {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("delete from recipes");
            stmt.executeUpdate();
        }
        catch (SQLException sqe) {
            sqe.printStackTrace(System.err);
        }
        finally {
            close(stmt);
        }
    }
    
    public void cleanup() {
        deleteIngredients();
        deleteRecipes();
    }

    public Connection getConnection() {
        return conn;
    }
}
