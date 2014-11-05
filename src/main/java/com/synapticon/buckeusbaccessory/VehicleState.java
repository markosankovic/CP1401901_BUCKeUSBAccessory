package com.synapticon.buckeusbaccessory;

public class VehicleState {
    
    private String label;
    private byte value;

    public VehicleState(String label, byte value) {
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
    public byte getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(byte value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return label;
    }
}
