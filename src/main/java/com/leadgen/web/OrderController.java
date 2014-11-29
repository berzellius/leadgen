package com.leadgen.web;

/**
 * Created by berz on 20.10.14.
 */

import com.leadgen.dmodel.*;
import com.leadgen.enumerated.Status;
import com.leadgen.exceptions.TakingOrderImpossibleException;
import com.leadgen.filters.OrderFilter;
import com.leadgen.repository.CommentRepository;
import com.leadgen.repository.OrderHistoryRepository;
import com.leadgen.repository.OrderRepository;
import com.leadgen.service.OrderService;
import com.leadgen.specifications.CommentSpecifications;
import com.leadgen.specifications.OrderSpecifications;
import com.leadgen.util.UserLoginUtil;
import com.leadgen.wrappers.PageWrapper;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderHistoryRepository orderHistoryRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserLoginUtil userLoginUtil;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String indexPage(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();

        return "index";
    }

    @RequestMapping("/orders")
    public String greeting(
            Model model,
            OrderFilter orderFilter,
            @PageableDefault(page = 0, value = 20)
            Pageable pageable
    ) {
        User currentUser = userLoginUtil.getCurrentLogInUser();

        Page<Order> orders = null;

        if(userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN)){
            orders = (Page<Order>) orderRepository.findAll(OrderSpecifications.matchForFilter(orderFilter), pageable);
        }

        if(userLoginUtil.userHaveRole(currentUser, UserRole.Role.CLIENT)){
            orders = (Page<Order>) orderRepository.findAll(OrderSpecifications.matchForFilter(orderFilter), pageable);
        }

        if(userLoginUtil.userHaveRole(currentUser, UserRole.Role.MANAGER)){
            Client client = currentUser.getClient();

            if(client != null)
                orders = (Page<Order>) orderRepository.findAll(OrderSpecifications.ordersAvailableForClient(client), pageable);
        }

        if(orderFilter != null) model.addAttribute("orderFilter", orderFilter);

        model.addAttribute("orders", orders);
        PageWrapper<Order> page = new PageWrapper<Order>(orders, "/orders");
        model.addAttribute("page", page);

        return "orders";
    }

    /*@RequestMapping("/add_order")
    public String addOrder(
            @RequestParam(value = "info", required = true)
            String info,
            @RequestParam(value = "source", required = true)
            SourceUTM source,
            Model model
    ){

        orderService.insertOrder(info, source);

        return "redirect:/orders";
    }*/

    @RequestMapping(value = "orders/take", method = RequestMethod.POST)
    public String takeOrder(
            @RequestParam(value = "order_id")
            Long orderId,
            final RedirectAttributes redirectAttributes
    ){

        User currentUser = userLoginUtil.getCurrentLogInUser();

        try {
            orderService.takeOrder(currentUser, orderId);
        } catch (TakingOrderImpossibleException e) {
            redirectAttributes.addAttribute("taking_reason", e.getReason());
        }

        return "redirect:/orders";
    }

    @RequestMapping(value = "orders/inprocess", method = RequestMethod.GET)
    public String getOrdersInProcess(
            Model model,
            OrderFilter orderFilter,
            @PageableDefault(page = 0, value = 20)
            Pageable pageable
    ){

        User currentUser = userLoginUtil.getCurrentLogInUser();

        if(orderFilter.getStatusList() == null || orderFilter.getStatusList().isEmpty()){
            orderFilter.setStatusList(Status.getUserDefinedStatuses());
        }

        if(currentUser.getClient() == null) throw new IllegalAccessError("this is only for clients/managers!");

        Specifications orderSpecifications =
                Specifications
                        .where(
                                OrderSpecifications.matchForFilter(orderFilter)
                        )
                        .and(
                                OrderSpecifications.isOwnedByClient(currentUser.getClient()
                                )
                        );

        Page<Order> orders = (Page<Order>) orderRepository.findAll(
                orderSpecifications,
                pageable
        );

        model.addAttribute("orders", orders);
        PageWrapper<Order> page = new PageWrapper<Order>(orders, "/orders/inprocess");
        model.addAttribute("page", page);

        return "orders/inprocess";
    }

    @RequestMapping(value = "orders/{id}", method = RequestMethod.GET)
    public String showOrder(
        @PathVariable(value = "id")
        Long id,
        Model model,
        @PageableDefault(page = 0, value = 20, sort = "dtmCreate", direction = Sort.Direction.DESC)
        Pageable pageableComments
    ) throws NotFoundException {

        User currentUser = userLoginUtil.getCurrentLogInUser();

        if(currentUser.getClient() == null) throw new IllegalAccessError("Only for users with client!");

        Order order = orderRepository.findOne(id);

        if(order == null) throw new NotFoundException("Not found order with id=".concat(id.toString()));

        if(!order.getClient().equals(currentUser.getClient()) && !userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN))
            throw new IllegalAccessError("You are not owner of the order!");

        List<OrderHistory> orderHistoryList = orderHistoryRepository.findByOrderAndClientOrderByDtmCreateDesc(order, currentUser.getClient());

        model.addAttribute("order", order);
        model.addAttribute("orderHistory", orderHistoryList);

        Page<Comment> commentPage = commentRepository.findAll(
                Specifications
                        .where(
                                CommentSpecifications.forOrder(order)
                        )
                        .and(
                                CommentSpecifications.notDisabled()
                        )
                ,
                pageableComments);

        model.addAttribute("comments", commentPage);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("statuses", Status.getUserDefinedStatuses());

        PageWrapper<Comment> page = new PageWrapper<Comment>(commentPage, "/orders/".concat(id.toString()));
        model.addAttribute("page", page);

        return "orders/show";
    }

    @RequestMapping(value = "orders/{id}", method = RequestMethod.GET, params = "done")
    public String done(
            @PathVariable(value = "id")
            Long id,
            Model model
    ) throws IllegalAccessException, NotFoundException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        Order order = orderRepository.findOne(id);

        if(
                currentUser.getClient() == null ||
                        !currentUser.getClient().equals(order.getClient())
                ) throw new IllegalAccessException("only for users with client");

        if(order == null) throw new NotFoundException("not found order with id=".concat(id.toString()));

        orderService.done(order);

        return "redirect:/orders/inprocess";
    }

    @RequestMapping(value = "orders/{id}", method = RequestMethod.GET, params = "decline")
    public String decline(
            @PathVariable(value = "id")
            Long id,
            Model model
    ) throws IllegalAccessException, NotFoundException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        Order order = orderRepository.findOne(id);

        if(
                currentUser.getClient() == null ||
                        !currentUser.getClient().equals(order.getClient())
                ) throw new IllegalAccessException("only for users with client");

        if(order == null) throw new NotFoundException("not found order with id=".concat(id.toString()));

        orderService.decline(order);

        return "redirect:/orders/inprocess";
    }

    @RequestMapping(value = "orders/{id}", method = RequestMethod.GET, params = "cancel")
    public String cancel(
            @PathVariable(value = "id")
            Long id,
            Model model
    ) throws IllegalAccessException, NotFoundException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        Order order = orderRepository.findOne(id);

        if(
                currentUser.getClient() == null ||
                        !currentUser.getClient().equals(order.getClient())
                ) throw new IllegalAccessException("only for users with client");

        if(order == null) throw new NotFoundException("not found order with id=".concat(id.toString()));

        orderService.cancelOrder(order);

        return "redirect:/orders/inprocess";
    }


    @RequestMapping(value = "orders/{id}", method = RequestMethod.POST, params = "change_status")
    public String changeStatus(
            @PathVariable(value = "id")
            Long id,
            Model model,
            @RequestParam(value = "status")
            Status status
    ) throws IllegalAccessException, NotFoundException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        Order order = orderRepository.findOne(id);

        if(
                currentUser.getClient() == null ||
                        !currentUser.getClient().equals(order.getClient())
                ) throw new IllegalAccessException("only for users with client");

        if(order == null) throw new NotFoundException("not found order with id=".concat(id.toString()));

        orderService.changeStatusByManager(order, status);

        return "redirect:/orders/".concat(id.toString());
    }

    @RequestMapping(value = "orders/{id}", method = RequestMethod.POST, params = "add_comment")
    public String addComment(
            @PathVariable(value = "id")
            Long id,
            Model model,
            @RequestParam(value = "text")
            String textComment,
            @RequestParam(value = "attachFile[]", required = false) List<MultipartFile> multipartFileList,
            @RequestParam(value = "fileName[]", required = false) List<String> fileNames
    ) throws IllegalAccessException, NotFoundException {
        User currentUser = userLoginUtil.getCurrentLogInUser();
        Order order = orderRepository.findOne(id);

        if(
                currentUser.getClient() == null ||
                        !currentUser.getClient().equals(order.getClient())
                ) throw new IllegalAccessException("only for users with client");

        if(order == null) throw new NotFoundException("not found order with id=".concat(id.toString()));

        orderService.addComment(order, textComment, currentUser, multipartFileList, fileNames);

        return "redirect:/orders/".concat(id.toString());
    }
}
