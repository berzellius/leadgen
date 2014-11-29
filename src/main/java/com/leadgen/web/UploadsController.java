package com.leadgen.web;

import com.leadgen.dmodel.UploadedFile;
import com.leadgen.repository.UploadedFileRepository;
import com.leadgen.settings.ProjectSettings;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by berz on 23.11.14.
 */
@Controller
@RequestMapping(value = "/uploads")
public class UploadsController {

    @Autowired
    UploadedFileRepository uploadedFileRepository;

    @Autowired
    ProjectSettings projectSettings;


    @RequestMapping(value = {"/{action}/{id}.{ext}", "/{id}.{ext}"}, method = RequestMethod.GET)
    void getFile(
            @PathVariable("id") Long id,
            @PathVariable("ext") String extension,
            @PathVariable("action") String action,
            HttpServletResponse response,
            HttpServletRequest request
    ) throws NotFoundException {
        UploadedFile uploadedFile = uploadedFileRepository.findOne(id);

        if(uploadedFile == null) throw new NotFoundException("file not found!");

        File file = null;
        try {
            file = new File(projectSettings.getPathToUploads().concat("/").concat(uploadedFile.getPath()));

            response.setCharacterEncoding("UTF-8");

            if(uploadedFile.getMimeType() != null)
                response.setHeader("Content-Type", uploadedFile.getMimeType().concat("; charset=UTF-8"));
            if(action != null && action.equals("download"))
                response.setHeader("Content-Disposition",
                    "attachment; filename=\""
                            .concat("leadgnerator_")
                            .concat(uploadedFile.getId().toString())
                            .concat(".")
                            .concat(uploadedFile.getExtension())
                            .concat("\""));

            InputStream inputStream = new FileInputStream(file);
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (FileNotFoundException e) {
            throw new NotFoundException("file not found!");
        } catch (IOException e) {
            throw new NotFoundException("file not found!");
        }
    }
}
