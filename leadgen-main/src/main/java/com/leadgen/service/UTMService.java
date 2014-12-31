package com.leadgen.service;

import com.leadgen.dmodel.SourceUTM;
import com.leadgen.exceptions.WrongInputDataException;
import org.springframework.stereotype.Service;

/**
 * Created by berz on 27.11.14.
 */
@Service
public interface UTMService {
    void addSourceUTM(SourceUTM sourceUTM) throws WrongInputDataException;

    void deleteSourceUTM(Long sourceUTMId);

    SourceUTM getDefaultUTM();

}
