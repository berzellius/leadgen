package com.leadgen.filters;

import com.leadgen.dmodel.Order;
import com.leadgen.enumerated.Status;

import java.util.List;
/**
 * Created by berz on 15.11.14.
 */
public class OrderFilter extends Order {

    private List<Status> statusList;

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }
}
