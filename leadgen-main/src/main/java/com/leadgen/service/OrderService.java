package com.leadgen.service;

import com.leadgen.dmodel.Order;
import com.leadgen.dmodel.User;
import com.leadgen.enumerated.Status;
import com.leadgen.exceptions.IllegalAuthKeyException;
import com.leadgen.exceptions.OrderCreateException;
import com.leadgen.exceptions.TakingOrderImpossibleException;
import com.leadgen.json.OrderFromSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by berz on 20.10.14.
 */
@Service
@Transactional
public interface OrderService {




    void insertOrder(String authKey, OrderFromSource info, String source) throws IllegalAuthKeyException, OrderCreateException;

    public String getSomeDataToHeader();

    List<Order> getAllOrders();

    Order getOrderById(Long l);

    void takeOrder(User currentUser, Long orderId) throws TakingOrderImpossibleException;

    void addComment(Order order, String textComment, User currentUser, List<MultipartFile> multipartFileList, List<String> fileNames);

    void changeStatusByManager(Order order, Status status);

    void cancelOrder(Order order);

    void done(Order order);

    void decline(Order order);
}
