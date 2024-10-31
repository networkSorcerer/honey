package com.kh.HoneySpring.dao;

import com.kh.HoneySpring.vo.UsersVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.regex.Pattern;


@Repository
public class UsersDAO {

    private final JdbcTemplate jdbcTemplate;

    public UsersDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
// 회원가입을 위해 jdbc를 이용하여 database에 insert
    public boolean joinMember(UsersVO vo) {
        int result = 0;
        String sql = "INSERT INTO users (userID, userPW, NName, phone, PWLock, PWKey, UDATE) VALUES (?, ?, ?, ?, ?, ?, SYSDATE)";

        try {
            result = jdbcTemplate.update(sql, vo.getUserID(), vo.getUserPW(), vo.getNName(),
                    vo.getPhone() , vo.getPwLOCK(), vo.getPwKey());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result > 0;
    }

// 아이디 생성 조건
    public boolean validateUserID(String userID) {
        if (userID.getBytes().length >= 8 && userID.getBytes().length <= 16) {
            return true;
        } else {
            System.out.println("-아이디 생성 조건을 다시 확인 후 입력 해 주세요.");
            return false;
        }
    }
// 비밀번호 생성 조건
    public boolean validatePW(String userPW) {
        if (userPW.getBytes().length >= 8 && userPW.getBytes().length <= 16) {
            return true;
        } else {
            System.out.println("-비밀번호 생성 조건을 다시 확인 후 입력 해 주세요.");
        }
        return false;
    }
// 비밀번호 확인(동일해야 true)
    public boolean validateConfirmPW(String userPW, String confirmPW) {
        return Objects.equals(userPW, confirmPW);
    }
// 닉네임 생성 조건
    public boolean validateNickname(String nName) {
        if (nName.getBytes().length <= 24) {  // 바이트 기준
            return true;
        } else {
            System.out.println("-닉네임 생성 조건을 다시 확인 후 입력 해 주세요.");
            return false;
        }
    }
// 전화번호 생성 조건
    public boolean validatePhone(String phone) {
        String regex = "^010-\\d{4}-\\d{4}$"; // 010-0000-0000 형식 정규 표현식
        boolean matchesFormat = Pattern.matches(regex, phone);

        if (matchesFormat) {
            return true;
        } else {
            System.out.println("-전화번호를 '010-0000-0000' 형식으로 입력 해 주세요.");
            return false;
        }
    }
// 제시문 생성 조건
    public boolean validatePwLOCK(String pwLock) {
        if (pwLock.getBytes().length >= 60) {
            System.out.print("-제시문 생성 조건을 확인 후 다시 입력 해 주세요.");
            return false;
        } else {
            return true;
        }
    }
// 제시어 생성 조건
    public boolean validatePwKey(String pwKey) {
        if (pwKey.getBytes().length <= 24) {
            return true;
        } else {
            System.out.print("-제시어 생성 조건을 확인 후 다시 입력 해 주세요.");
            return false;
        }
    }
// 아이디 중복 여부 확인
    public boolean isUserIDExists(String userID) {
        String sql = "SELECT COUNT(*) FROM users WHERE userID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{userID}, Integer.class);
        return count != null && count > 0; // 중복된 아이디가 있으면 true 반환
    }
// 닉네임 중복 여부 확인
    public boolean isNicknameExists(String nName) {
        String sql = "SELECT COUNT(*) FROM users WHERE NName = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{nName}, Integer.class);
        return count != null && count > 0; // 중복된 닉네임이 있으면 true 반환
    }
// 전화번호 중복 여부 확인
    public boolean isPhoneExists(String Phone) {
        String sql = "SELECT COUNT(*) FROM users WHERE Phone = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{Phone}, Integer.class);
        return count != null && count > 0; // 중복된 닉네임이 있으면 true 반환
    }
}
