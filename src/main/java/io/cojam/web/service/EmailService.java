package io.cojam.web.service;

import io.cojam.web.dao.EmailDao;
import io.cojam.web.domain.Email;
import io.cojam.web.domain.ResponseDataDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    EmailDao emailDao;

    public List<Email> getEnableEmailList(Email email){
        return emailDao.getEnableEmailList(email);
    }

    public Integer getEnableEmailListCnt(Email email){
        return emailDao.getEnableEmailListCnt(email);
    }

    public int updateEnableEmail(Email email){
        return emailDao.updateEnableEmail(email);
    }

    public int deleteEnableEmail(Email email){
        return emailDao.deleteEnableEmail(email);
    }

    public int saveEnableEmail(Email email){
        return emailDao.saveEnableEmail(email);
    }

    public Email getEnableEmailInfo(Email email){
        return emailDao.getEnableEmailInfo(email);
    }

    @Transactional
    public ResponseDataDTO saveEmail(Email email){
        ResponseDataDTO response = new ResponseDataDTO();

        if(StringUtils.isBlank(email.getEmailName())){
            response.setCheck(false);
            response.setMessage("Enter a email name");
            return response;
        }

        email.setEmailName(email.getEmailName().trim());

        if(this.getEnableEmailInfo(email)!=null){
            response.setCheck(false);
            response.setMessage("This is already registered information.");
            return response;
        }

        this.saveEnableEmail(email);

        response.setCheck(true);
        response.setMessage("Success");

        return response;
    }


    @Transactional
    public ResponseDataDTO deleteEmail(Email email){
        ResponseDataDTO response = new ResponseDataDTO();

        if(StringUtils.isBlank(email.getEmailName())){
            response.setCheck(false);
            response.setMessage("Enter a email name");
            return response;
        }

        email.setEmailName(email.getEmailName().trim());

        this.deleteEnableEmail(email);

        response.setCheck(true);
        response.setMessage("Success");

        return response;
    }


    @Transactional
    public ResponseDataDTO updateEmail(Email email){
        ResponseDataDTO response = new ResponseDataDTO();

        if(StringUtils.isBlank(email.getEmailName())){
            response.setCheck(false);
            response.setMessage("Enter a email name");
            return response;
        }

        email.setEmailName(email.getEmailName().trim());

        this.updateEnableEmail(email);

        response.setCheck(true);
        response.setMessage("Success");

        return response;
    }
}
