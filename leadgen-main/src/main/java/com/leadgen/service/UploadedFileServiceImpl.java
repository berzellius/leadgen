package com.leadgen.service;

import com.leadgen.dmodel.UploadedFile;
import com.leadgen.exceptions.UploadFileException;
import com.leadgen.repository.UploadedFileRepository;
import com.leadgen.settings.ProjectSettings;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by berz on 23.11.14.
 */
@Service
@Transactional
public class UploadedFileServiceImpl implements UploadedFileService {

    @Autowired
    ProjectSettings projectSettings;

    @Autowired
    UploadedFileRepository uploadedFileRepository;

    @Override
    public void uploadFile(UploadedFile uploadedFile, MultipartFile file) throws UploadFileException {

        assert projectSettings.getPathToUploads() != null;
        assert projectSettings.getAllowedFileMimeTypes() != null;

        if(!projectSettings.getAllowedFileMimeTypes().contains(file.getContentType()))
            throw new UploadFileException(
                    "MIME type not allowed! "
                            .concat(file.getName())
                            .concat(" ")
                            .concat(file.getContentType())
            );

        uploadedFile.setMimeType(file.getContentType());

        uploadedFile.setPath(this.generateFilename(file));
        uploadedFile.setExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
        uploadedFile.setFilename(file.getOriginalFilename());

        uploadedFile.setDtmCreate(new Date());

        try {
            file.transferTo(new File(projectSettings.getPathToUploads().concat("/").concat(uploadedFile.getPath())));

            uploadedFileRepository.save(uploadedFile);

        } catch (IOException e) {
            throw new UploadFileException("input/output error for ".concat(file.getOriginalFilename()));
        }

    }

    private String generateFilename(MultipartFile file) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(
                    ((String) file.getOriginalFilename() + file.getSize() + new Date().toString()).getBytes("UTF-8")
            );
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; ++i) {
                sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().concat(".").concat( FilenameUtils.getExtension(file.getOriginalFilename()) );
        } catch (NoSuchAlgorithmException e) {
            return file.getOriginalFilename();
        } catch (UnsupportedEncodingException e) {
            return file.getOriginalFilename();
        }

    }
}
