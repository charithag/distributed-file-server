package org.dc.file.search.dto;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    private String commentId;
    private String parentId;
    private String fileName;
    private String userName;
    private String text;
    private List<Comment> replies = new ArrayList<>();
    private List<Rating> ratings = new ArrayList<>();

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public int getTotalRating(){
        if (ratings.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (Rating rating : ratings) {
            total += rating.getValue();
        }
        return total / ratings.size();
    }

}
