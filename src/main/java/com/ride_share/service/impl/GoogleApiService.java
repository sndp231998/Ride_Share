//package com.ride_share.service.impl;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//public class GoogleApiService {
//
//    @Value("${google.api.key}")
//    private String apiKey;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public String getDirections(String origin, String destination) {
//        String url = "https://maps.googleapis.com/maps/api/directions/json?"
//                   + "origin=" + origin + "&destination=" + destination
//                   + "&key=" + apiKey;
//        
//        return restTemplate.getForObject(url, String.class);
//    }
//    
//    public String getPlaceDetails(String placeId) {
//        String url = "https://maps.googleapis.com/maps/api/place/details/json?"
//                   + "place_id=" + placeId
//                   + "&key=" + apiKey;
//        
//        return restTemplate.getForObject(url, String.class);
//    }
//
//}
