package com.board.boardbackend.service;

import com.board.boardbackend.domain.Article;
import com.board.boardbackend.domain.User;
import com.board.boardbackend.domain.UserRoleEnum;
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
    public Article saveArticle(Article article, User user) {
        Article newArticle = new Article(article.getTitle(), article.getContent(), user);
        return articleRepository.save(newArticle);
    }

    @Transactional
    public void deleteArticleById(Long id, User user) {
        Article articleToDelete = articleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Article not found with id: " + id));

        if (!articleToDelete.getUser().getId().equals(user.getId()) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "게시글을 삭제할 권한이 없습니다.");
        }

        articleRepository.deleteById(id);
    }

    @Transactional
    public Article updateArticle(Long id, Article updatedArticle, User user) {
        Article existingArticle = articleRepository.findById(id).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, "Article not found with id: " + id)
        );

        if (!existingArticle.getUser().getId().equals(user.getId()) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "게시글을 수정할 권한이 없습니다.");
        }

        existingArticle.setTitle(updatedArticle.getTitle());
        existingArticle.setContent(updatedArticle.getContent());
        return articleRepository.save(existingArticle);
    }
}
