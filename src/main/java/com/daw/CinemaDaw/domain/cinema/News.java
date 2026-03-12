package com.daw.CinemaDaw.domain.cinema;

public class News {
    private String headline;
    private String body;

    public News(String headline, String body) {
        this.headline = headline;
        this.body = body;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
