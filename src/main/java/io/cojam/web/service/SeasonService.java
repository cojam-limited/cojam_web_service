package io.cojam.web.service;

import io.cojam.web.dao.SeasonDao;
import io.cojam.web.domain.Season;
import io.cojam.web.domain.SeasonCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonService {

    @Autowired
    SeasonDao seasonDao;

    public Season getSeasonInfo(){
        return seasonDao.getSeasonInfo();
    }

    public List<SeasonCategory> getSeasonCategoryList(){
        return seasonDao.getSeasonCategoryList();
    }

    public List<Season> getSeasonList(Season season){
        return seasonDao.getSeasonList(season);
    }

    public List<SeasonCategory> getSeasonCategoryCntList(String seasonKey){
        return seasonDao.getSeasonCategoryCntList(seasonKey);
    }
}
