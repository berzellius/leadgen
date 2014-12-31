package com.leadgen.exceptions;

/**
 * Created by berz on 14.11.14.
 */
public class TakingOrderImpossibleException extends Exception {

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public enum Reason{
        ACCESS_ERROR, ALREADY_PROCESSING, ORDER_ALREADY_TAKEN
    }

    private Reason reason;

    public TakingOrderImpossibleException(){

    }

    public TakingOrderImpossibleException(String s){
        super(s);
    }

    public TakingOrderImpossibleException(String s, Reason reason){
        super(s);
        this.setReason(reason);
    }

}
