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
public class PostListDAO {
    private final JdbcTemplate jdbcTemplate;

    public PostListDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PostsVO> selectPage(String value, int sel) {
        if (sel > sql.length) {
            System.out.println("PLDAO 입력값 코딩 오류");
            return null;
        }
        String sqlTemp = sql[sel];
        List<PostsVO> result = null;
        try {
            result = jdbcTemplate.query(sqlTemp,new Object[]{value}, new PostListRowMapper());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public List<PostsVO> selectPage() {
        String sqlTemp = "SELECT * From VM_POSTS_PAGE";
        List<PostsVO> result = null;
        try {
            result = jdbcTemplate.query(sqlTemp, new PostListRowMapper());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
    private final static String[] sql =
            {"SELECT * From VM_POSTS_PAGE WHERE NNAME = ?",
                    "SELECT * From VM_POSTS_PAGE WHERE CATE = ?",
                    "SELECT * From VM_POSTS_PAGE WHERE POSTNO IN " +
                            "(SELECT POSTNO FROM VM_LIKE WHERE NNAME = ?)",
                    "SELECT * FROM VM_POSTS_PAGE WHERE POSTNO in " +
                            "(SELECT POSTNO FROM VM_COMM WHERE NNAME = ?)"
            };

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

    public String getContent(int postNo){
        String sql = "SELECT PCONTENT FROM VM_POST WHERE POSTNO = ?";
        List<String> result = null;
        try{
            result = jdbcTemplate.query(sql, new Object[]{postNo}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("PCONTENT");
                }
            });
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result != null ? result.get(0) : null;
    }

    private static class PostListRowMapper implements RowMapper<PostsVO> {

        @Override
        public PostsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PostsVO(
                    rs.getInt("POSTNO"),
                    rs.getString("TITLE"),
                    null,
                    rs.getString("NNAME"),
                    rs.getDate("PDATE"),
                    rs.getString("CATE"),
                    rs.getString("USERID")
            );
        }
    }
}