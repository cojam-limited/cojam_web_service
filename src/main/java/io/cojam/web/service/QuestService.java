package io.cojam.web.service;

import io.cojam.web.dao.MarketDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestService {

    @Autowired
    MarketDao marketDao;


}
