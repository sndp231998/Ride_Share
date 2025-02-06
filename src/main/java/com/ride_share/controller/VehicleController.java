package com.ride_share.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.http.MediaType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.VehicleDto;
import com.ride_share.service.FileService;
import com.ride_share.service.VehicleService;


@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

  @Autowired
  private FileService fileService;

  @Value("${project.image}")
  private String path;
    
    @PostMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<VehicleDto> createVehicle(@RequestBody VehicleDto vehicleDto,
                                                    @PathVariable Integer userId,
                                                    @PathVariable Integer categoryId) {
        VehicleDto createdVehicle = vehicleService.createVehicle(vehicleDto, userId, categoryId);
        return new ResponseEntity<>(createdVehicle, HttpStatus.CREATED);
    }
    
    
    @PutMapping("/{vehicleId}") 
    public ResponseEntity<VehicleDto> updateVehicle(@RequestBody VehicleDto vehicleDto,
    		@PathVariable Integer vehicleId) { 
    	VehicleDto updatedVehicle = vehicleService.updateVehicle(vehicleDto, vehicleId);
    	return new ResponseEntity<>(updatedVehicle, HttpStatus.OK); 
    	}


// Delete Vehicle
@DeleteMapping("/{vehicleId}")
public ResponseEntity<ApiResponse> deleteVehicle(@PathVariable Integer vehicleId) {
  this.vehicleService.deleteVehicle(vehicleId);
  return new ResponseEntity<>(new ApiResponse("Vehicle deleted successfully", true, vehicleId), HttpStatus.OK);
}




//--get rider by vehcles by v id
@GetMapping("/{vehicleId}")
public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Integer vehicleId) {

	VehicleDto vehicleDto = this.vehicleService.getVehicleById(vehicleId);
	return new ResponseEntity<VehicleDto>(vehicleDto, HttpStatus.OK);

}




@GetMapping("/user/{userId}/")
public ResponseEntity<List<VehicleDto>> getVehiclesByUser(@PathVariable Integer userId) {

	List<VehicleDto> vehicles = this.vehicleService.getVehiclesByUser(userId);
	return new ResponseEntity<List<VehicleDto>>(vehicles, HttpStatus.OK);

}

// get by category
//@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/category/{categoryId}/")
public ResponseEntity<List<VehicleDto>> getVehiclesByCategory(@PathVariable Integer categoryId) {

	List<VehicleDto> vehicles = this.vehicleService.getVehiclesByCategory(categoryId);
	return new ResponseEntity<List<VehicleDto>>(vehicles, HttpStatus.OK);

}



// GET All - vehicles
@GetMapping("/")
public ResponseEntity<List<VehicleDto>> getAllVehicles() {
	return ResponseEntity.ok(this.vehicleService.getAllVehicles());
}


// Updated upload method for multiple file types
//------vehicle blueBook img 1---------------------
@PostMapping("/bluebook1/upload/{vehicleId}")
public ResponseEntity<VehicleDto> uploadVehicleFile1(@RequestParam("file") MultipartFile file,
                                             @PathVariable Integer vehicleId) throws IOException {
   // Get the file extension in lowercase
   String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

   // Allowable file types
   if (!fileExtension.equals("pdf") && !fileExtension.equals("jpeg") && !fileExtension.equals("jpg")
           && !fileExtension.equals("png")) {
       return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
   }

   // Continue with file upload
   VehicleDto vehicleDto = this.vehicleService.getVehicleById(vehicleId);
   String fileName = this.fileService.uploadFile(path, file);
   vehicleDto.setBillBook1(fileName);// Assuming imageName is used for storing any file type name
  
   //vehicleDto.setVechicle_Img(fileName);

   VehicleDto updatedVehicle = this.vehicleService.updateVehicle(vehicleDto, vehicleId);
   		
   return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
}

//------vehicle blueBook img 2---------------------
@PostMapping("/bluebook2/upload/{vehicleId}")
public ResponseEntity<VehicleDto> uploadVehicleFile2(@RequestParam("file") MultipartFile file,
                                             @PathVariable Integer vehicleId) throws IOException {
   // Get the file extension in lowercase
   String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

   // Allowable file types
   if (!fileExtension.equals("pdf") && !fileExtension.equals("jpeg") && !fileExtension.equals("jpg")
           && !fileExtension.equals("png")) {
       return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
   }

   // Continue with file upload
   VehicleDto vehicleDto = this.vehicleService.getVehicleById(vehicleId);
   String fileName = this.fileService.uploadFile(path, file);
   vehicleDto.setBillBook2(fileName);
  VehicleDto updatedVehicle = this.vehicleService.updateVehicle(vehicleDto, vehicleId);
   		
   return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
}

//----------vehicle image upoad---------------------
@PostMapping("/image/upload/{vehicleId}")
public ResponseEntity<VehicleDto> uploadVehicleFile3(@RequestParam("file") MultipartFile file,
                                             @PathVariable Integer vehicleId) throws IOException {
   // Get the file extension in lowercase
   String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

   // Allowable file types
   if (!fileExtension.equals("pdf") && !fileExtension.equals("jpeg") && !fileExtension.equals("jpg")
           && !fileExtension.equals("png")) {
       return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
   }

   // Continue with file upload
   VehicleDto vehicleDto = this.vehicleService.getVehicleById(vehicleId);
   String fileName = this.fileService.uploadFile(path, file);
   vehicleDto.setVechicleImg(fileName);
  VehicleDto updatedVehicle = this.vehicleService.updateVehicle(vehicleDto, vehicleId);
   		
   return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
}

//---------------- Method to serve files of various types-------------------
	@GetMapping(value = "/image/{fileName}")
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

	
	
	// search
		@GetMapping("/search/{keywords}")
		public ResponseEntity<List<VehicleDto>> searchVehicles(@PathVariable String keywords) {
			List<VehicleDto> result = this.vehicleService.searchVehicles(keywords);
			return new ResponseEntity<List<VehicleDto>>(result, HttpStatus.OK);
		}
}



