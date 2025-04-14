//package com.ride_share.controller;
//import org.springframework.web.bind.annotation.*;
//
//import com.ride_share.service.impl.GoogleApiService;
//
//@RestController
//@RequestMapping("/api/google")
//public class GoogleApiController {
//
//    private final GoogleApiService googleApiService;
//
//    public GoogleApiController(GoogleApiService googleApiService) {
//        this.googleApiService = googleApiService;
//    }
//
//    @GetMapping("/directions")
//    public String getDirections(@RequestParam String origin, @RequestParam String destination) {
//        return googleApiService.getDirections(origin, destination);
//    }
//}
