package com.leadgen.web;

import com.leadgen.exceptions.IllegalAuthKeyException;
import com.leadgen.exceptions.OrderCreateException;
import com.leadgen.json.AddOrder;
import com.leadgen.json.OrderFromSource;
import com.leadgen.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by berz on 26.10.14.
 */
@RestController
@RequestMapping("/rest/order")
public class OrderRestController {

    @Autowired
    OrderService orderService;


    @RequestMapping(
            value = "add",
            method = RequestMethod.POST/*,
            headers="Accept=application/json"*/ // пока что тестируем так
    )
    public AddOrder addOrder(
            @RequestParam(value = "auth_key")
            String authKey,
            OrderFromSource info,
            @RequestParam(value = "source", required = true)
            String source,
            Model model
    ) {
        AddOrder addOrder = new AddOrder();



        try {
            orderService.insertOrder(authKey, info, source);
            addOrder.success = true;
        }
        catch (IllegalAuthKeyException e) {
            addOrder.success = false;
            addOrder.message = e.getMessage();
        }
        catch (RuntimeException e){
            addOrder.success = false;
            addOrder.message = "Adding failed: ".concat(e.getMessage());
            e.printStackTrace();
        } catch (OrderCreateException e) {
            addOrder.success = false;
            addOrder.message = e.getMessage();
        }

        return addOrder;
    }
}
