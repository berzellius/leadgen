package com.leadgen.service;

import com.leadgen.enumerated.TicketStatus;
import com.leadgen.exceptions.UploadFileException;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.dmodel.*;
import com.leadgen.repository.CommentRepository;
import com.leadgen.repository.TicketRepository;
import com.leadgen.util.UserLoginUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * Created by berz on 19.11.14.
 */
@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UploadedFileService uploadedFileService;

    @Autowired
    CommentService commentService;

    @Override
    public void saveTicket(Ticket ticket, User owner, Client client, List<MultipartFile> multipartFileList, List<String> fileNames) throws WrongInputDataException {
        if(ticket.getName() == null || ticket.getName().equals(""))
            throw new WrongInputDataException("name attr of tickets entity not filled!", WrongInputDataException.Reason.NAME_FIELD);
        if(ticket.getDescription() == null || ticket.getDescription().equals(""))
            throw new WrongInputDataException("description name of tickets not filled!", WrongInputDataException.Reason.DESCRIPTION_FIELD);

        ticket.setOwner(owner);
        ticket.setClient(client);
        ticket.setDtmCreate(new Date());
        ticket.setStatus(TicketStatus.NEW);

        attachFilesToTicket(ticket, multipartFileList, fileNames);

        ticketRepository.save(ticket);
    }

    @Override
    public void attachFilesToTicket(Ticket ticket, List<MultipartFile> multipartFileList, List<String> fileNames) {

        for(MultipartFile file : multipartFileList){
            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setName(
                    fileNames.size() > multipartFileList.indexOf(file) &&
                            fileNames.get(multipartFileList.indexOf(file)) != null &&
                            !fileNames.get(multipartFileList.indexOf(file)).equals("") ?
                        fileNames.get(multipartFileList.indexOf(file)) : FilenameUtils.getBaseName(file.getOriginalFilename())
            );
            uploadedFile.setTicket(ticket);

            try{
                uploadedFileService.uploadFile(uploadedFile, file);
            } catch (UploadFileException e) {
                //
            }
        }
    }

    @Override
    public boolean checkOwnership(Ticket ticket, User currentUser) {
        return ticket.getClient() != null &&
                (
                                (
                                        (currentUser.getClient()!= null &&
                                                ticket.getClient().equals(currentUser.getClient()))&&
                                            userLoginUtil.userHaveRole(currentUser, UserRole.Role.CLIENT)

                                                                    ) ||
                                ticket.getOwner().equals(currentUser) ||
                                userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN)
                        );
    }

    @Override
    public void addComment(Ticket ticket, String textComment, User currentUser, List<MultipartFile> multipartFileList, List<String> fileNames) {

        Comment comment = new Comment();
        comment.setDisabled(false);
        comment.setText(textComment);
        comment.setTicket(ticket);
        comment.setUser(currentUser);
        comment.setDtmCreate(new Date());

        ticket.setStatus(TicketStatus.WAIT);

        commentService.attachFilesToComment(comment, multipartFileList, fileNames);

        ticketRepository.save(ticket);
        commentRepository.save(comment);
    }


    @Override
    public void done(Ticket ticket) {
        ticket.setStatus(TicketStatus.DONE);

        ticketRepository.save(ticket);
    }

    @Override
    public void close(Ticket ticket) {
        ticket.setStatus(TicketStatus.CLOSED);

        ticketRepository.save(ticket);
    }
}
