package com.board.boardbackend.service;

import com.board.boardbackend.domain.Article;
import com.board.boardbackend.exception.ApiException;
import com.board.boardbackend.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;

    public Page<Article> findAllArticles(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return articleRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
        } else {
            return articleRepository.findAll(pageable);
        }
    }

    public Article findArticleById(Long id) {
        return articleRepository.findById(id).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, "Article not found with id: " + id)
        );
    }

    @Transactional
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    @Transactional
    public void deleteArticleById(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Article not found with id: " + id);
        }
        articleRepository.deleteById(id);
    }

    @Transactional
    public Article updateArticle(Long id, Article updatedArticle) {
        Article existingArticle = articleRepository.findById(id).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, "Article not found with id: " + id)
        );

        existingArticle.setTitle(updatedArticle.getTitle());
        existingArticle.setContent(updatedArticle.getContent());
        return articleRepository.save(existingArticle);
    }
}
