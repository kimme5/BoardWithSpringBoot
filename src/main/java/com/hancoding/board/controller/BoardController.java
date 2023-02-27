package com.hancoding.board.controller;

import com.hancoding.board.entity.Board;
import com.hancoding.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write")
    public String boardWriteForm() {
        return "boardwrite";
    }

    @PostMapping("/board/writedo")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws IOException {

        // board 클래스에 저장된 정보들을 service 객체로 넘겨 DB에 저장하도록 처리함
        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    @GetMapping("/board/list")
    public String boardList(Model model
                          , @PageableDefault(page=0, size=10, sort="id", direction= Sort.Direction.DESC) Pageable pageable
                          , String searchKeyword) {

        Page<Board> pageList = null;

        if(searchKeyword != null) {
            pageList = boardService.boardSearchList(searchKeyword, pageable);
        } else {
            pageList = boardService.boardList(pageable);
        }

        int nowPage = pageList.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage-4, 1);
        int endPage = Math.min(nowPage+5, pageList.getTotalPages());

        model.addAttribute("list", pageList);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "boardlist";
    }

    @GetMapping("/board/view")  // localhost:8080/board/view?id=1
    public String boardView(Model model, int id) {
        model.addAttribute("board", boardService.boardView(id));
        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(int id, Model model) {
        boardService.boardDelete(id);
        model.addAttribute("message", "글 삭제가 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    // 수정 페이지로 넘어감
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") int id, Model model) {
        model.addAttribute("article", boardService.boardView(id));
        return "boardmodify";
    }

    // 수정 후 실제 transaction 처리가 이루어짐
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") int id, Board board, Model model, MultipartFile file) throws IOException {
        // 특정 id 정보를 boardView()를 통해 얻어와 boardTemp로 생성함
        Board boardTemp = boardService.boardView(id);
        // 변경된 제목을 boardTemp에 새롭게 담음
        boardTemp.setTitle(board.getTitle());
        // 변경된 내용을 boardTemp에 새롭게 담음
        boardTemp.setContent(board.getContent());
        // boardServe.write() 메소드를 통해 boardTemp를 저장함
        boardService.write(boardTemp, file);

        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
//        model.addAttribute("searchUrl", "redirect:/board/list");

        return "message";
    }

}
