package com.ride_share.security;

import io.agora.media.RtcTokenBuilder;



public class AgoraTokenGenerator {
    private static final String APP_ID = "3826ff3d16584c1998cc3f22f5b86a99";
    private static final String APP_CERTIFICATE = "ae2e958884094b0a8c85dc77077ef3e6";
    private static final int TOKEN_EXPIRATION_SECONDS = 3600; // 1 hour

    public static String generateAgoraToken(String channelName, int userId) {
        RtcTokenBuilder tokenBuilder = new RtcTokenBuilder();
        int timestamp = (int) (System.currentTimeMillis() / 1000) + TOKEN_EXPIRATION_SECONDS;
        return tokenBuilder.buildTokenWithUid(APP_ID, APP_CERTIFICATE, channelName, userId,
                RtcTokenBuilder.Role.Role_Publisher, timestamp);
    }

//    public static void main(String[] args) {
//        String token = generateAgoraToken("testChannel", 12345);
//        System.out.println("Generated Token: " + token);
//    }
}
