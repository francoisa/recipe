/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jmf.recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author francoisa
 */
public class RecipeDao {
    private static final Logger LOG = Logger.getLogger(RecipeDao.class.getName());    
    private final Connection conn;
    
    public RecipeDao(Connection conn) {
        this.conn = conn;
    }
    
    public int save(Recipe recipe) {
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
                LOG.warning("Insert into recipes failed.");
            }
        }
        catch (SQLException sqe) {
            LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
            throw new RuntimeException(sqe.getMessage());
        }
        finally {
            close(stmt);
        }
        recipe.getIngredients().stream().forEach(ingredient -> insertIngredient(id, ingredient));        
        return id;
    }
    
    public String update(Recipe recipe) {
        String result = "SUCCESS";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("update recipes " + 
                    "set directions = ?" +
                    " where id = ?");
            stmt.setString(1, recipe.getDirections());
            stmt.setInt(2, recipe.getId());
            int rowCount = stmt.executeUpdate();
            if (rowCount != 1) {
                LOG.warning("Update recipes failed.");
            }
        }
        catch (SQLException sqe) {
            LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
            throw new RuntimeException(sqe.getMessage());
        }
        finally {
            close(stmt);
        }
        recipe.getIngredients().stream().forEach(ingredient -> updateIngredient(recipe.getId(), ingredient));        
        return result;
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
                LOG.warning("Insert into recipes failed.");
            }
        }
        catch (SQLException sqe) {
            LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
            throw new RuntimeException(sqe.getMessage());
        }
        finally {
            close(stmt);
        }
        return id;
    }
    
    private int updateIngredient(int recipeId, Ingredient ingredient) {
        int id = getNewIngredientId();
        if (ingredient.getId() > 0) {
            insertIngredient(recipeId, ingredient);
        }
        else {
            PreparedStatement stmt = null;
            try {
                stmt = conn.prepareStatement("update ingredients set" + 
                        " unit = ?, ingredient = ?, amount = ?, fraction = ?" +
                        " where recipe_id = ? and id = ?");
                stmt.setString(1, ingredient.getUnit());
                stmt.setString(2, ingredient.getIngredient());
                stmt.setInt(3, ingredient.getAmount());
                stmt.setString(4, ingredient.getFraction().toString());
                stmt.setInt(5, recipeId);
                stmt.setInt(6, id);
                int rowCount = stmt.executeUpdate();
                if (rowCount != 1) {
                    LOG.warning("Insert into recipes failed.");
                }
            }
            catch (SQLException sqe) {
                LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
                throw new RuntimeException(sqe.getMessage());
            }
            finally {
                close(stmt);
            }
        }
        return id;
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

    public Recipe get(int id) {
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
            LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
            throw new RuntimeException(sqe.getMessage());
        }
        finally {
            close(stmt);
        }
        return recipe;        
    }

    public List<Recipe> list() {
        PreparedStatement stmt = null;
        List<Recipe> recipeList = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("select id, name, directions from recipes");
            List<Ingredient> ingredients = new ArrayList<>();
            try (ResultSet results = stmt.executeQuery()) {
                while(results.next()) {
                    ingredients.clear();
                    int id = results.getInt(1);
                    String name = results.getString(2);
                    String directions = results.getString(3);
                    selectIngredients(id, ingredients);
                    recipeList.add(new Recipe(id, name, directions, ingredients));
                }
            }
        }
        catch (SQLException sqe) {
            LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
            throw new RuntimeException(sqe.getMessage());
        }
        finally {
            close(stmt);
        }
        return recipeList;
    }

    public void delete(int id) {
        List<Ingredient> ingredients = new ArrayList<>();
        selectIngredients(id, ingredients);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("delete from recipes where id = ?");
            stmt.setInt(1, id);
            int rowCount = stmt.executeUpdate();
            if (rowCount != 1) {
                LOG.warning("Delete did not affect 1 row.");
            }
        }
        catch (SQLException sqe) {
            LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
            throw new RuntimeException(sqe.getMessage());
        }
        finally {
            close(stmt);
        }
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
            LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
            throw new RuntimeException(sqe.getMessage());
        }
        finally {
            close(stmt);
        }
    }

    private int getNewRecipeId() {
        PreparedStatement stmt = null;
        int id = -1;
        try {
            stmt = conn.prepareStatement("values (next value for recipe_id_seq)");
            try (ResultSet results = stmt.executeQuery()) {
                while(results.next()) {
                    id = results.getInt(1);
                }
            }
        }
        catch (SQLException sqe) {
            LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
            throw new RuntimeException(sqe);
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
                while(results.next()) {
                    id = results.getInt(1);
                }
            }
        }
        catch (SQLException sqe) {
            LOG.log(Level.SEVERE, sqe.getMessage(), sqe);
            throw new RuntimeException(sqe);
        }
        finally {
            close(stmt);          
        }
        return id;
    }        
}
