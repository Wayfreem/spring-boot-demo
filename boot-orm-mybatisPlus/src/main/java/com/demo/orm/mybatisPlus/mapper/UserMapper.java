package com.demo.orm.mybatisPlus.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.orm.mybatisPlus.model.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    // 使用自定义的查询时，不会自动加入 delete = 0 逻辑删除的语句
    @Select("select * from User ${ew.customSqlSegment}")
    List<User> selectCustomSql(@Param(Constants.WRAPPER) Wrapper<User> wrapper);


    List<User> selectCustomSqlByXML(@Param(Constants.WRAPPER) Wrapper<User> wrapper);

    IPage<User> selectCustomPage(Page<User> page, @Param(Constants.WRAPPER) Wrapper<User> wrapper);
}
