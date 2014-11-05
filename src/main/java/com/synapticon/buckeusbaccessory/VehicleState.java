package com.synapticon.buckeusbaccessory;

public class VehicleState {
    
    private String label;
    private int value;

    public VehicleState(String label, int value) {
        this.label = label;
        this.value = value;
    }
    
    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return label;
    }
}
