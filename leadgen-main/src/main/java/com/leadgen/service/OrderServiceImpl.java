package com.leadgen.service;

import com.leadgen.enumerated.Status;
import com.leadgen.exceptions.IllegalAuthKeyException;
import com.leadgen.exceptions.OrderCreateException;
import com.leadgen.exceptions.TakingOrderImpossibleException;
import com.leadgen.json.OrderFromSource;
import com.leadgen.dmodel.*;
import com.leadgen.repository.*;
import com.leadgen.util.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by berz on 20.10.14.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderSourceRepository orderSourceRepository;

    @Autowired
    OrderHistoryRepository orderHistoryRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    SourceUTMRepository sourceUTMRepository;

    @Autowired
    UTMService utmService;

    @Autowired
    OrderSourceService orderSourceService;



    @Override
    public void insertOrder(String authKey, OrderFromSource info, String source) throws IllegalAuthKeyException, OrderCreateException {

        List<SourceUTM> sourceUTMList = sourceUTMRepository.findByCode(source);
        SourceUTM sourceUTM = null;

        if(sourceUTMList.isEmpty()){
            sourceUTM = utmService.getDefaultUTM();

            if(sourceUTM == null) throw new OrderCreateException("Cant find default UTM source!");
        }
        else{
            sourceUTM = sourceUTMList.get(0);
        }

        List<OrderSource> orderSources = orderSourceRepository.findByAuthKey(authKey);
        if (orderSources == null || orderSources.isEmpty())
            throw new IllegalAuthKeyException("no sources with authKey = ".concat(authKey).concat(" registered"));


        OrderSource orderSource = orderSources.get(0);

        BigDecimal cost = orderSourceService.getCostForOrder(orderSource, sourceUTM);
        BigDecimal price = orderSourceService.getPriceForOrder(orderSource, sourceUTM);

        Order order = new Order();
        order.setOrderSource(orderSource);
        order.setInfoJson(info);
        order.setStatus(Status.AVAILABLE);
        order.setDtmCreate(new Date());
        order.setSourceUTM(sourceUTM);
        order.setCost(cost);
        order.setPrice(price);

        entityManager.persist(order);
    }

    @Override
    public String getSomeDataToHeader() {
        return "my header data from service";
    }

    @Override
    public List<Order> getAllOrders() {
        //return entityManager.createQuery("SELECT o FROM Order o", Order.class).getResultList();
        //return (List<Order>) orderRepository.findAll();
        return orderRepository.findByStatus(Status.AVAILABLE);
    }

    @Override
    public Order getOrderById(Long l) {
        return entityManager.find(Order.class, l);
    }

    @Override
    public void takeOrder(User currentUser, Long orderId) throws TakingOrderImpossibleException {
        if (currentUser.getClient() == null) {
            throw new TakingOrderImpossibleException("you have not role MANAGER or CLIENT", TakingOrderImpossibleException.Reason.ACCESS_ERROR);
        }


        Map<String, Object> params = new HashMap<String, Object>();
        params.put("javax.persistence.lock.timeout", 0);// update nowait

        Order order = null;

        try {
            order = entityManager.find(Order.class, orderId, LockModeType.PESSIMISTIC_READ);
            //order = entityManager.find(Order.class, orderId);
        }
        catch (javax.persistence.LockTimeoutException e){
            throw new TakingOrderImpossibleException("order already processing", TakingOrderImpossibleException.Reason.ALREADY_PROCESSING);
        }

        // Не откатились на update nowait, значит, дальше можем обрабатывать заказ
        pullInHistory(order);

        order.setClient(currentUser.getClient());
        order.setStatus(Status.PROCESSING);
        order.setDtmUpdate(new Date());

        orderRepository.save(order);
    }

    @Override
    public void addComment(Order order, String textComment, User currentUser, List<MultipartFile> multipartFileList, List<String> fileNames) {

        Comment comment = new Comment();
        comment.setDisabled(false);
        comment.setOrder(order);
        comment.setText(textComment);
        comment.setUser(currentUser);
        comment.setDtmCreate(new Date());

        commentService.attachFilesToComment(comment, multipartFileList, fileNames);

        commentRepository.save(comment);

    }

    @Override
    public void changeStatusByManager(Order order, Status status) {
        if(!status.isUserDefined()) return;
        if(!order.getStatus().isUserDefined()) return;

        pullInHistory(order);

        order.setStatus(status);
        order.setDtmUpdate(new Date());

        orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Order order) {

        if(order.getStatus().equals(Status.DONE)) return;


        // В историю
        pullInHistory(order);

        // Сбрасываем клиента
        order.setClient(null);

        // В список доступных для выбора
        order.setStatus(Status.AVAILABLE);

        // Очищаем комментарии
        commentService.eraseCommentsForOrder(order);
    }

    @Override
    public void done(Order order) {
        if(order.getStatus().equals(Status.CANCELLED)) return;

        pullInHistory(order);

        order.setStatus(Status.DONE);
        order.setDtmUpdate(new Date());
    }

    @Override
    public void decline(Order order) {
        if(!order.getStatus().equals(Status.CLIENT_DECLINED)) return;

        pullInHistory(order);

        order.setStatus(Status.CANCELLED);
        order.setDtmUpdate(new Date());
    }

    private void pullInHistory(Order order) {
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrder(order);
        orderHistory.setStatus(order.getStatus());
        orderHistory.setDtmCreate(new Date());

        if(order.getDtmUpdate() != null) orderHistory.setDtmUpdate(order.getDtmUpdate());
        else orderHistory.setDtmUpdate(order.getDtmCreate());

        if(order.getClient() != null) orderHistory.setClient(order.getClient());

        orderHistoryRepository.save(orderHistory);
    }
}
