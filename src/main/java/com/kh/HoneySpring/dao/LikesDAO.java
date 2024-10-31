package com.kh.HoneySpring.dao;

import com.kh.HoneySpring.vo.LikesVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LikesDAO {
    private final JdbcTemplate jdbcTemplate;
    private final static String[] HEART = {"♡", "♥"};

    public LikesDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LikesVO> likeList (int postNo) {
        String sql = "SELECT * FROM VM_LIKE WHERE POSTNO = ?";
        List<LikesVO> result = null;
        try {
            result = jdbcTemplate.query(sql,new Object[]{postNo}, new LikesRowMapper());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public List<LikesVO> likeList (String id) {
        String sql = "SELECT * FROM VM_LIKE WHERE USERID = ?";
        List<LikesVO> result = null;
        try {
            result = jdbcTemplate.query(sql,new Object[]{id}, new LikesRowMapper());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean isLike(List<LikesVO> list, String name){
        for (LikesVO vo :list){
            if (vo.getNName().equals(name)) return true;
        }
        return false;
    }

    public String likeMark(List<LikesVO> list, int postNo) {
        for (LikesVO vo : list) {
            if (vo.getPostNo() == postNo) return HEART[1];
        }
        return HEART[0];
    }

    public boolean addLike(int postNo, String id) {
        String sql = "INSERT INTO LIKES (USERID, POSTNO) VALUES (?, ?)";
        int result = 0;
        try {
            result = jdbcTemplate.update(sql, id, postNo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }

    public boolean cancelLike(int postNo, String id) {
        String sql = "DELETE FROM LIKES WHERE USERID = ? AND POSTNO = ?";
        int result = 0;
        try {
            result = jdbcTemplate.update(sql, id, postNo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }

    private static class LikesRowMapper implements RowMapper<LikesVO> {

        @Override
        public LikesVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new LikesVO(
                    rs.getInt("POSTNO"),
                    rs.getString("NNAME"),
                    rs.getString("USERID")
            );
        }
    }
}