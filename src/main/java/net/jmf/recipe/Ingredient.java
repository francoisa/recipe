package net.jmf.recipe;

/**
 *
 * @author francoisa
 */
public class Ingredient {
    private int hash = 0;    
    private final int id;
    private final int reportId;
    private final String unit;
    private final String ingredient;
    private final int amount;
    private final Fraction fraction;

    public Ingredient(String unit, String ingredient, int amount, Fraction fraction) {
        this(0, 0, unit, ingredient, amount, fraction);
    }

    public Ingredient(int reportId, String unit, String ingredient, int amount, Fraction fraction) {
        this(0, reportId, unit, ingredient, amount, fraction);
    }

    public Ingredient(int id, int reportId, String unit, String ingredient, int amount) {
        this(id, reportId, unit, ingredient, amount, Fraction.zero);
    }

    public Ingredient(int id, int reportId, String unit, String ingredient, Fraction fraction) {
        this.id = id;
        this.reportId = reportId;
        this.unit = unit;
        this.ingredient = ingredient;
        this.amount = 0;
        this.fraction = fraction;
    }

    public Ingredient(int id, int reportId, String unit, String ingredient, int amount, Fraction fraction) {
        this.id = id;
        this.reportId = reportId;
        this.unit = unit;
        this.ingredient = ingredient;
        this.amount = amount;
        this.fraction = fraction;
    }

    public int getId() {
        return id;
    }

    public int getReportId() {
        return reportId;
    }

    public String getUnit() {
        return unit;
    }

    public String getIngredient() {
        return ingredient;
    }

    public int getAmount() {
        return amount;
    }

    public Fraction getFraction() {
        return fraction;
    }
    @Override 
    public boolean equals(Object aThat) {
       if (this == aThat) return true;
       if (!(aThat instanceof Ingredient)) return false;
       Ingredient that = (Ingredient) aThat;
       return (this.amount == that.amount) &&
           this.unit.equals(that.unit) &&
           this.fraction.equals(that.fraction) &&
           this.ingredient.equals(that.ingredient);
    }
    
    @Override
    public int hashCode() {
        if (hash == 0) {
            StringBuilder s1 = new StringBuilder(this.unit).append("|");
            s1.append(this.ingredient).append("|");
            s1.append(String.valueOf(this.amount)).append("|");
            s1.append(this.fraction.toString());
            hash = s1.hashCode();
        }
        return hash;
    }    
}
