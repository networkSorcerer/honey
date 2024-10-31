package com.kh.HoneySpring.dao;

import com.kh.HoneySpring.vo.CommentsVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CommentsDAO {
    private final JdbcTemplate jdbcTemplate;
    
    public CommentsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CommentsVO> commList (int postNo) {
        String sql = "SELECT * FROM VM_COMM WHERE POSTNO = ?";
        List<CommentsVO> result = null;
        try {
            result = jdbcTemplate.query(sql,new Object[]{postNo}, new CommentsRowMapper());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean addComment(CommentsVO vo, int commNo) {
        String sql = "INSERT INTO COMMENTS (COMMNO, SUBNO, POSTNO, USERID, CCONTENT, CDATE) VALUES(?, SEQ_SUBNO.NEXTVAL, ?, ?, ?, SYSDATE)";
        int result = 0;
        try {
            result = jdbcTemplate.update(sql, commNo, vo.getPostNo(), vo.getUserId(), vo.getContent());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }
    public boolean addComment(CommentsVO vo) {
        String sql = "INSERT INTO COMMENTS (COMMNO, SUBNO, POSTNO, USERID, CCONTENT, CDATE) VALUES(SEQ_COMMNO.NEXTVAL, SEQ_SUBNO.NEXTVAL, ?, ?, ?, SYSDATE)";
        int result = 0;
        try {
            result = jdbcTemplate.update(sql, vo.getPostNo(), vo.getUserId(), vo.getContent());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }

    public boolean updateComment(CommentsVO vo) {
        String sql = "UPDATE COMMENTS SET CCONTENT = ? WHERE SUBNO = ?";
        int result = 0;
        try {
            result = jdbcTemplate.update(sql, vo.getContent(), vo.getSubNo());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }

    public boolean deleteComment(CommentsVO vo) {
        String sql = "UPDATE COMMENTS SET CCONTENT = '삭제된 댓글입니다.\n' , USERID = 'unknown' WHERE SUBNO = ?";
        int result = 0;
        try {
            result = jdbcTemplate.update(sql, vo.getSubNo());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }

    public CommentsVO getComment(int subNo) {
        String sql = "SELECT * FROM VM_COMM WHERE SUBNO = ?";
        List<CommentsVO> result = null;
        try {
            result = jdbcTemplate.query(sql,new Object[]{subNo}, new CommentsRowMapper());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return (result != null && !result.isEmpty()) ? result.get(0) : null;
    }

    private static class CommentsRowMapper implements RowMapper<CommentsVO> {

        @Override
        public CommentsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommentsVO(
                    rs.getInt("POSTNO"),
                    rs.getString("NNAME"),
                    rs.getString("CCONTENT"),
                    rs.getDate("CDATE"),
                    rs.getInt("COMMNO"),
                    rs.getInt("SUBNO"),
                    rs.getString("USERID")
            );
        }
    }
}