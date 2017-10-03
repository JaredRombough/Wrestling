/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling.model;

import java.time.DayOfWeek;

/**
 *
 * @author jared
 */
public class Television {

    private DayOfWeek day;
    private String name;
    private Promotion promotion;

    /**
     * @return the day
     */
    public DayOfWeek getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

}
