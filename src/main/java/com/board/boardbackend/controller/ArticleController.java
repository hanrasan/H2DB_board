package com.board.boardbackend.controller;

import com.board.boardbackend.domain.Article;
import com.board.boardbackend.dto.ArticleResponseDto;
import com.board.boardbackend.security.UserDetailsImpl;
import com.board.boardbackend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<Page<ArticleResponseDto>> getAllArticles(
            @RequestParam(required = false) String keyword,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        Page<Article> articlePage = articleService.findAllArticles(keyword, pageable);
        Page<ArticleResponseDto> articleDtoPage = articlePage.map(ArticleResponseDto::new);
        return ResponseEntity.ok(articleDtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseDto> getArticleById(@PathVariable Long id) {
        Article article = articleService.findArticleById(id);
        return ResponseEntity.ok(new ArticleResponseDto(article));
    }

    @PostMapping
    public ResponseEntity<ArticleResponseDto> createArticle(
            @RequestBody Article article,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Article savedArticle = articleService.saveArticle(article, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ArticleResponseDto(savedArticle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponseDto> updateArticle(
            @PathVariable Long id,
            @RequestBody Article article,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Article updatedArticle = articleService.updateArticle(id, article, userDetails.getUser());
        return ResponseEntity.ok(new ArticleResponseDto(updatedArticle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticleById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        articleService.deleteArticleById(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}
