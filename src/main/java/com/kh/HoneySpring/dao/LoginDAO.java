package com.kh.HoneySpring.dao;

import com.kh.HoneySpring.vo.UsersVO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class LoginDAO {
    private final JdbcTemplate jdbcTemplate;

    public LoginDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UsersVO FindByUserID(String userID) {
        // 아이디를 이용해 유저를 찾는 쿼리문
        String sql = "SELECT * FROM USERS WHERE USERID = ?";

        // RowMapper 로 생성자를 통해 ID, PW, UserNickName 을 입력
        RowMapper<UsersVO> rowMapper = new RowMapper<UsersVO>() {
            @Override
            public UsersVO mapRow(ResultSet rs, int rowNum) throws SQLException {
                UsersVO usersVO = new UsersVO();
                usersVO.setUserID(rs.getString("USERID"));
                usersVO.setUserPW(rs.getString("USERPW"));
                usersVO.setNName(rs.getString("NNAME"));
                return usersVO;
            }
        };
        // 위 쿼리 RowMapper, UserID 찾기 실패시 예외처리
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, userID);
        } catch (EmptyResultDataAccessException e) {    // Try Catch 구문 추가
            return null;    // 실패시 Null 로 반환
        }
    }
}

