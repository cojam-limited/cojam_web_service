package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.constant.SequenceCode;
import io.cojam.web.dao.SeasonDao;
import io.cojam.web.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SeasonService {

    @Autowired
    SeasonDao seasonDao;

    @Autowired
    SequenceService sequenceService;

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

    @Transactional
    public ResponseDataDTO saveSeason(SeasonInput season, HttpServletRequest request, Account account){
        ResponseDataDTO response = new ResponseDataDTO();
        season.setSeasonKey(sequenceService.getSequence(SequenceCode.TB_SEASON));
        season.setIsActive(false);
        seasonDao.saveSeason(season);

        List<SeasonCategory> categoryList = seasonDao.getSeasonCategoryList();
        if(categoryList != null){
            for (SeasonCategory category : categoryList) {
                category.setSeasonKey(season.getSeasonKey());
                category.setCreateMemberKey(account.getMemberKey());
                category.setUpdateMemberKey(account.getMemberKey());
                String limitation = request.getParameter(category.getCategoryKey());
                if(StringUtils.isBlank(limitation)){
                    limitation  = "100";
                }
                category.setLimitation(limitation);
                seasonDao.saveSeasonCategoryMapping(category);

            }
        }

        response.setCheck(true);
        response.setMessage("success");
        return response;
    }


    @Transactional
    public ResponseDataDTO updateSeason(SeasonInput season,HttpServletRequest request, Account account){
        ResponseDataDTO response = new ResponseDataDTO();

        if(StringUtils.isBlank(season.getSeasonKey())){
            response.setCheck(false);
            response.setMessage("Wrong parameter.");
            return response;
        }

        Season detail = seasonDao.getSeasonDetail(season.getSeasonKey());

        if(detail == null){
            response.setCheck(false);
            response.setMessage("No season data.");
            return response;
        }

        seasonDao.updateSeason(season);

        List<SeasonCategory> categoryList = seasonDao.getSeasonCategoryList();
        if(categoryList != null){
            for (SeasonCategory category : categoryList) {
                category.setSeasonKey(season.getSeasonKey());
                category.setCreateMemberKey(account.getMemberKey());
                category.setUpdateMemberKey(account.getMemberKey());
                String limitation = request.getParameter(category.getCategoryKey());
                if(StringUtils.isBlank(limitation)){
                    limitation  = "100";
                }
                category.setLimitation(limitation);
                SeasonCategory mappingDetail = seasonDao.getSeasonCategoryMapping(category);
                if(mappingDetail==null){
                    seasonDao.saveSeasonCategoryMapping(category);
                }else{
                    if(!mappingDetail.getLimitation().equals(limitation)){
                        seasonDao.updateSeasonCategoryMapping(category);
                    }
                }
            }
        }



        response.setCheck(true);
        response.setMessage("success");
        return response;
    }

    public Season getSeasonDetail(String seasonKey){
        return seasonDao.getSeasonDetail(seasonKey);
    }

    public List<SeasonCategory> getSeasonCategoryMappingList(String seasonKey){
        return seasonDao.getSeasonCategoryMappingList(seasonKey);
    }
}
