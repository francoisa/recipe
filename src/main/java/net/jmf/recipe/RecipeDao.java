/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jmf.recipe;

import java.util.List;

/**
 *
 * @author francoisa
 */
public interface RecipeDao {

    void delete(int id);

    Recipe get(int id);

    List<Recipe> list();

    int save(Recipe recipe);

    String update(Recipe recipe);
    
}
