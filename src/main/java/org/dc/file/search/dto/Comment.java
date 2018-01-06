package org.dc.file.search.dto;

import java.util.List;

public class Comment {

    private String commentId;
    private long lamportTime;
    private String fileName;
    private List<Comment> replies;
    private List<Rating> ratings;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public long getLamportTime() {
        return lamportTime;
    }

    public void setLamportTime(long lamportTime) {
        this.lamportTime = lamportTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
