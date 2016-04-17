package org.deuxpiedsdeuxroues.velobs;


public class PointOfInterest {

    private String id;
    private String category;
    private String address;
    private String distance;
    private String status;
    private String photo;
    private String ville;
    private String description;

    public PointOfInterest(String id, String category, String address,
                           String distance, String status, String photo, String ville, String description) {

        this.id = id ;
        this.category = category ;
        this.address = address ;
        this.distance = distance ;
        this.status = status ;
        this.photo = photo ;
        this.ville = ville ;
        this.description = description ;
    }

    public void setId(String id) {
        this.id = id ;
    }

    public void setAddress(String address) {
        this.address = address ;
    }

    public void setCategory(String category) {
        this.category = category ;
    }

    public void setDistance(String distance) {
        this.distance = distance ;
    }

    public void setStatus(String status) {
        this.status = status ;
    }

    public void setPhoto(String photo) {
        this.photo = photo ;
    }

    public void setVille(String ville){
        this.ville = ville ;
    }

    public void setDescription(String description){
        this.description = description;
    }


    public String getAddress() {
        return address;
    }

    public String getCategory() {
        return category;
    }

    public String getDistance() {
        return distance;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getPhoto() {
        return photo;
    }

    public String getVille() {
        return ville;
    }

    public String getDescription() {
        return description;
    }


}
