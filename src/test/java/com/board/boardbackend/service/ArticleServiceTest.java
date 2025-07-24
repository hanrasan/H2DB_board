package com.board.boardbackend.service;

import com.board.boardbackend.domain.Article;
import com.board.boardbackend.domain.User;
import com.board.boardbackend.domain.UserRoleEnum;
import com.board.boardbackend.exception.ApiException;
import com.board.boardbackend.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {
    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    User testUser;
    User adminUser;
    Article testArticle;

    @BeforeEach
    public void setUp() {
        testUser = new User("testUser", "password", UserRoleEnum.USER);
        testUser.setId(100L);
        adminUser = new User("testAdmin", "password", UserRoleEnum.ADMIN);
        adminUser.setId(200L);
        testArticle = new Article("Test Title", "Test Content", testUser);
        testArticle.setId(1L);
    }

    @Test
    @DisplayName("게시글 생성 테스트 - 성공")
    void saveArticle_Success() {
        given(articleRepository.save(any(Article.class))).willReturn(testArticle);

        Article savedArticle = articleService.saveArticle(testArticle, testUser);

        assertNotNull(savedArticle);
        assertEquals("Test Title", savedArticle.getTitle());
        assertEquals("Test Content", savedArticle.getContent());
        assertEquals(testUser.getUsername(), savedArticle.getUser().getUsername());

        verify(articleRepository).save(any(Article.class));
    }

    @Test
    @DisplayName("게시글 수정 테스트 - 성공 (작성자 본인)")
    void updateArticle_Success_Owner() {
        given(articleRepository.findById(1L)).willReturn(Optional.of(testArticle));
        given(articleRepository.save(any(Article.class))).willAnswer(invocation -> invocation.getArgument(0));

        Article updatedArticleRequest = new Article("Updated Title", "Updated Content", null);
        Article updatedArticle = articleService.updateArticle(1L, updatedArticleRequest, testUser);

        assertNotNull(updatedArticle);
        assertEquals("Updated Title", updatedArticle.getTitle());
        assertEquals("Updated Content", updatedArticle.getContent());
        assertEquals(testUser.getUsername(), updatedArticle.getUser().getUsername());
    }

    @Test
    @DisplayName("게시글 수정 테스트 - 실패 (권한 없음 - 다른 사용자")
    void updateArticle_Fail_Unauthorized() {
        given(articleRepository.findById(1L)).willReturn(Optional.of(testArticle));

        User autherUser = new User("anotherUser", "password", UserRoleEnum.USER);
        Article updatedArticleRequest = new Article("Updated Title", "Updated Content", null);

        ApiException exception = assertThrows(ApiException.class, () ->
                articleService.updateArticle(1L, updatedArticleRequest, autherUser));

        assertEquals("게시글을 수정할 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("게시글 삭제 테스트 - 성공 (작성자 본인)")
    void deleteArticle_Success_Owner() {
        given(articleRepository.findById(1L)).willReturn(Optional.of(testArticle));

        articleService.deleteArticleById(1L, testUser);

        verify(articleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("게시글 삭제 테스트 - 실패 (권한 없음 - 관리자 아님")
    void deleteArticle_Fail_Unauthorized() {
        given(articleRepository.findById(1L)).willReturn(Optional.of(testArticle));

        User anotherUser = new User("anotherUser", "password", UserRoleEnum.USER);

        ApiException exception = assertThrows(ApiException.class, () ->
                articleService.deleteArticleById(1L, anotherUser));

        assertEquals("게시글을 삭제할 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("게시글 삭제 테스트 - 성공 (관리자)")
    void deleteArticle_Success_Admin() {
        given(articleRepository.findById(1L)).willReturn(Optional.of(testArticle));

        articleService.deleteArticleById(1L, adminUser);

        verify(articleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("게시글 조회 테스트 - 성공")
    void findArticleById_Success() {
        given(articleRepository.findById(1L)).willReturn(Optional.of(testArticle));

        Article foundArticle = articleService.findArticleById(1L);

        assertNotNull(foundArticle);
        assertEquals(testArticle.getTitle(), foundArticle.getTitle());
        assertEquals(testArticle.getContent(), foundArticle.getContent());
    }

    @Test
    @DisplayName("게시글 조회 테스트 - 실패 (존재하지 않는 게시글)")
    void findArticleById_Fail_NotFound() {
        given(articleRepository.findById(999L)).willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () ->
                articleService.findArticleById(999L));

        assertEquals("Article not found with id: 999", exception.getMessage());
    }
}
