package com.orchid.examples.repeatedinsert.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orchid.examples.repeatedinsert.model.Test;
import org.apache.ibatis.annotations.Param;

public interface TestDao extends BaseMapper<Test> {

    int insertTest(@Param("test") Test test);
}
