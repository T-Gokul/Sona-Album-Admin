package com.gokul.sonaalbumadmin;

public class UploadImage {
    private String name;
    private String image;

    public UploadImage() {
    }

    public UploadImage(String name, String image) {
        if(name.trim().equals(""))
        {
            name = "No Name";
        }
        this.name = name;
        this.image = image;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
