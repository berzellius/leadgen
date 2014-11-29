package com.leadgen.web;

import com.leadgen.dmodel.Comment;
import com.leadgen.dmodel.Ticket;
import com.leadgen.dmodel.User;
import com.leadgen.dmodel.UserRole;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.repository.CommentRepository;
import com.leadgen.repository.TicketRepository;
import com.leadgen.service.TicketService;
import com.leadgen.specifications.CommentSpecifications;
import com.leadgen.specifications.TicketSpecifications;
import com.leadgen.util.UserLoginUtil;
import com.leadgen.wrappers.PageWrapper;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 19.11.14.
 */
@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    TicketService ticketService;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    CommentRepository commentRepository;

    @RequestMapping
    public String tickets(
            Model model,
            @PageableDefault(page = 0, value = 20)
            Pageable pageable,
            @RequestParam(value = "archive", required = false)
            String archive
    ) {

        User currentUser = userLoginUtil.getCurrentLogInUser();

        Page<Ticket> tickets = null;

        List<Sort.Order> ordering = new LinkedList<Sort.Order>();
        ordering.add(new Sort.Order(
                (userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN))? Sort.Direction.ASC : Sort.Direction.DESC,
                "status"
        ));
        Sort sortOrders = new Sort(ordering);

        Pageable pageableTickets = new PageRequest(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortOrders
        );

        if (userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN))
            tickets = ticketRepository.findAll(
                    Specifications.where(
                            TicketSpecifications.ticketsForAdmin()
                    )
                            .and(
                                    (archive == null)? TicketSpecifications.notClosed() : TicketSpecifications.isClosed()
                            )
                    ,
                    pageableTickets
            );

        if (userLoginUtil.userHaveRole(currentUser, UserRole.Role.CLIENT))
            tickets = ticketRepository.findAll(
                    Specifications.where(
                            TicketSpecifications.ticketsForClient(currentUser.getClient())
                    )
                            .and(
                                    (archive == null)? TicketSpecifications.notClosed() : TicketSpecifications.isClosed()
                            )
                    ,
                    pageableTickets
            );

        if (userLoginUtil.userHaveRole(currentUser, UserRole.Role.MANAGER))
            tickets = ticketRepository.findAll(
                    Specifications.where(
                            TicketSpecifications.ticketsForManager(currentUser)
                    )
                            .and(
                                    (archive == null) ? TicketSpecifications.notClosed() : TicketSpecifications.isClosed()
                            )
                    ,
                    pageableTickets
            );

        if(archive != null) model.addAttribute("archive", true);
        model.addAttribute("tickets", tickets);

        PageWrapper<Ticket> page = new PageWrapper<Ticket>(tickets, "/tickets");
        model.addAttribute("page", page);

        return "tickets/all";
    }

    @RequestMapping(value = "new", method = RequestMethod.POST)
    public String newTicket(
            Ticket ticket,
            final RedirectAttributes redirectAttributes,
            @RequestParam(value = "attachFile[]", required = false) List<MultipartFile> multipartFileList,
            @RequestParam(value = "fileName[]", required = false) List<String> fileNames
    ) {

        User currentUser = userLoginUtil.getCurrentLogInUser();

        if (currentUser.getClient() == null) throw new IllegalAccessError("users without client cant create tickets!");

        try {
            ticketService.saveTicket(ticket, currentUser, currentUser.getClient(), multipartFileList, fileNames);
        } catch (WrongInputDataException e) {
            redirectAttributes.addFlashAttribute(ticket);
            redirectAttributes.addAttribute("reason", e.getReason());

            return "redirect:/tickets/new";
        }

        return "redirect:/tickets";
    }

    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newTicketForm(
            Model model,
            @RequestParam(value = "reason", required = false)
            WrongInputDataException.Reason reason
    ) {
        if (reason != null) model.addAttribute("reason", reason);

        return "tickets/form";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String ticket(
            Model model,
            @PathVariable(value = "id")
            Long ticketId,
            @PageableDefault(page = 0, value = 20, sort = "dtmCreate", direction = Sort.Direction.DESC)
            Pageable pageableComments
    ) throws NotFoundException, IllegalAccessException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        Ticket ticket = ticketRepository.findOne(ticketId);

        if (ticket == null) throw new NotFoundException("cant find ticket with id=".concat(ticketId.toString()));

        if (!ticketService.checkOwnership(ticket, currentUser))
            throw new IllegalAccessException("you are not owner of this ticket!");

        Page<Comment> commentPage = commentRepository.findAll(
                Specifications
                        .where(
                                CommentSpecifications.forTicket(ticket)
                        )
                        .and(
                                CommentSpecifications.notDisabled()
                        )
                ,
                pageableComments);

        model.addAttribute("ticket", ticket);

        model.addAttribute("comments", commentPage);

        model.addAttribute("currentUser", currentUser);

        PageWrapper<Comment> page = new PageWrapper<Comment>(commentPage, "/tickets/".concat(ticketId.toString()));
        model.addAttribute("page", page);

        return "tickets/ticket";

    }

    @RequestMapping(value = "/{id}", params = "add_comment", method = RequestMethod.POST)
    public String addComment(
            @PathVariable(value = "id")
            Long ticketId,
            @RequestParam(value = "text")
            String textComment,
            @RequestParam(value = "attachFile[]", required = false) List<MultipartFile> multipartFileList,
            @RequestParam(value = "fileName[]", required = false) List<String> fileNames
    ) throws NotFoundException, IllegalAccessException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        Ticket ticket = ticketRepository.findOne(ticketId);

        if (ticket == null) throw new NotFoundException("cant find ticket with id=".concat(ticketId.toString()));

        if (!ticketService.checkOwnership(ticket, currentUser))
            throw new IllegalAccessException("you are not owner of this ticket!");

        ticketService.addComment(ticket, textComment, currentUser, multipartFileList, fileNames);

        return "redirect:/tickets/".concat(ticketId.toString());
    }

    @RequestMapping(value = "/{id}", params = "done", method = RequestMethod.GET)
    public String done(
            @PathVariable(value = "id")
            Long ticketId
    ) throws NotFoundException, IllegalAccessException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        Ticket ticket = ticketRepository.findOne(ticketId);

        if (ticket == null) throw new NotFoundException("cant find ticket with id=".concat(ticketId.toString()));

        if (!ticketService.checkOwnership(ticket, currentUser))
            throw new IllegalAccessException("you are not owner of this ticket!");

        ticketService.done(ticket);

        return "redirect:/tickets";
    }

    @RequestMapping(value = "/{id}", params = "close", method = RequestMethod.GET)
    public String close(
            @PathVariable(value = "id")
            Long ticketId
    ) throws NotFoundException, IllegalAccessException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        Ticket ticket = ticketRepository.findOne(ticketId);

        if (ticket == null) throw new NotFoundException("cant find ticket with id=".concat(ticketId.toString()));

        if (!ticketService.checkOwnership(ticket, currentUser) || userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN))
            throw new IllegalAccessException("you are not owner of this ticket!");

        ticketService.close(ticket);

        return "redirect:/tickets/";
    }

}
