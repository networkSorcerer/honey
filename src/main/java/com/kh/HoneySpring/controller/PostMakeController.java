package com.kh.HoneySpring.controller;

import com.kh.HoneySpring.dao.PostListDAO;
import com.kh.HoneySpring.dao.PostMakeDAO;
import com.kh.HoneySpring.vo.PostsVO;
import com.kh.HoneySpring.vo.UsersVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostMakeController {
    private final PostMakeDAO postMakeDAO;
    private final List<String> CATEGORIES;

    public PostMakeController(PostMakeDAO postMakeDAO, PostListDAO postListDAO) {
        CATEGORIES = postListDAO.category();
        this.postMakeDAO = postMakeDAO;
    }
    
    // GetMapping 으로 빈객체를 생성후 입력 받아 전달
    @GetMapping("/create")
    public String showCreatePostForm(@SessionAttribute("login") UsersVO usersVO, Model model, PostsVO postsVO) {
        if (usersVO == null) {
            return "redirect:/users/login"; // 로그인 페이지로 리다이렉트
        }
        PostsVO post = new PostsVO();
        post.setAuthor(usersVO.getNName());
        post.setUserID(usersVO.getUserID());
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("post", post);
        return "thymeleaf/postMakeCreate";
    }
    // GetMapping 으로 입력 받은 값을 전달하기 위한 메서드
    @PostMapping("/create")
    public String submitPost(@Validated @ModelAttribute("post") PostsVO postsVO,
                             BindingResult bindingResult, Model model) {
        model.addAttribute("isSubmitted", false); // 기본값 설정

        // 제목 유효성 검사
        if (postsVO.getTitle() == null || postsVO.getTitle().trim().isEmpty()) {
            bindingResult.rejectValue("title", "error.title", "제목을 입력하세요.");
        }

        // 내용 유효성 검사
        if (postsVO.getContent() == null || postsVO.getContent().trim().isEmpty()) {
            bindingResult.rejectValue("content", "error.content", "내용을 입력하세요.");
        }

        // 카테고리 유효성 검사
        if (postsVO.getCategory() == null || postsVO.getCategory().trim().isEmpty()) {
            bindingResult.rejectValue("category", "error.categories", "카테고리를 선택 해 주세요.");
        }

        // 카테고리의 값이 일치 하는지
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "글 작성에 실패했습니다. 모든 필드를 입력하세요.");
            model.addAttribute("category", CATEGORIES);
            return "thymeleaf/postMakeResult"; // 오류 시 결과 페이지로 이동
        }

        // 게시물 작성 처리
        boolean isSubmitted = postMakeDAO.PostmakeCreate(postsVO);
        if (!isSubmitted) {
            model.addAttribute("error", "유효하지 않은 사용자 ID 또는 카테고리입니다.");
            return "thymeleaf/postMakeResult"; // 실패 시 결과 페이지로 이동
        }

        model.addAttribute("isSubmitted", true);
        return "thymeleaf/postMakeResult"; // 성공 시 결과 페이지로 이동
    }
}


