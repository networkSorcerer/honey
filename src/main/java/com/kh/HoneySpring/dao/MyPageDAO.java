package com.kh.HoneySpring.dao;

import com.kh.HoneySpring.vo.UsersVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;

@Repository
public class MyPageDAO {
    private final JdbcTemplate jdbcTemplate;

    public MyPageDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UsersVO> usersSelect() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, new UsersRowMapper());
    }

    public boolean usersUpdate(UsersVO vo) { // 업데이트는 반환값이 필요 없으므로 void 사용
        String sql = "UPDATE USERS SET USERPW = ?, NNAME=?, PHONE=?, PWLOCK=?, PWKey=? WHERE USERID = ?";
        int isUpdate = 0;
        try{
        isUpdate = jdbcTemplate.update(sql,
                vo.getUserPW(),
                vo.getNName(),
                vo.getPhone(),
                vo.getPwLOCK(),
                vo.getPwKey(),
                vo.getUserID());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return isUpdate > 0;
    }

    public UsersVO findUserById(String userId) {
        String sql = "SELECT * FROM USERS WHERE USERID = ?";
        List<UsersVO> vo = jdbcTemplate.query(sql, new Object[]{userId}, new UsersRowMapper());
        return vo.isEmpty() ? null : vo.get(0);
    }

    public boolean checkPassword(String userID, String password) {
        // 사용자 정보를 데이터베이스에서 조회
        UsersVO user = findUserById(userID);

        // 사용자가 존재하지 않으면 false 반환
        if (user == null) {
            return false;
        }
        return user.getUserPW().equals(password);
    }



    private static class UsersRowMapper implements RowMapper<UsersVO> {
        @Override
        public UsersVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UsersVO(
                    rs.getString("USERID"),
                    rs.getString("USERPW"),
                    rs.getString("NNAME"),
                    rs.getString("PHONE"),
                    rs.getDate("UDATE"),
                    rs.getString("PWLOCK"),
                    rs.getString("PWKEY")
            );
        }
    }



    public void usersSelectResult(List<UsersVO> list) {
        System.out.println("----------------------------------------------");
        System.out.println("             회원 정보");
        System.out.println("----------------------------------------------");
        for (UsersVO e : list) {
            System.out.print(e.getUserID() + " ");
            System.out.print(e.getUserPW() + " ");
            System.out.print(e.getNName() + " ");
            System.out.print(e.getPhone() + " ");
            System.out.print(e.getUpdateDATE() + " ");
            System.out.print(e.getPwLOCK() + " ");
            System.out.print(e.getPwKey() + " ");
            System.out.println();
        }
        System.out.println("----------------------------------------------");
    }
}