package com.hancoding.board.service;

import com.hancoding.board.entity.Board;
import com.hancoding.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService  {

    @Autowired  // Dependancy Injection
    private BoardRepository boardRepository;
    // 글 작성
    public void write(Board board, MultipartFile file) {
        try {
            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();
            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);
            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);

            boardRepository.save(board);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // 게시글 목록 가져오기
    // paging 처리를 위해 반환타입을 List<Board> --> Page<Board>로 변경함
    // public List<Board> boardList() {
    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    // 특정 게시글 불러오기
    public Board boardView(int id) {
        return boardRepository.findById(id).get();
    }

    // 특정 게시글 삭제
    public void boardDelete(int id) {
        boardRepository.deleteById(id);
    }

    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable) {
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

}
