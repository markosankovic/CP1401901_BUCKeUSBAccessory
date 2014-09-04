package com.synapticon.buckeusbaccessory;

public class IdentifyingInformation {
    
    private final String manufacturerName;
    private final String modelName;
    private final String description;
    private final String version;
    private final String URI;
    private final String serialNumber;
    
    /**
     * Initialize USB Accessory Identifying Information.
     * 
     * @param manufacturerName
     * @param modelName
     * @param description
     * @param version
     * @param URI
     * @param serialNumber 
     */
    public IdentifyingInformation(String manufacturerName, String modelName, String description, String version, String URI, String serialNumber) {
        this.manufacturerName = manufacturerName;
        this.modelName = modelName;
        this.description = description;
        this.version = version;
        this.URI = URI;
        this.serialNumber = serialNumber;
    }

    /**
     * @return the manufacturerName
     */
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * @return the modelName
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the URI
     */
    public String getURI() {
        return URI;
    }

    /**
     * @return the serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }
}
