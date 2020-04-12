package com.orchid.redis.example.model;

/**
 * 文章模型
 */
public class Article {
    private Long id;
    private String title;//标题
    private String link;//网址
    private Long posterId;//发布人id
    private Long time;//发布时间
    private Integer votes;//票数
    private Double score;//评分

    public Article() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getPosterId() {
        return posterId;
    }

    public void setPosterId(Long posterId) {
        this.posterId = posterId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", posterId=" + posterId +
                ", time=" + time +
                ", votes=" + votes +
                ", score=" + score +
                '}';
    }
}
