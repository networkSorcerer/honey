package com.kh.HoneySpring.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
@Repository
public class findPWDAO {
    private final JdbcTemplate jdbcTemplate;

    public findPWDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 제시문(pwLOCK)을 가져오는 메서드
    public String getPWLock(String userID) {
        String sql = "SELECT pwLOCK FROM USERS WHERE userID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userID}, String.class);
        } catch (Exception e) {
            // 아이디가 존재하지 않는 경우 null 반환
            return null;
        }
    }

    // 비밀번호를 찾는 메서드
    public String findPW(String userID, String pwKey) {
        String sql = "SELECT PWKEY, userPW FROM USERS WHERE userID = ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{userID}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String actualPwKey = rs.getString("PWKEY");
                    if (actualPwKey.equals(pwKey)) {
                        return rs.getString("userPW"); // 비밀번호 반환
                    } else {
                        return "제시어가 다릅니다.";
                    }
                }
            }).stream().findFirst().orElse("해당 아이디로 가입된 계정이 없습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return "비밀번호 찾기 실패";
        }
    }
}