package com.ride_share.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppConstants {

	public static final String PAGE_NUMBER = "0";
	public static final String PAGE_SIZE = "10";
	public static final String SORT_BY = "postId";
	public static final String SORT_DIR = "asc";
	public static final Integer SUPER_ADMIN_USER = 500;
	public static final Integer ADMIN_USER=501;
	public static final Integer NORMAL_USER = 502;
	public static final Integer BRANCH_MANAGER_USER=503;
	public static final Integer RIDER_USER = 504;
	
	public static final Integer DAMAK_BRANCH=601;
	public static final Integer KATHMANDU_BRANCH=602;
	
	
	public static final List<String> VALID_PROVINCES = Collections.unmodifiableList(Arrays.asList(
		    "Koshi Province",
		    "Madhesh Province",
		    "Bagamati Province",
		    "Gandaki Province",
		    "Lumbini Province",
		    "Karnali Province",
		    "Sudurpashchim Province"
		));


	
}
