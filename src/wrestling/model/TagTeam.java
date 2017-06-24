/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jared
 */
public class TagTeam {

    private String name;
    private List<Worker> workers = new ArrayList<>();
    private int experience;
    private boolean active;
    
    @Override
    public String toString()
    {
        return name;
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
     * @return the workers
     */
    public List<Worker> getWorkers() {
        return workers;
    }

    /**
     * @param workers the workers to set
     */
    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    /**
     * @return the experience
     */
    public int getExperience() {
        return experience;
    }

    /**
     * @param experience the experience to set
     */
    public void setExperience(int experience) {
        this.experience = experience;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}
