package net.jmf.recipe;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author francoisa
 */
public class Recipe {    
    private final int id;
    private final String name;
    private final String directions;
    private final Ingredient[] ingredients;
    
    public Recipe(String name, String directions, List<Ingredient> ingredients) {
        this(0, name, directions, ingredients);
    }
    
    public Recipe(int id, String name, String directions, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.directions = directions;
        this.ingredients = new Ingredient[ingredients.size()];
        ingredients.toArray(this.ingredients);
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDirections() {
        return directions;
    }
    
    public List<Ingredient> getIngredients() {
        return Arrays.asList(ingredients);
    }
    
    @Override 
    public boolean equals(Object aThat) {
       if (this == aThat) return true;
       if (!(aThat instanceof Recipe) ) return false;
       Recipe that = (Recipe) aThat;
       boolean e = this.name.equals(that.name) &&
           this.directions.equals(that.directions);
       e = e && (this.ingredients.length == that.ingredients.length);
       for (Ingredient i : this.ingredients) {
        boolean f = false;
        for (Ingredient i2 : that.ingredients) {
            if (i2.equals(i)) {
                f = true;
                break;
            }
        }
        e = e && f;
       }
       return e;
    }
    
    private int hash = 0;
    
    @Override
    public int hashCode() {
        if (hash == 0) {
            StringBuilder s1 = new StringBuilder(name + "|" + directions);
            for (Ingredient i : ingredients) {
                s1.append(i.toString());
            }
            hash = s1.hashCode();
        }
        return hash;
    }
}