package com.kh.HoneySpring.controller;

import com.kh.HoneySpring.dao.CommentsDAO;
import com.kh.HoneySpring.dao.LikesDAO;
import com.kh.HoneySpring.dao.PostViewDAO;
import com.kh.HoneySpring.vo.CommentsVO;
import com.kh.HoneySpring.vo.LikesVO;
import com.kh.HoneySpring.vo.PostsVO;
import com.kh.HoneySpring.vo.UsersVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostViewController {
    private final PostViewDAO dao;
    private final LikesDAO lDao;
    private final CommentsDAO cDao;
    private final List<String> CATEGORIES;

    public PostViewController(PostViewDAO dao , LikesDAO ldao , CommentsDAO cdao) {
        this.dao = dao;
        this.lDao = ldao;
        this.cDao = cdao;
        CATEGORIES = dao.category();
    }

    @GetMapping("/view")    // http://localhost:8112/posts/list
    public String viewPost(@SessionAttribute(value="login", required = false)UsersVO vo,
                           @RequestParam("postno") int postNo, @RequestParam(value = "commNo",defaultValue = "0")int commNo,
                           @RequestParam(value = "subNo", defaultValue = "0")int subNo, @RequestParam(value = "isComment",defaultValue = "false") boolean isComment,
                           @RequestParam(value = "content", defaultValue = "")String content ,Model model) {
        PostsVO post= dao.viewPost(postNo);
        List<LikesVO> lList = lDao.likeList((vo!=null)?vo.getUserID():"");
        int likeNo = lDao.likeList(postNo).size();
        String likeMark = lDao.likeMark(lList,postNo);
        List<CommentsVO> cList = cDao.commList(postNo);
        Collections.sort(cList);
        for (int i = 1; i < cList.size(); i++) {
            if (cList.get(i).getCommNo() == cList.get(i - 1).getCommNo()) {
                cList.get(i).setContent("->" + cList.get(i).getContent());
            }
        }
        if(content.startsWith("->"))content = content.substring(2);
        CommentsVO comment = null;
        if(vo!=null){
            comment = new CommentsVO();
            comment.setPostNo(postNo);
            comment.setNName(vo.getNName());
            comment.setUserId(vo.getUserID());
            comment.setCommNo(commNo);
            comment.setSubNo(subNo);
            comment.setContent(content);
        }
        model.addAttribute("isComment", isComment);
        model.addAttribute("comment", comment);
        model.addAttribute("post", post);
        model.addAttribute("user",vo);
        model.addAttribute("isUser",vo!=null);
        model.addAttribute("likeNo",likeNo);
        model.addAttribute("likeMark",likeMark);
        model.addAttribute("cList", cList);
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("name", (vo!=null)?vo.getNName():null);
        return "thymeleaf/viewPost";
    }

    @PostMapping(value = "/view/like")
    public String like(@SessionAttribute("login") UsersVO vo, @RequestParam("postno") int postno, RedirectAttributes redirectAttributes) {
        List<LikesVO> lList = lDao.likeList(postno);
        boolean success =(lDao.isLike(lList,vo.getNName()))?lDao.cancelLike(postno, vo.getUserID()):lDao.addLike(postno, vo.getUserID());
        redirectAttributes.addFlashAttribute("likeSuccess", (success)?1:2);
        return "redirect:/posts/view?postno="+postno;
    }

    @PostMapping(value="/view")
    public String submitComment(@ModelAttribute("comment") CommentsVO comment, RedirectAttributes redirectAttributes) {
        boolean isSuccess;
        if(comment.getCommNo() == 0){
            isSuccess = cDao.addComment(comment);
        }
        else if (comment.getSubNo() == 0){
            isSuccess = cDao.addComment(comment,comment.getCommNo());
        }
        else{
            isSuccess = cDao.updateComment(comment);
            redirectAttributes.addFlashAttribute("updateSuccess", !isSuccess);
            return "redirect:/posts/view?postno="+comment.getPostNo();
        }
        redirectAttributes.addFlashAttribute("createCommSuccess", !isSuccess);
        return "redirect:/posts/view?postno="+comment.getPostNo();
    }

    @GetMapping("/update")
    public String updatePost(@RequestParam("postno") int postNo, Model model) {
        PostsVO post= dao.viewPost(postNo);
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("post", post);
        return "thymeleaf/updatePost";
    }

    @PostMapping("/update")
    public String submitUpdatePost(@ModelAttribute("post") PostsVO post, RedirectAttributes redirectAttributes) {
        boolean success = dao.updatePost(post);
        redirectAttributes.addFlashAttribute("updateSuccess", (success)?1:2);
        return "redirect:/posts/view?postno="+post.getPostno();
    }

    @PostMapping("/delete")
    public String deletePost(@RequestParam("postno") int postNo, RedirectAttributes redirectAttributes) {
        PostsVO post= dao.viewPost(postNo);
        boolean success = dao.deletePost(post.getPostno());
        redirectAttributes.addFlashAttribute("deleteSuccess", (success)?1:2);
        return "redirect:/posts/board";
    }

    @PostMapping("/comment/delete")
    public String submitDeleteComment(@RequestParam("subNo") int subNo, RedirectAttributes redirectAttributes) {
        CommentsVO vo = cDao.getComment(subNo);
        boolean success = cDao.deleteComment(vo);
        redirectAttributes.addFlashAttribute("deleteCommSuccess", (success)?1:2);
        return "redirect:/posts/view?postno="+vo.getPostNo();
    }

}