package com.kh.HoneySpring.controller;

import com.kh.HoneySpring.dao.LikesDAO;
import com.kh.HoneySpring.dao.MyPageDAO;
import com.kh.HoneySpring.dao.PostListDAO;
import com.kh.HoneySpring.vo.LikesVO;
import com.kh.HoneySpring.vo.PostsVO;
import com.kh.HoneySpring.vo.UsersVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostListController {
    private final PostListDAO dao;
    private final LikesDAO lDao;
    private final static List<String> SEARCHOPTIONS = List.of("제목", "작성자", "내용", "제목 + 내용");
    private final static List<String> SORTOPTIONS = List.of("최신순", "인기순", "오래된순");
    private final List<String> CATEGORIES;
    private final static int MAXBOARD = 10;

    public PostListController(PostListDAO dao, LikesDAO lDao, MyPageDAO myPageDAO) {
        this.dao = dao;
        this.lDao = lDao;
        CATEGORIES = dao.category();
    }
// 현재는 검색을 DB로 수행하고 있지만 나중에 기능 추가로 페이지별로 따로 검색할 수 있게 만들기
    @GetMapping("/board")    // http://localhost:8112/posts/board
    public String showBoard(@SessionAttribute(value = "login", required = false) UsersVO vo,
                            @RequestParam(value = "type", defaultValue = "type1") String type,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "search", defaultValue = "") String search,
                            @RequestParam(value = "value", defaultValue = "") String value,
                            @RequestParam(value = "searchType", defaultValue = "type1") String searchType ,
                            @RequestParam(value = "sort", defaultValue = "type1") String sort,
                            Model model) {
        List<PostsVO> boardTemp = new ArrayList<>();
        switch (type) {
            case "type1":
                // 전체 검색
                boardTemp = dao.selectPage();
                break;
            case "type2":
                // 닉네임으로 불러오기
                boardTemp = dao.selectPage(value,0);
                break;
            case "type3":
                // 카테고리로 불러오기
                boardTemp = dao.selectPage(value,1);
                break;
            case "type4":
                // 좋아요한 글 불러오기
                boardTemp = dao.selectPage(value,2);
                break;
            case "type5":
                // 댓글 쓴 글 불러오기
                boardTemp = dao.selectPage(value,3);
                break;
            default:
                System.out.println("코드 에러");
                break;
        }
        List<PostsVO> board = new ArrayList<>();
        if(searchType.equals("type1")) board = boardTemp;
        else{
            for (PostsVO post : boardTemp) {
                switch (searchType) {
                    case "type2":
                        if(post.getTitle().contains(search)) board.add(post);
                        break;
                    case "type3":
                        if(post.getAuthor().contains(search)) board.add(post);
                        break;
                    case "type4":
                        if(dao.getContent(post.getPostno()).contains(search)) board.add(post);
                        break;
                    case "type5":
                        if(post.getTitle().contains(search)
                                || dao.getContent(post.getPostno()).contains(search)) board.add(post);
                        break;
                    default:
                        System.out.println("코드 에러");
                        break;
                }
            }

        }
        String id = (vo != null)?vo.getUserID():null;
        List<LikesVO> like = lDao.likeList(id);
        int boardNo = (int)Math.ceil((double) board.size()/MAXBOARD);
        for (PostsVO post : board) post.setContent(lDao.likeMark(like, post.getPostno()));

        switch (sort){
            case "type1":
                Collections.sort(board);
                break;
            case "type2":
                board.sort(new Comparator<PostsVO>() {
                    @Override
                    public int compare(PostsVO o1, PostsVO o2) {
                        return lDao.likeList(o2.getPostno()).size() - lDao.likeList(o1.getPostno()).size();
                    }
                });
                break;
            case "type3":
                board.sort(Collections.reverseOrder());
                break;
        }

        model.addAttribute("user",vo);
        model.addAttribute("isUser",  vo != null);
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("maxBoard",MAXBOARD);
        model.addAttribute("boardNo", boardNo);
        model.addAttribute("searchOptions", SEARCHOPTIONS);
        model.addAttribute("sortOptions", SORTOPTIONS);
        model.addAttribute("board", board);
        model.addAttribute("search",search);
        model.addAttribute("searchType", searchType);
        model.addAttribute("sort", sort);
        model.addAttribute("value", value);
        model.addAttribute("page", page);
        model.addAttribute("type", type);
        return "thymeleaf/showBoard";
    }
}