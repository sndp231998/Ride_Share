package com.ride_share.controller;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.engine.jdbc.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ride_share.entities.RiderTransaction;
import com.ride_share.exceptions.ApiException;
import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.RiderDto;
import com.ride_share.repositories.RiderTransactionRepo;
import com.ride_share.service.FileService;
import com.ride_share.service.RiderService;
import com.ride_share.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/api/v1/")
public class RiderController {

	@Autowired
	private RiderService riderService;

	@Autowired
	private FileService fileService;

	@Autowired
	private RiderTransactionRepo riderTransactionRepo;
	
	@Value("${project.image}")
	private String path;
	
	 private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	 
	@GetMapping("/rider/{riderId}/statement")
	public List<RiderTransaction> getStatement(@PathVariable Integer riderId) {
	    return riderTransactionRepo.findByRiderIdOrderByDateTimeDesc(riderId);
	}


	@PostMapping("/rider/file/upload/{riderId}")
	public ResponseEntity<RiderDto> uploadRiderFile(@RequestParam("file") MultipartFile file,
	                                                @RequestParam("fileType") String fileType,
	                                                @PathVariable Integer riderId) throws IOException {
	   
		 final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

		    // Check file size
		 if (file.getSize() > MAX_FILE_SIZE) {
			    throw new ApiException("File size must be less than 2MB");
			}

		String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

	    if (!fileExtension.equals("pdf") && !fileExtension.equals("jpeg") && !fileExtension.equals("jpg")
	            && !fileExtension.equals("png") && !fileExtension.equals("pptx")) {
	        return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	    }

	    String fileName = this.fileService.uploadFile(path, file);
	    RiderDto riderDto = this.riderService.getRiderById(riderId);

	    // Smart setting of the file based on type
	    switch (fileType.toLowerCase()) {
	        case "selfie":
	            riderDto.setSelfieWithIdCard(fileName);
	            break;
	        case "license":
	            riderDto.setLicense_Image(fileName);
	            break;
	        case "citizen_front":
	            riderDto.setCitizen_Front(fileName);
	            break;
	        case "citizen_back":
	            riderDto.setCitizen_Back(fileName);
	            break;
	        case "nid":
	            riderDto.setNid_Img(fileName);
	            break;
	        default:
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }

	    RiderDto updatedRider = this.riderService.updateRider(riderDto, riderId);
	    return new ResponseEntity<>(updatedRider, HttpStatus.OK);
	}

   
	// Method to serve files of various types
		@GetMapping(value = "/rider/image/{fileName}")
		public void downloadFile(
		        @PathVariable("fileName") String fileName,
		        HttpServletResponse response
		) throws IOException {
		    // Determine the file extension to set content type
		    String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();
		    MediaType mediaType;

		    switch (fileExtension) {
		        case "png":
		            mediaType = MediaType.IMAGE_PNG;
		            break;
		        case "jpg":
		        case "jpeg":
		            mediaType = MediaType.IMAGE_JPEG;
		            break;
		        case "pdf":
		            mediaType = MediaType.APPLICATION_PDF;
		            break;
		        case "pptx":
		            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
		            break;
		        default:
		            mediaType = MediaType.APPLICATION_OCTET_STREAM;
		    }

		    // Set the content type
		    response.setContentType(mediaType.toString());
		    
		    // Set the Content-Disposition header manually
		    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		    // Serve the file
		    try (InputStream resource = this.fileService.getResource(path, fileName)) {
		        StreamUtils.copy(resource, response.getOutputStream());
		    }
    
}
	
	
	@GetMapping("/riders/pending")
	public ResponseEntity<List<RiderDto>> getAllPendingRiders() {
	    List<RiderDto> pendingRiders = riderService.getPendingRiders();
	    return ResponseEntity.ok(pendingRiders);
	}

	
	// create Rider--------------------------------------
	 //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	@PostMapping("/user/{userId}/category/{categoryId}/riders")
	public ResponseEntity<RiderDto> createRider(@RequestBody RiderDto riderDto,
			@PathVariable Integer userId, @PathVariable Integer categoryId) {
		logger.info(riderDto.getDriver_License());
		logger.info(riderDto.getNid_No());
		logger.info(riderDto.getCitizen_No());
		logger.info(riderDto.getCitizen_No());
		RiderDto createRiderDto = this.riderService.createRider(riderDto, userId,categoryId);
		return new ResponseEntity<>(createRiderDto, HttpStatus.CREATED);
	}
	// Update Rider----------------------------------
    @PutMapping("rider/{riderId}")
    public ResponseEntity<RiderDto> updateRider(@RequestBody RiderDto riderDto, 
                                                @PathVariable Integer riderId) {
        RiderDto updatedRider = this.riderService.updateRider(riderDto, riderId);
        return new ResponseEntity<>(updatedRider, HttpStatus.OK);
    }

    @PutMapping("rider/{riderId}/balance")
    public ResponseEntity<RiderDto> addBalance(@PathVariable Integer riderId, @RequestBody RiderDto riderDto) {
        RiderDto updatedRider = riderService.addBalanceOfRider(riderDto, riderId);
        return ResponseEntity.ok(updatedRider);
    }
    // Delete Rider--------------------------
    @DeleteMapping("rider/{riderId}")
    public ResponseEntity<ApiResponse> deleteRider(@PathVariable Integer riderId) {
        this.riderService.deleteRider(riderId);
        return new ResponseEntity<>(new ApiResponse("Rider deleted successfully", true), HttpStatus.OK);
    }
    
    
    @GetMapping("/user/{userId}/riders")
	public ResponseEntity<List<RiderDto>> getRidersByUser(@PathVariable Integer userId) {

		List<RiderDto> riders = this.riderService.getRidersByUser(userId);
		return new ResponseEntity<List<RiderDto>>(riders, HttpStatus.OK);

	}
    
    
 // get rider details by id
 	//@PreAuthorize("hasRole('ADMIN')")
 	@GetMapping("/riders/{riderId}")
 	public ResponseEntity<RiderDto> getRiderById(@PathVariable Integer riderId) {

 		RiderDto riderDto = this.riderService.getRiderById(riderId);
 		return new ResponseEntity<RiderDto>(riderDto, HttpStatus.OK);

 	}

 	 // GET All - Riders
 	@GetMapping("/riders")
 	public ResponseEntity<List<RiderDto>> getAllRiders() {
 		return ResponseEntity.ok(this.riderService.getAllRiders());
 	}

		
	    @PutMapping("/{riderId}/reject")
	    public ResponseEntity<RiderDto> rejectRider(@RequestBody RiderDto riderDto,@PathVariable Integer riderId) {
	        RiderDto rejectedRequest = riderService.rejectRider(riderDto,riderId);
	        		
	        return ResponseEntity.ok(rejectedRequest);
	    }
	    @PutMapping("/{riderId}/approved")
	    public ResponseEntity<RiderDto> approvedRider(@PathVariable Integer riderId) {
	        RiderDto approvedRequest = riderService.approveRider(riderId); 		
	        return ResponseEntity.ok(approvedRequest);
	    }

}

