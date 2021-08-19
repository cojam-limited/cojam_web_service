package io.cojam.web.dao;

import io.cojam.web.domain.DeviceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface DeviceDao {

    int saveDeviceInfo(DeviceInfo deviceInfo);

    int deleteDeviceInfo(DeviceInfo deviceInfo);

    int deleteDeviceInfoMemberKey(DeviceInfo deviceInfo);
}
