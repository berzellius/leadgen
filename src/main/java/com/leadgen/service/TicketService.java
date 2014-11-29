package com.leadgen.service;

import com.leadgen.dmodel.Client;
import com.leadgen.dmodel.Ticket;
import com.leadgen.dmodel.User;
import com.leadgen.exceptions.WrongInputDataException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by berz on 19.11.14.
 */
@Service
public interface TicketService {


    public void saveTicket(Ticket ticket, User owner, Client client, List<MultipartFile> multipartFileList, List<String> fileNames) throws WrongInputDataException;

    void attachFilesToTicket(Ticket ticket, List<MultipartFile> multipartFileList, List<String> fileNames);

    boolean checkOwnership(Ticket ticket, User currentUser);

    void addComment(Ticket ticket, String textComment, User currentUser, List<MultipartFile> multipartFileList, List<String> fileNames);

    void done(Ticket ticket);

    void close(Ticket ticket);

}
