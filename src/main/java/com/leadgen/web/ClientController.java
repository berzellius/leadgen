package com.leadgen.web;

import com.leadgen.dmodel.Client;
import com.leadgen.dmodel.OrderSource;
import com.leadgen.dmodel.User;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.repository.ClientRepository;
import com.leadgen.repository.OrderSourceRepository;
import com.leadgen.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Created by berz on 07.11.14.
 */
@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    OrderSourceRepository orderSourceRepository;



    @RequestMapping(method = RequestMethod.GET)
    public String clients(Model model){

        List<Client> clients = (List<Client>) clientRepository.findAll();
        model.addAttribute("clients", clients);
        model.addAttribute("hello", "null");

        return "clients/all";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newClientForm(
            Model model,
            @RequestParam(value = "reason", required = false)
            WrongInputDataException.Reason reason,
            @RequestParam(value = "client", required = false)
            Client client
    ){

        if(reason != null) model.addAttribute("reason", reason);

        return "clients/new";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addClient(
            Client client,
            Model model,
            final RedirectAttributes redirectAttributes
    ){

        try {
            clientService.createClient(client);
        } catch (WrongInputDataException e) {
            //model.addAttribute("reason", e.getReason());
            //model.addAttribute("client", client);
            redirectAttributes.addFlashAttribute("client" ,client);
            redirectAttributes.addAttribute("reason", e.getReason());

            return "redirect:/clients/new";
        }

        return "redirect:/clients";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String editClient(
            Model model,
            @PathVariable(value = "id")
            Long id,
            @RequestParam(value = "reason", required = false)
            WrongInputDataException.Reason reason
    ){
        Client client = clientRepository.findOne(id);

        User adminUser = clientService.getAdminUser(client);

        if(client == null) return "redirect:/clients";

        if(reason != null) model.addAttribute("reason", reason);

        List<OrderSource> orderSourcesAvailable = clientService.findSourcesNotExistsInClientSourcesList(client);

        model.addAttribute("sources_available", (orderSourcesAvailable.size() > 0)? orderSourcesAvailable : null);

        model.addAttribute("client", client);

        model.addAttribute("adminUser", adminUser);

        return "clients/edit";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String updateClient(
            Model model,
            final RedirectAttributes redirectAttributes,
            @PathVariable(value = "id")
            Long id,
            Client client
    ){

        try {
            clientService.updateClient(id, client);
        } catch (WrongInputDataException e) {
            redirectAttributes.addAttribute("reason", e.getReason());
        }

        return "redirect:/clients/".concat(id.toString());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, params = "del_source")
    public String delOrderSourceFromClient(
        Model model,
        final RedirectAttributes redirectAttributes,
        @PathVariable(value = "id")
        Long id,
        @RequestParam(value = "source_id")
        Long orderSourceId
    ){
        Client client = clientRepository.findOne(id);
        OrderSource orderSource = orderSourceRepository.findOne(orderSourceId);

        clientService.delOrderSourceFromClient(client, orderSource);

        return "redirect:/clients/".concat(id.toString());
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = "add_source")
    public String addOrderSourceToClient(
            Model model,
            final RedirectAttributes redirectAttributes,
            @PathVariable(value = "id")
            Long id,
            @RequestParam(value = "source_id")
            Long orderSourceId
    ){
        Client client = clientRepository.findOne(id);
        OrderSource orderSource = orderSourceRepository.findOne(orderSourceId);

        clientService.addOrderSourceToClient(client, orderSource);

        return "redirect:/clients/".concat(id.toString());
    }

}
