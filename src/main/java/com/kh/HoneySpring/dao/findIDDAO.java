package com.kh.HoneySpring.dao;

import com.kh.HoneySpring.Common.Common;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class findIDDAO {
    private final JdbcTemplate jdbcTemplate;

    // JdbcTemplate을 주입받기 위한 생성자
    public findIDDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 전화번호로 아이디를 찾는 메서드
    public String findID(String phone) {
        // SQL 쿼리 작성
        String sql = "SELECT userID FROM USERS WHERE phone = ?";

        // 데이터 조회 및 반환
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{phone}, String.class);
        } catch (Exception e) {
            // 조회 실패 시 null 반환
            return null;
        }
    }
}
