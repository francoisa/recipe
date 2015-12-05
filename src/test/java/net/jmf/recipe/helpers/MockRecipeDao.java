/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jmf.recipe.helpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import net.jmf.recipe.Recipe;
import net.jmf.recipe.RecipeDao;

/**
 *
 * @author francoisa
 */
public class MockRecipeDao implements RecipeDao {
    Map<Integer, Recipe> recipeMap;
    
    public MockRecipeDao(Map<Integer, Recipe> recipeMap) {
        this.recipeMap = recipeMap == null ? new HashMap<>() : recipeMap;
    }
    
    @Override
    public void delete(int id) {
        recipeMap.remove(id);
    }

    @Override
    public Recipe get(int id) {
        return recipeMap.getOrDefault(id, null);
    }

    @Override
    public List<Recipe> list() {
        Recipe[] recipeArray = new Recipe[recipeMap.size()];
        return Arrays.asList(recipeMap.values().toArray(recipeArray));
    }

    @Override
    public int save(Recipe recipe) {
        OptionalInt max = recipeMap.keySet().stream().mapToInt((x) -> x).max();
        int id = max.orElse(0) + 1;
        recipeMap.put(id, new Recipe(id, recipe.getName(), 
                recipe.getDirections(), recipe.getIngredients()));
        return id;
    }

    @Override
    public String update(Recipe recipe) {
        recipeMap.put(recipe.getId(), recipe);
        return "SUCCESS";
    }
    
}
