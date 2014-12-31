package com.leadgen.web;

import com.leadgen.dmodel.OrderSource;
import com.leadgen.dmodel.OrderSourcePrices;
import com.leadgen.dmodel.SourceUTM;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.repository.OrderSourcePricesRepository;
import com.leadgen.repository.OrderSourceRepository;
import com.leadgen.repository.SourceUTMRepository;
import com.leadgen.service.OrderSourceService;
import com.leadgen.specifications.SourceUTMSpecifications;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by berz on 12.11.14.
 */
@Controller
@RequestMapping(value = "/order_src")
public class OrderSourceController {

    @Autowired
    OrderSourceRepository orderSourceRepository;

    @Autowired
    OrderSourceService orderSourceService;

    @Autowired
    SourceUTMRepository sourceUTMRepository;

    @Autowired
    OrderSourcePricesRepository orderSourcePricesRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String getOrderSources(
            Model model,
            @RequestParam(value = "reason", required = false)
            WrongInputDataException.Reason reason
    ){

        List<OrderSource> orderSources = (List<OrderSource>) orderSourceRepository.findAll();
        model.addAttribute("sources", orderSources);

        orderSourceService.calculateRates(orderSources);

        if(reason != null) model.addAttribute("reason", reason);

        return "ordersources/all";
    }

    @RequestMapping(value = "new", method = RequestMethod.POST)
    public String addOrderSource(
            Model model,
            OrderSource orderSource,
            final RedirectAttributes redirectAttributes
    ){

        try {
            orderSourceService.addOrderSource(orderSource);
        } catch (WrongInputDataException e) {
            redirectAttributes.addAttribute("reason", e.getReason());
        }

        return "redirect:/order_src";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(
            Model model,
            @PathVariable(value = "id")
            Long id,
            @RequestParam(value = "orderSourceReason", required = false)
            WrongInputDataException.Reason orderSourceReason,
            @RequestParam(value = "utmPricesReason", required = false)
            WrongInputDataException.Reason utmPricesReason
    ) throws NotFoundException {
        OrderSource orderSource = orderSourceRepository.findOne(id);

        if(orderSource == null) throw new NotFoundException("not found order source with id=".concat(id.toString()));

        List<OrderSourcePrices> orderSourcePricesList = orderSourcePricesRepository.findByOrderSource(orderSource);

        List<SourceUTM> sourceUTMList = (List<SourceUTM>) sourceUTMRepository.findAll(
                Specifications
                    .where(
                            SourceUTMSpecifications.notDeleted()
                    )
                    .and(
                            SourceUTMSpecifications.forOrderSource(orderSource)
                    )

        );





        /*System.out.println(orderSourceService.getDoneOrderCost(orderSource));
        System.out.println(orderSourceService.getOrdersConversionInDone(orderSource));
        System.out.println(orderSourceService.getOrdersConversionHasClientInDone(orderSource));
        System.out.println(orderSourceService.getProfitability(orderSource));*/
        orderSourceService.calculateRates(orderSource);

        orderSourceService.calculateRatesOrderSourcePrices(orderSourcePricesList);

        model.addAttribute("utmpossible", sourceUTMList);
        model.addAttribute("prices", orderSourcePricesList);
        model.addAttribute("orderSource", orderSource);

        if(orderSourceReason != null)
            model.addAttribute("orderSourceReason", orderSourceReason);

        if(utmPricesReason != null)
            model.addAttribute("utmPricesReason", utmPricesReason);


        return "ordersources/show";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String update(
            Model model,
            @PathVariable(value = "id")
            Long id,
            OrderSource orderSource,
            final RedirectAttributes redirectAttributes
    ){
        orderSource.setId(id);

        try {
            orderSourceService.update(orderSource);
        } catch (WrongInputDataException e) {
            redirectAttributes.addAttribute("orderSourceReason", e.getReason());
        }

        return "redirect:/order_src/".concat(id.toString());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = "add_price")
    public String addPrices(
            Model model,
            @PathVariable(value = "id")
            Long id,
            @RequestParam(value = "sourceUTM")
            Long utmId,
            @RequestParam(value = "price")
            BigDecimal price,
            @RequestParam(value = "cost")
            BigDecimal cost,
            final RedirectAttributes redirectAttributes
    ){

        try {
            orderSourceService.addOrderSourcePricesToOrderSource(id, utmId, price, cost);
        } catch (WrongInputDataException e) {
            redirectAttributes.addAttribute("utmPricesReason", e.getReason());
        }

        return "redirect:/order_src/".concat(id.toString());
    }

    @RequestMapping(value = "/{order_src_id}/price/{id}", method = RequestMethod.DELETE)
    public String deletePrice(
            @PathVariable(value = "id")
            Long pricesId,
            @PathVariable(value = "order_src_id")
            Long orderSourceId
    ){

        orderSourceService.deleteOrderSourcePrices(pricesId);

        return "redirect:/order_src/".concat(orderSourceId.toString());
    }
}
