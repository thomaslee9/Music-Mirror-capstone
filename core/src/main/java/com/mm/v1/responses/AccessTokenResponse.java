package com.mm.v1.responses;

public class AccessTokenResponse {

    private String access_token;
    private String token_type;
    private String scope;
    private int expires_in;
    private String refresh_token;

    public String getAccessToken()  {

        return this.access_token;

    }

    public String getRefreshToken() {

        return this.refresh_token;

    }
    
}