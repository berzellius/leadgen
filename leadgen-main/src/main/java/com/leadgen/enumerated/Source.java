package com.leadgen.enumerated;

/**
 * Created by berz on 20.10.14.
 */
public enum Source {
    INTERNET,
    TWITTER,
    FACEBOOK;

    public static Source valueOfSource(String sourceName){
        try{
            return Source.valueOf(sourceName);
        }
        catch (RuntimeException e){
            return null;
        }
    }
}
