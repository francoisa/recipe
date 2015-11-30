/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jmf.recipe;

/**
 *
 * @author francoisa
 */
public enum Fraction {
    seven_eigths ("7/8", 7, 8), 
    three_quarters ("3/4", 3, 4), 
    five_eigths ("5/8", 5, 8), 
    half ("1/2", 1, 2), 
    three_eigths ("3/8", 3, 8), 
    quarter ("1/4", 1, 4), 
    eigth ("1/8", 1, 8), 
    zero ("", 0, 1);
    
    private final String name;
    private final int num;   // numerator
    private final int denom; // denominator
    Fraction(String name, int num, int denom) {
        this.name = name;
        this.num = num;
        this.denom = denom;
    }
    
    public static Fraction fromString(String n) {
        if (seven_eigths.name.equals(n)) {
            return seven_eigths;
        }
        else if (three_quarters.name.equals(n)) {
            return three_quarters;
        }
        else if (five_eigths.name.equals(n)) {
            return five_eigths;
        }
        else if (half.name.equals(n)) {
            return half;
        }
        else if (three_eigths.name.equals(n)) {
            return three_eigths;
        }
        else if (quarter.name.equals(n)) {
            return quarter;
        }
        else if (eigth.name.equals(n)) {
            return eigth;
        }
        else {
            return zero;
        }
    }
    
    @Override
    public String toString() {
        return name;
    }
}
