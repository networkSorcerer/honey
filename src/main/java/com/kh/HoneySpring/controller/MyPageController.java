package com.kh.HoneySpring.controller;

import com.kh.HoneySpring.dao.MyPageDAO;
import com.kh.HoneySpring.dao.UsersDAO;
import com.kh.HoneySpring.vo.UsersVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/users")
public class MyPageController {
    private final MyPageDAO myPageDAO;
    private final UsersDAO usersDAO;

    public MyPageController(MyPageDAO myPageDAO, UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
        this.myPageDAO = myPageDAO;
    }

    @GetMapping("/myPage")
    public String showMyPage(@SessionAttribute(value = "login", required = false) UsersVO user, Model model,
                             RedirectAttributes redirectAttributes){

        if(user == null) {
            redirectAttributes.addFlashAttribute("error", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return "redirect:/users/login";
        }
        model.addAttribute("user", user);
        return "thymeleaf/myPage";
    }

    @GetMapping("/verify")
    public String verifyPW(@SessionAttribute(value = "login", required = false) UsersVO user, Model model,
                           @RequestParam(value="type",defaultValue = "type1") String type, RedirectAttributes redirectAttributes){
        if(user == null) {
            redirectAttributes.addFlashAttribute("error", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return "redirect:/users/login";
        }
        model.addAttribute("password","");
        model.addAttribute("type", type);
        return "thymeleaf/verify";
    }

    @PostMapping("/verify")
    public String submitVerify(@SessionAttribute(value = "login",required = false) UsersVO user,
                               @ModelAttribute("type") String type, @ModelAttribute("password") String password,
                               RedirectAttributes redirectAttributes, Model model){
        if(user == null) {
            redirectAttributes.addFlashAttribute("error", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return "redirect:/users/login";
        }
        if(myPageDAO.checkPassword(user.getUserID(), password)){
            redirectAttributes.addFlashAttribute("verify","verified");
            return switch (type) {
                case "type1" -> "redirect:/users/select";
                case "type2" -> "redirect:/users/update";
                default -> {
                    redirectAttributes.addFlashAttribute("error", "잘못된 연결입니다.");
                    yield "redirect:/users/verify";
                }
            };
        }
        redirectAttributes.addFlashAttribute("error","비밀번호가 일치하지 않습니다.");
        return "redirect:/users/verify";

    }

    @GetMapping("/select")
    public String selectViewUser(@SessionAttribute(value = "login", required = false) UsersVO vo,
                                 @ModelAttribute(value = "verify") String verify,
                                 RedirectAttributes redirectAttributes, Model model) {
        if(vo == null) {
            redirectAttributes.addFlashAttribute("error", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return "redirect:/users/login";
        }
        if(!verify.equals("verified")) {
            redirectAttributes.addFlashAttribute("error","인증되지 않았습니다. 인증을 먼저 해주십시오");
            return "redirect:/users/myPage";
        }
        UsersVO user = myPageDAO.findUserById(vo.getUserID());
        model.addAttribute("user", user);
        return "thymeleaf/selectInfo";
    }

    @GetMapping("/update")
    public String updateUserForm(@SessionAttribute("login") UsersVO vo,
                                 @ModelAttribute(value = "verify") String verify,
                                 RedirectAttributes redirectAttributes, Model model) {
        if(vo == null) {
            redirectAttributes.addFlashAttribute("error", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return "redirect:/users/login";
        }
        if(!verify.equals("verified")) {
            redirectAttributes.addFlashAttribute("error","인증되지 않았습니다. 인증을 먼저 해주십시오");
            return "redirect:/users/myPage";
        }
        UsersVO userToUpdate = myPageDAO.findUserById(vo.getUserID());
        model.addAttribute("user", userToUpdate);
        return "thymeleaf/updateInfo";
    }

    // 사용자 업데이트 처리 (POST 요청)
    @PostMapping("/update")
    public String updateUser(@SessionAttribute("login") UsersVO user,
                             @ModelAttribute("user") UsersVO vo, Model model,
                             RedirectAttributes redirectAttributes) {
        if(user == null) {
            redirectAttributes.addFlashAttribute("error", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return "redirect:/users/login";
        }

        List<Boolean> validate = new ArrayList<>();
        List<String> validString = List.of(
                "비밀번호가 잘못되었습니다.",
                "비밀번호가 일치하지 않습니다.",
                "닉네임이 잘못되었습니다.",
                "전화번호가 잘못되었습니다.",
                "제시문이 잘못되었습니다.",
                "제시어가 잘못되었습니다."
        );

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
        boolean isUpdate = false;
        if (isValid) {
            user = myPageDAO.findUserById(vo.getUserID());

            if (usersDAO.isNicknameExists(vo.getNName()) && !user.getNName().equals(vo.getNName())) {
                valid.add("이미 사용 중인 닉네임입니다.");
                isValid = false;
            }
            if (usersDAO.isPhoneExists(vo.getPhone()) && !user.getPhone().equals(vo.getPhone())) {
                valid.add("이미 사용 중인 전화번호입니다.");
                isValid = false;
            }
        }
        if(isValid) isUpdate = myPageDAO.usersUpdate(vo);
        model.addAttribute("signUp", vo);
        model.addAttribute("valid", valid);
        model.addAttribute("isValid", isValid);
        model.addAttribute("isJoin", isUpdate);
        return "thymeleaf/submitUpdateInfo"; // 업데이트 완료 후 사용자 목록으로 리다이렉트
    }


}