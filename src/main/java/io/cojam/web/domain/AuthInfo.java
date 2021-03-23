package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AuthInfo {
    List<AuthMap> authMapList;

    public AuthInfo() {
        List<AuthMap> authMapList = new ArrayList<>();
        AuthMap authMap = new AuthMap();
        authMap.setAuthCode("USER");
        authMap.setAuthName("Customer");
        authMapList.add(authMap);
        authMap = new AuthMap();
        authMap.setAuthCode("ADMIN");
        authMap.setAuthName("Admin");
        authMapList.add(authMap);
        authMap = new AuthMap();
        authMap.setAuthCode("VIP");
        authMap.setAuthName("VIP");
        authMapList.add(authMap);
        this.authMapList = authMapList;
    }
}
