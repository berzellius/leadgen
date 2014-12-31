package com.leadgen.service;

import com.leadgen.dmodel.Comment;
import com.leadgen.dmodel.Order;
import com.leadgen.dmodel.UploadedFile;
import com.leadgen.exceptions.UploadFileException;
import com.leadgen.specifications.CommentSpecifications;
import com.leadgen.repository.CommentRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Date;

/**
 * Created by berz on 24.11.14.
 */
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    UploadedFileService uploadedFileService;

    @Autowired
    CommentRepository commentRepository;

    @Override
    public void attachFilesToComment(Comment comment, List<MultipartFile> multipartFileList, List<String> fileNames) {
        for(MultipartFile file : multipartFileList){
            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setName(
                    fileNames.size() > multipartFileList.indexOf(file) &&
                            fileNames.get(multipartFileList.indexOf(file)) != null &&
                            !fileNames.get(multipartFileList.indexOf(file)).equals("") ?
                            fileNames.get(multipartFileList.indexOf(file)) : FilenameUtils.getBaseName(file.getOriginalFilename())
            );
            uploadedFile.setComment(comment);

            try{
                uploadedFileService.uploadFile(uploadedFile, file);
            } catch (UploadFileException e) {
                //
            }
        }
    }

    @Override
    public void eraseCommentsForOrder(Order order) {

        List<Comment> comments = commentRepository.findAll(CommentSpecifications.forOrder(order));
        for(Comment comment : comments){
            comment.setDisabled(true);
            comment.setDtmUpdate(new Date());
        }

        commentRepository.save(comments);
    }
}
