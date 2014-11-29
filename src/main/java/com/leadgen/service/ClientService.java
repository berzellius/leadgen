package com.leadgen.service;

import com.leadgen.dmodel.Client;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.dmodel.OrderSource;
import com.leadgen.dmodel.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by berz on 07.11.14.
 */
@Service
public interface ClientService {

    public void createClient(Client client) throws WrongInputDataException;

    void updateClient(Long id, Client client) throws WrongInputDataException;

    User getAdminUser(Client client);

    void addOrderSourceToClient(Client client, OrderSource orderSource);

    List<OrderSource> findSourcesNotExistsInClientSourcesList(Client client);

    void delOrderSourceFromClient(Client client, OrderSource orderSource);
}
