package com.bupocket.http.api.dto.resp;

public class GetUserTokenRespDto {

    /**
     * expiresIn : 604800000
     * userToken : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJCVVBPQ0tFVCIsImV4cCI6MTUzODU1NTc0NiwidXNlcklkIjoiYWVlMWIxYWQ4YzQ5ZmM5MWYxODFmYTBmODA5ZjM0OWUifQ.9johytvyhrSBqs_GMav9GI8vDe1Knfk9to7AQpAgKKc
     */

    private int expiresIn;
    private String userToken;

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}