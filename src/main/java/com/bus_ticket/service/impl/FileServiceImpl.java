package com.bus_ticket.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bus_ticket.service.FileService;



@Service
public class FileServiceImpl implements FileService {

    // List of allowed extensions
    private final List<String> allowedExtensions = Arrays.asList("jpeg", "jpg", "png", "pdf", "pptx");

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // File name
        String name = file.getOriginalFilename();

        // Validate file type
        String extension = FilenameUtils.getExtension(name).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            throw new IOException("Invalid file type");
        }

        // Generate random file name
        String randomID = UUID.randomUUID().toString();
        String fileName1 = randomID.concat(".").concat(extension);

        // Full path
        String filePath = path + File.separator + fileName1;

        // Create folder if not created
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }

        // Copy file to the specified path
      //  Files.copy(file.getInputStream(), Paths.get(filePath));
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return fileName1;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        String fullPath = path + File.separator + fileName;
        InputStream is = new FileInputStream(fullPath);
        return is;
    }

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // You can keep this method if you still need it, or remove it if not needed
        return uploadFile(path, file);
    }
}
