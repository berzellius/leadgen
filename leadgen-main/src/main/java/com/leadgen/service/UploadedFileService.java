package com.leadgen.service;

import com.leadgen.dmodel.UploadedFile;
import com.leadgen.exceptions.UploadFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by berz on 23.11.14.
 */
@Service
public interface UploadedFileService {
    void uploadFile(UploadedFile uploadedFile, MultipartFile file) throws UploadFileException;
}
