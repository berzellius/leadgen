package com.leadgen.service;

import com.leadgen.dmodel.Comment;
import com.leadgen.dmodel.Order;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by berz on 24.11.14.
 */
public interface CommentService {
    void attachFilesToComment(Comment comment, List<MultipartFile> multipartFileList, List<String> fileNames);

    void eraseCommentsForOrder(Order order);
}
