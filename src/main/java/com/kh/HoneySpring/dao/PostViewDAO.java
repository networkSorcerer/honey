package com.kh.HoneySpring.dao;

import com.kh.HoneySpring.vo.PostsVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class PostViewDAO {
    private final JdbcTemplate jdbcTemplate;

    public PostViewDAO(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    public PostsVO viewPost(int postNo) {
        List<PostsVO> result = null;
        String sql = "select * from VM_POST where POSTNO = ?";
        try {
            result = jdbcTemplate.query(sql,new Object[]{postNo}, new PostViewRowMapper());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
            return (result != null && !result.isEmpty())? result.get(0) : null;
    }

    public boolean updatePost(PostsVO vo){
        String sql = "UPDATE POSTS SET TITLE = ?, PCONTENT = ?, CATE = ? WHERE POSTNO = ?";
        int result = 0;
        try {
            result = jdbcTemplate.update(sql, vo.getTitle(), vo.getContent(), vo.getCategory(), vo.getPostno());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }

    public boolean deletePost(int postNo) {
        String sql = "UPDATE POSTS SET CATE = 'DELETE' WHERE POSTNO = ?";
        int result = 0;
        try {
            result = jdbcTemplate.update(sql, postNo);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }

    public List<String> category(){
        List<String> result = null;
        String sql = "SELECT CATE FROM CATEGORY WHERE CATE != 'DELETE'";
        try{
            result = jdbcTemplate.query(sql, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("CATE");
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Collections.sort(result, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if(o1.equals("QNA")) return 1;
                if(o2.equals("QNA")) return -1;
                return o1.compareTo(o2);
            }
        });
        return result;
    }

    private static class PostViewRowMapper implements RowMapper<PostsVO> {

        @Override
        public PostsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostsVO(
                    rs.getInt("POSTNO"),
                    rs.getString("TITLE"),
                    rs.getString("PCONTENT"),
                    rs.getString("NNAME"),
                    rs.getDate("PDATE"),
                    rs.getString("CATE"),
                    rs.getString("USERID")
            );
        }
    }
}