package com.ride_share.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.core.env.Environment;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;


import com.ride_share.playoads.DistanceMatrixResponse;
import com.ride_share.service.MapService;


@Service
public class MapServiceImpl implements MapService {
	private final Environment environment;

    public MapServiceImpl(Environment environment) {
        this.environment = environment;
    }



    @Override
    public DistanceMatrixResponse getDistanceMatrixData(double sourceLat, double sourceLng, double destLat, double destLng) throws Exception {
        String origins = sourceLat + "," + sourceLng;
        String destinations = destLat + "," + destLng;

        String urlStr = "https://maps.googleapis.com/maps/api/directions/json?origin=" + URLEncoder.encode(origins, "UTF-8")
        + "&destination=" + URLEncoder.encode(destinations, "UTF-8")
        + "&key=" + environment.getProperty("google.api.key");  // Corrected here

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

//  @Override
//  public String getDistanceAndTime(double sourceLat, double sourceLng, double destLat, double destLng) {
//      String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
//              + sourceLat + "," + sourceLng +
//              "&destinations=" + destLat + "," + destLng +
//              "&key=" + GOOGLE_API_KEY;
//
//      RestTemplate restTemplate = new RestTemplate();
//      return restTemplate.getForObject(url, String.class);
//  }
  


//DistanceMatrixResponse getDistanceMatrixData(double sourceLat, double sourceLng, double destLat, double destLng) throws Exception;

//@Override
//public DistanceMatrixResponse getDistanceMatrixData(double sourceLat, double sourceLng, double destLat, double destLng) throws Exception {
//    String origins = sourceLat + "," + sourceLng;
//    String destinations = destLat + "," + destLng;
//    String apiKey = "kasarw9wCZdbbpi9Trj7SZrwwSvxwczDiO2NUPtDGlwdvFiFyc32kzjhDfo5CeY8";
//
//    String urlStr = "https://api.distancematrix.ai/maps/api/distancematrix/json?origins=" + URLEncoder.encode(origins, "UTF-8")
//            + "&destinations=" + URLEncoder.encode(destinations, "UTF-8")
//            + "&key=" + apiKey;
//
//    URL url = new URL(urlStr);
//    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//    conn.setRequestMethod("GET");
//    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
//
//    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//    StringBuilder response = new StringBuilder();
//    String inputLine;
//    while ((inputLine = in.readLine()) != null) {
//        response.append(inputLine);
//    }
//    in.close();
//
//    JSONObject jsonResponse = new JSONObject(response.toString());
//
//    String originAddress = jsonResponse.getJSONArray("origin_addresses").getString(0);
//    String destinationAddress = jsonResponse.getJSONArray("destination_addresses").getString(0);
//
//    JSONObject element = jsonResponse.getJSONArray("rows")
//            .getJSONObject(0)
//            .getJSONArray("elements")
//            .getJSONObject(0);
//
//    String origin = originAddress;  // or element.getString("origin") if present
//    String destination = destinationAddress; // or element.getString("destination") if present
//
//    double distance = element.getJSONObject("distance").getDouble("value") / 1000.0;  // meters to km
//    double duration = element.getJSONObject("duration").getDouble("value") / 60.0;    // seconds to minutes
//
//    return new DistanceMatrixResponse(
//            originAddress,
//            destinationAddress,
//            origin,
//            destination,
//            distance,
//            duration
//    );
//}

// https://maps.googleapis.com/maps/api/directions/json?origin=27.686246107892664,85.41398706592108&destination=27.71599805854803,85.37976128237251&key=AIzaSyAXs9F40dZwRZVioQJiXw4S82ZQ5dWFaXw

//call garne tarika
//Map<String, String> data = getDistanceMatrixData(
//	    27.686284293110297, 85.41387816149667, // source lat, lng
//	    27.715993329218495, 85.37965796635491  // destination lat, lng
//	);
//
//	System.out.println("From: " + data.get("originAddress"));
//	System.out.println("To: " + data.get("destinationAddress"));
//	System.out.println("Distance: " + data.get("distanceText"));
//	System.out.println("Duration: " + data.get("durationText"));


//public String getCityName(double latitude, double longitude) throws Exception {
//    String urlStr = "https://maps.googleapis.com/maps/api/geocode/json?latlng=en" + latitude + "," + longitude + "&key=" + GOOGLE_API_KEY;
//  
//    URL url = new URL(urlStr);
//    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//    conn.setRequestMethod("GET");
//    
//    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//    String inputLine;
//    StringBuilder response = new StringBuilder();
//    
//    while ((inputLine = in.readLine()) != null) {
//        response.append(inputLine);
//    }
//    in.close();
//    
//    JSONObject jsonResponse = new JSONObject(response.toString());
//    if ("OK".equals(jsonResponse.getString("status"))) {
//        JSONArray results = jsonResponse.getJSONArray("results");
//        if (results.length() > 0) {
//            JSONObject addressComponents = results.getJSONObject(0);
//            JSONArray components = addressComponents.getJSONArray("address_components");
//            for (int i = 0; i < components.length(); i++) {
//                JSONObject component = components.getJSONObject(i);
//                JSONArray types = component.getJSONArray("types");
//                for (int j = 0; j < types.length(); j++) {
//                    if ("locality".equals(types.getString(j))) {
//                        return component.getString("long_name");
//                    }
//                }
//            }
//        }
//    }
//    throw new Exception("City not found");
//}
