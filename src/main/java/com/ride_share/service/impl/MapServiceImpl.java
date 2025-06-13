package com.ride_share.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.Environment;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.ride_share.RideShareApplication;
import com.ride_share.entities.Category;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.DistanceMatrixResponse;
import com.ride_share.repositories.CategoryRepo;
import com.ride_share.service.MapService;


@Service
public class MapServiceImpl implements MapService {
	//https://maps.googleapis.com/maps/api/distancematrix/json?origins=27.031629514446895,84.89380051873742&destinations=27.163661784138665,84.9796605549453&mode=driving&key=AIzaSyAXs9F40dZwRZVioQJiXw4S82ZQ5dWFaXw
//	@Autowired
//	CategoryRepo categoryRepo;
//	
    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @Override
    public DistanceMatrixResponse getDistanceMatrixData(double sourceLat, double sourceLng, double destLat, double destLng) throws Exception {
        String origins = sourceLat + "," + sourceLng;
        String destinations = destLat + "," + destLng;

        String urlStr = "https://maps.googleapis.com/maps/api/directions/json?origin=" + URLEncoder.encode(origins, "UTF-8")
        + "&destination=" + URLEncoder.encode(destinations, "UTF-8")
        + "&mode=driving" 
        + "&key=" + googleMapsApiKey; // ✅ fixed here
  // Corrected here

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());

        JSONArray routes = jsonResponse.getJSONArray("routes");
        if (routes.length() == 0) {
            throw new Exception("No routes found.");
        }

        JSONObject leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);

        String originAddress = leg.getString("start_address");
        String destinationAddress = leg.getString("end_address");

        JSONObject distanceObj = leg.getJSONObject("distance");
        JSONObject durationObj = leg.getJSONObject("duration");

        double distanceKm = distanceObj.getDouble("value") / 1000.0;
        double durationMin = durationObj.getDouble("value") / 60.0;

        return new DistanceMatrixResponse(
                originAddress,
                destinationAddress,
                distanceKm,
                durationMin
        );
    }
    
    @Override
    public DistanceMatrixResponse getDistanceMatrixDataa(double sourceLat, double sourceLng, double destLat, double destLng) throws Exception {
        String origins = sourceLat + "," + sourceLng;
        String destinations = destLat + "," + destLng;

        String urlStr = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origins
                + "&destinations=" + destinations
                + "&mode=driving"
                + "&key="+googleMapsApiKey;

        System.out.println("Final URL: " + urlStr);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());

        if (!jsonResponse.getString("status").equals("OK")) {
            throw new Exception("Distance Matrix API Error: " + jsonResponse.getString("status"));
        }

        String originAddress = jsonResponse.getJSONArray("origin_addresses").getString(0);
        String destinationAddress = jsonResponse.getJSONArray("destination_addresses").getString(0);

        JSONObject element = jsonResponse.getJSONArray("rows")
                .getJSONObject(0)
                .getJSONArray("elements")
                .getJSONObject(0);

        if (!element.getString("status").equals("OK")) {
            throw new Exception("Distance Matrix element error: " + element.getString("status"));
        }

        double distanceKm = element.getJSONObject("distance").getDouble("value") / 1000.0;
        double durationMin = element.getJSONObject("duration").getDouble("value") / 60.0;

        return new DistanceMatrixResponse(
                originAddress,
                destinationAddress,
                distanceKm,
                durationMin
        );
    }

    
    //27.031629514446895,84.89380051873742&destinations=27.163661784138665,84.9796605549453
    
//    public static void main(String[] args) {
//		SpringApplication.run(RideShareApplication.class, args);
//		
//		 try {
//			 MapServiceImpl app = new  MapServiceImpl();
//
//		        // Sample coordinates
//		        double sourceLat = 27.031629514446895;
//		        double sourceLng = 84.89380051873742;
//		        double destLat = 27.163661784138665;
//		        double destLng = 84.9796605549453;
//
//		        DistanceMatrixResponse result=app.getDistanceMatrixData(sourceLat, sourceLng, destLat, destLng);
//		        //DistanceMatrixResponse result = app.getDistanceMatrixDataa(sourceLat, sourceLng, destLat, destLng);
//
//		        System.out.println("Origin: " + result.getOriginAddress());
//		        System.out.println("Destination: " + result.getDestinationAddress());
//		        System.out.println("Distance (km): " + result.getDistanceKm());
//		        System.out.println("Duration (min): " + result.getDurationMin());
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		    }
//		}
    
    
    
    
    
    
    
    
    
    
    
//    public DistanceMatrixResponse getMapHelper(double sourceLat, double sourceLng, double destLat, double destLng,Integer categoryId) throws Exception {
//    	Category category = this.categoryRepo.findById(categoryId)
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id ", categoryId));
//
//    	String origins = sourceLat + "," + sourceLng;
//        String destinations = destLat + "," + destLng;
//
//        String urlStr = "https://maps.googleapis.com/maps/api/directions/json?origin=" + URLEncoder.encode(origins, "UTF-8")
//        + "&destination=" + URLEncoder.encode(destinations, "UTF-8")
//        + "&key=" + googleMapsApiKey; // ✅ fixed here
//  // Corrected here
//
//        URL url = new URL(urlStr);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        StringBuilder response = new StringBuilder();
//        String inputLine;
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//        JSONObject jsonResponse = new JSONObject(response.toString());
//
//        JSONArray routes = jsonResponse.getJSONArray("routes");
//        if (routes.length() == 0) {
//            throw new Exception("No routes found.");
//        }
//
//        JSONObject leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
//
//        String originAddress = leg.getString("start_address");
//        String destinationAddress = leg.getString("end_address");
//
//        JSONObject distanceObj = leg.getJSONObject("distance");
//        JSONObject durationObj = leg.getJSONObject("duration");
//
//        double distanceKm = distanceObj.getDouble("value") / 1000.0;
//        double durationMin = durationObj.getDouble("value") / 60.0;
//
//        return new DistanceMatrixResponse(
//                originAddress,
//                destinationAddress,
//                distanceKm,
//                durationMin
//        );
//    }


   
    public String getState(double latitude, double longitude) throws Exception {
        String urlStr = "https://nominatim.openstreetmap.org/reverse?format=json&accept-language=en&lat=" + latitude + "&lon=" + longitude;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());

        if (jsonResponse.has("address")) {
            JSONObject address = jsonResponse.getJSONObject("address");
            if (address.has("state")) {
                return address.getString("state");
            }
        }

        throw new Exception("State not found in the response.");
    }

    }
