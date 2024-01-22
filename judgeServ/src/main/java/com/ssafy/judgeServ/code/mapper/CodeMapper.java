package com.ssafy.judgeServ.code.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.sql.SQLException;
import java.util.HashMap;

@Mapper
public interface CodeMapper {
    HashMap sumbit(String code) throws SQLException;

}
