package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.dao.DeviceDao;
import io.cojam.web.domain.DeviceInfo;
import io.cojam.web.domain.ResponseDataDTO;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class DeviceService {

    @Autowired
    DeviceDao deviceDao;

    int saveDeviceInfo(DeviceInfo deviceInfo){
        return deviceDao.saveDeviceInfo(deviceInfo);
    }

    int deleteDeviceInfo(DeviceInfo deviceInfo){
        return deviceDao.deleteDeviceInfo(deviceInfo);
    }

    int deleteDeviceInfoMemberKey(DeviceInfo deviceInfo){
        return deviceDao.deleteDeviceInfoMemberKey(deviceInfo);
    }

    @Transactional
    public ResponseDataDTO saveDeviceProc(Account account, DeviceInfo deviceInfo){
        ResponseDataDTO response = new ResponseDataDTO();

        if(StringUtils.isBlank(deviceInfo.getToken())){
            response.setCheck(false);
            response.setMessage("no token!");
            return response;
        }

        if(StringUtils.isBlank(deviceInfo.getDeviceType())){
            response.setCheck(false);
            response.setMessage("no device type!");
            return response;
        }

        if(!StringUtils.isBlank(deviceInfo.getMemberKey())){
            this.deleteDeviceInfoMemberKey(deviceInfo);
        }

        this.deleteDeviceInfo(deviceInfo);
        this.saveDeviceInfo(deviceInfo);

        response.setCheck(true);
        response.setMessage("SUCCESS");
        return response;
    }
}
