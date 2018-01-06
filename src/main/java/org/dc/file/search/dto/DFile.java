package org.dc.file.search.dto;

import java.util.ArrayList;
import java.util.List;

public class DFile {

    private String fileName;
    private List<Comment> comments = new ArrayList<>();
    private List<Rating> ratings = new ArrayList<>();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public float getTotalRating(){
        if (ratings.isEmpty()) {
            return 0;
        }
        float total = 0;
        for (Rating rating : ratings) {
            total += rating.getValue();
        }
        return total / ratings.size();
    }
}
