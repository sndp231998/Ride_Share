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
