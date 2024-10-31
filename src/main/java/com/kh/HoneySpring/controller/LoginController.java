package com.kh.HoneySpring.controller;

import com.kh.HoneySpring.dao.LoginDAO;
import com.kh.HoneySpring.vo.UsersVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class LoginController {
    private final LoginDAO loginDAO;

    public LoginController(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    @PostMapping("/login")  // http://localhost:8112/users/login
    public String login(@ModelAttribute("login") UsersVO usersVO, Model model, HttpSession session) {
        // 기존에 DB 내에 있는 매개변수를 통해 페이지내에서 입력받은 ID 와 PW 비교
        UsersVO dbBasedUser = loginDAO.FindByUserID(usersVO.getUserID());

        // 로그인 입력시 Null 값이 오지않고 기존에 있는 데이터베이스와 새로 GetMapping 에서 전달받은 아이디 비밀번호와 비교
        if (dbBasedUser != null && usersVO.getUserPW().equals(dbBasedUser.getUserPW())) {
            dbBasedUser.setUserPW(null);
            session.setAttribute("login", dbBasedUser);
            return "redirect:/posts/board"; // 로그인 성공 시 posts/board 로 리디렉션
        } else {
            model.addAttribute("error", "아이디, 비밀번호가 올바르지 않습니다.");   // 로그인 실패시 에러표시
            return "thymeleaf/login"; // 실패 시 로그인 페이지로
        }
    }

    @GetMapping("/board")
    public String board() {
        return "thymeleaf/showBoard";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        session.removeAttribute("login");
        model.addAttribute("login", new UsersVO());
        return "thymeleaf/login";
    }

    @GetMapping("/findID") // html 에서 버튼 클릭시 아이디찾기 페이지로
    public String findID(Model model) {    // http://localhost:8112/findID
        return "thymeleaf/findID";
    }

    @GetMapping("/findPW") // html 에서 버튼 클릭시 비밀번호 찾기 페이지로
    public String findPW(Model model) {    // http://localhost:8112/findPW
        return "thymeleaf/findPW";
    }

    @GetMapping("/signUp") // html 에서 버튼 클릭시 회원가입 페이지로
    public String signUp(Model model) {    // http://localhost:8112/joinUser
        return "thymeleaf/signUp";
    }
}
