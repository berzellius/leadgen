package com.leadgen.json;

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.util.LinkedList;
import java.util.List;
import java.beans.Introspector;

/**
 * Created by berz on 16.11.14.
 */
public class OrderFromSource {

    public OrderFromSource(){

    }

    private String phone;
    private String name;
    private String email;

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String toString(){
        return this.getPhone();
    }



    public List<String> keys(){
        List<String> keys = new LinkedList<String>();

        try {
            for(PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors()){
                if(propertyDescriptor.getPropertyType().equals(String.class))
                    keys.add(propertyDescriptor.getName());
            }
        } catch (IntrospectionException e) {
            //e.printStackTrace();
        }

        return keys;
    }

    public List<String> restrictedKeys(){
        List<String> keys = new LinkedList<String>();

        keys.add("phone");
        keys.add("email");

        return keys;
    }
}