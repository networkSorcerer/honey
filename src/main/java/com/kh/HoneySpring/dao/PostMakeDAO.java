package com.kh.HoneySpring.dao;

import com.kh.HoneySpring.vo.PostsVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class PostMakeDAO {
    private final JdbcTemplate jdbcTemplate;

    public PostMakeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean PostmakeCreate(PostsVO postsVO) {
        // 유저아이디를 확인
        String userCheckSql = "SELECT COUNT(*) FROM Users WHERE USERID = ?";
        Integer userExists = jdbcTemplate.queryForObject(userCheckSql, Integer.class, postsVO.getUserID());
        // 카테고리 유효성 검사
        String cateCheckSql = "SELECT COUNT(*) FROM category WHERE CATE = ?";
        Integer cateExists = jdbcTemplate.queryForObject(cateCheckSql, Integer.class, postsVO.getCategory());
        // 유저와 PostMake 확인
        if ((userExists != null && userExists > 0) && (cateExists != null && cateExists > 0)) {
            String sql = "INSERT INTO Posts (POSTNO, TITLE, USERID, PCONTENT, PDATE, CATE)"
                    + "VALUES (seq_postno.nextval, ?, ?, ?, SYSDATE, ?)";
            jdbcTemplate.update(sql, postsVO.getTitle(), postsVO.getUserID(), postsVO.getContent(), postsVO.getCategory());
            return true;
        } else {
            System.out.println("유효한 아이디와 카테고리가 아닙니다.");
            return false;
        }
    }
}



