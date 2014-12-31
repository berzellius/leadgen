package com.leadgen.enumerated;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 19.11.14.
 */
public enum Status {

    AVAILABLE(false),
    PROCESSING(true),
    PROCESSING_CLIENT(true),
    CLIENT_DECLINED(true),
    CLIENT_CONFIRMED(true),
    DONE(false),
    CANCELLED(false);

    Status(boolean userDefined) {
        this.userDefined = userDefined;
    }

    private boolean userDefined;

    public boolean isUserDefined(){
        return this.userDefined;
    }

    public static List<Status> getUserDefinedStatuses(){
        List<Status> statuses = new LinkedList();
        for(Status st : Status.values()){
            if(st.userDefined) statuses.add(st);
        }

        return statuses;
    }
}
