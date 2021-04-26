package io.cojam.web.dao;

import io.cojam.web.domain.Season;
import io.cojam.web.domain.SeasonCategory;
import io.cojam.web.domain.SeasonInput;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SeasonDao {


    /**
     * 활성 시즌 정보
     * @return
     */
    Season getSeasonInfo();

    List<SeasonCategory> getSeasonCategoryList();

    List<Season> getSeasonList(Season season);

    List<SeasonCategory> getSeasonCategoryCntList(String seasonKey);

    int saveSeason(SeasonInput season);

    int updateSeason(SeasonInput season);

    Season getSeasonDetail(String seasonKey);

    int saveSeasonCategoryMapping(SeasonCategory category);

    int updateSeasonCategoryMapping(SeasonCategory category);

    SeasonCategory getSeasonCategoryMapping(SeasonCategory category);

    List<SeasonCategory> getSeasonCategoryMappingList(String seasonKey);
}
