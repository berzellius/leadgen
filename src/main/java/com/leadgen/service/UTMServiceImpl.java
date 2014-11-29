package com.leadgen.service;

import com.leadgen.dmodel.SourceUTM;
import com.leadgen.exceptions.WrongInputDataException;
import com.leadgen.repository.SourceUTMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 27.11.14.
 */
@Service
@Transactional
public class UTMServiceImpl implements UTMService {

    @Autowired
    SourceUTMRepository sourceUTMRepository;


    @Override
    public void addSourceUTM(SourceUTM sourceUTM) throws WrongInputDataException {
        if(sourceUTM.getCode() == null || sourceUTM.getCode().equals(""))
            throw new WrongInputDataException("fill code field for new utm source!", WrongInputDataException.Reason.CODE_FIELD);

        if(sourceUTM.getName() == null || sourceUTM.getName().equals(""))
            throw new WrongInputDataException("fill name field for new utm source!", WrongInputDataException.Reason.NAME_FIELD);

        if(! sourceUTMRepository.findByCode(sourceUTM.getCode()).isEmpty())
            throw new WrongInputDataException("code is already used", WrongInputDataException.Reason.UNIQUE);

        if(! sourceUTMRepository.findByName(sourceUTM.getName()).isEmpty())
            throw new WrongInputDataException("name is already used", WrongInputDataException.Reason.UNIQUE);

        sourceUTM.setDeleted(false);
        sourceUTMRepository.save(sourceUTM);
    }

    @Override
    public void deleteSourceUTM(Long sourceUTMId) {
        SourceUTM sourceUTM = sourceUTMRepository.findOne(sourceUTMId);
        if(sourceUTM != null){
            sourceUTM.setDeleted(true);
        }
    }

    @Override
    public SourceUTM getDefaultUTM() {
        return sourceUTMRepository.findOne(1l);
    }
}
