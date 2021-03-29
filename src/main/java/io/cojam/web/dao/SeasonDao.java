package io.cojam.web.dao;

import io.cojam.web.domain.Season;
import io.cojam.web.domain.SeasonCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SeasonDao {

    Season getSeasonInfo();

    List<SeasonCategory> getSeasonCategoryList();

    List<Season> getSeasonList(Season season);

    List<SeasonCategory> getSeasonCategoryCntList(String seasonKey);
}
