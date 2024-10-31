package com.kh.HoneySpring.controller;

import com.kh.HoneySpring.dao.UsersDAO;
import com.kh.HoneySpring.dao.findPWDAO;
import com.kh.HoneySpring.dao.findIDDAO;
import com.kh.HoneySpring.vo.UsersVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/login")
public class MainController {
    private final UsersDAO usersDAO;
    private final findIDDAO findIDDAO;
    private final findPWDAO findPWDAO;
// 의존성 주입
    public MainController(UsersDAO usersDAO, findIDDAO findIDDAO, findPWDAO findPWDAO) {
        this.usersDAO = usersDAO;
        this.findIDDAO = findIDDAO;
        this.findPWDAO = findPWDAO;
    }
//회원가입----------------------------------------------------------------------------------------------------
    @GetMapping("/signUp") //  http://localhost:8112/login/signUp
    public String signUp(Model model) {
        model.addAttribute("signUp", new UsersVO());
        model.addAttribute("confirmPW", "");
        return "thymeleaf/signUp";
    }

    @PostMapping("/signUp")
    public String validAndJoin(@ModelAttribute("signUp") UsersVO vo, Model model) {
        List<Boolean> validate = new ArrayList<>();
        List<String> validString = List.of(
                "아이디가 잘못되었습니다",
                "비밀번호가 잘못되었습니다.",
                "비밀번호가 일치하지 않습니다.",
                "닉네임이 잘못되었습니다.",
                "전화번호를 '010-0000-0000' 형식으로 입력 해 주세요.",
                "제시문이 잘못되었습니다.",
                "제시어가 잘못되었습니다."
        );

        validate.add(usersDAO.validateUserID(vo.getUserID()));
        validate.add(usersDAO.validatePW(vo.getUserPW()));
        validate.add(usersDAO.validateConfirmPW(vo.getUserPW(), vo.getConfirmPW()));
        validate.add(usersDAO.validateNickname(vo.getNName()));
        validate.add(usersDAO.validatePhone(vo.getPhone()));
        validate.add(usersDAO.validatePwLOCK(vo.getPwLOCK()));
        validate.add(usersDAO.validatePwKey(vo.getPwKey()));
        boolean isValid = true;
        List<String> valid = new ArrayList<>();
        for (int i = 0; i < validate.size(); i++) {
            if (!validate.get(i)) {
                valid.add(validString.get(i));
                isValid = false;
            }
        }
        boolean isJoin = false;
        if (isValid) {
            if (usersDAO.isUserIDExists(vo.getUserID())) {
                valid.add("이미 사용 중인 아이디입니다.");
                isValid = false;
            }
            if (usersDAO.isNicknameExists(vo.getNName())) {
                valid.add("이미 사용 중인 닉네임입니다.");
                isValid = false;
            }
            if (usersDAO.isPhoneExists(vo.getPhone())) {
                valid.add("이미 사용 중인 전화번호입니다.");
                isValid = false;
            }

        }
        if(isValid) isJoin = usersDAO.joinMember(vo);
        model.addAttribute("signUp", vo);
        model.addAttribute("valid", valid);
        model.addAttribute("isValid", isValid);
        model.addAttribute("isJoin", isJoin);
        return "thymeleaf/submitSignup";
    }


// 아이디 찾기---------------------------------------------------------------------------------------------
    @GetMapping("/findID")
    public String findID() {
        return "thymeleaf/findID";
    }

    // 아이디 찾기 처리
    @PostMapping("/findID")
    public String findID(String phone, Model model){
        String userID = findIDDAO.findID(phone);
        if (userID != null) {
            String maskedID = maskUserID(userID);
            model.addAttribute("result", maskedID);
        } else {
            model.addAttribute("error", "해당 전화번호로 가입된 아이디가 없습니다.");
        }
        return "thymeleaf/showID";
    }

// 아이디 마스킹------------------------------------------------------------------------------------
    private String maskUserID(String userID) {
        if (userID == null || userID.isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 비어있을 수 없습니다.");
        }
        if (userID.length() < 4) {
            return userID; // 길이가 4 미만일 경우 원본 문자열 반환
        }
        String visiblePart = userID.substring(0, 4);
        String maskedPart = "*".repeat(userID.length() - 4);
        return visiblePart + maskedPart;
    }

// 비밀번호 찾기----------------------------------------------------------------------------
    @GetMapping("/findPW")
    public String findPW() {
        return "thymeleaf/findPW";
    }

    @PostMapping("/findPW")
    public String findPW(String userID, Model model) {
        String pwLOCK = findPWDAO.getPWLock(userID); // 제시문 가져오기
        if (pwLOCK != null) {
            model.addAttribute("userID", userID); // 사용자 ID를 모델에 추가
            model.addAttribute("pwLOCK", pwLOCK); // 제시문을 모델에 추가
            return "thymeleaf/inputPWKey"; // 제시어 입력 페이지로 이동
        } else {
            // 아이디가 존재하지 않을 경우
            model.addAttribute("error", "해당 아이디로 가입된 계정이 없습니다.");
            return "thymeleaf/showPW"; // 결과 페이지로 이동
        }
    }

    @PostMapping("/inputPWKey")
    public String submitPwKey(String userID, String pwKey, Model model) {
        String result = findPWDAO.findPW(userID, pwKey); // 비밀번호 찾기 처리
        model.addAttribute("result", result); // 결과를 모델에 추가
        return "thymeleaf/showPW"; // 결과 페이지로 이동
    }
}
