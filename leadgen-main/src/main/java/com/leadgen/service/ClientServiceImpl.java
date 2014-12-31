package com.leadgen.service;

import com.leadgen.dmodel.Client;
import com.leadgen.dmodel.OrderSource;
import com.leadgen.dmodel.User;
import com.leadgen.dmodel.UserRole;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.repository.ClientRepository;
import com.leadgen.repository.UserRepository;
import com.leadgen.repository.OrderSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 07.11.14.
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderSourceRepository orderSourceRepository;

    private void validateClient(Client client) throws WrongInputDataException {
        if(client.getName() == null || client.getName().equals("")) throw new WrongInputDataException("Name field is required for new client!", WrongInputDataException.Reason.NAME_FIELD);
    }

    @Override
    public void createClient(Client client) throws WrongInputDataException {

        validateClient(client);

        client.setDtmCreate(new Date());
        client.setDtmUpdate(new Date());

        client.setMoney(BigDecimal.ZERO);

        clientRepository.save(client);
    }

    @Override
    public void updateClient(Long id, Client client) throws WrongInputDataException {
        assert( id == client.getId());

        validateClient(client);

        Client old = clientRepository.findOne(id);

        if(old != null){
            old.setName(client.getName());
            old.setDescription(client.getDescription());
            old.setDtmUpdate(new Date());

            clientRepository.save(old);
        }
    }

    @Override
    public User getAdminUser(Client client) {
        List<User> userList = userRepository.findByClientHasRole(client, UserRole.Role.CLIENT);

        if(userList.size() > 0) return userList.get(0);
        else return null;
    }

    @Override
    public void addOrderSourceToClient(Client client, OrderSource orderSource) {
        if(orderSource != null && client != null){
            List<OrderSource> orderSources = client.getSourceList();
            orderSources.add(orderSource);
            client.setSourceList(orderSources);

            clientRepository.save(client);
        }
    }

    @Override
    public List<OrderSource> findSourcesNotExistsInClientSourcesList(Client client) {
        List<OrderSource> sourceList = client.getSourceList();

        if(sourceList != null && sourceList.size() > 0){
            return orderSourceRepository.findSourcesNotExistsInSourcesList(sourceList);
        }
        else return (List<OrderSource>) orderSourceRepository.findAll();
    }

    @Override
    public void delOrderSourceFromClient(Client client, OrderSource orderSource) {
        if(orderSource != null && client != null){
            List<OrderSource> orderSources = client.getSourceList();
            if(orderSources.contains(orderSource)){
                orderSources.remove(orderSource);
            }
            client.setSourceList(orderSources);

            clientRepository.save(client);
        }
    }
}
