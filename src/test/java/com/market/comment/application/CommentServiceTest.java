package com.market.comment.application;

import com.market.comment.application.dto.CommentCreateRequest;
import com.market.comment.application.dto.CommentPatchRequest;
import com.market.comment.domain.Comment;
import com.market.comment.domain.CommentFakeRepository;
import com.market.comment.domain.CommentRepository;
import com.market.comment.exception.exceptions.CommentNotFoundException;
import com.market.comment.exception.exceptions.CommentWriterNotEqualsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.market.comment.fixture.CommentFixture.댓글_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class CommentServiceTest {

    private CommentService commentService;
    private CommentRepository commentRepository;

    @BeforeEach
    void setup() {
        commentRepository = new CommentFakeRepository();
        commentService = new CommentService(commentRepository);
    }

    @Test
    void 댓글을_생성한다() {
        // given
        CommentCreateRequest req = new CommentCreateRequest("내용");

        // when & then
        assertDoesNotThrow(() -> commentService.createComment(1L, 1L, req));
    }

    @Test
    void 게시글_id에_해당하는_댓글을_모두_조회한다() {
        // given
        Comment saved = commentRepository.save(댓글_생성());
        Long boardId = saved.getBoardId();

        // when
        List<Comment> result = commentService.findAllCommentsByBoardId(boardId);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result).hasSize(1);
            softly.assertThat(result.get(0)).usingRecursiveComparison()
                    .isEqualTo(saved);
        });
    }

    @Test
    void 댓글을_수정한다() {
        // given
        Comment saved = commentRepository.save(댓글_생성());
        String text = "edit";
        CommentPatchRequest req = new CommentPatchRequest(text);

        // when
        commentService.patchCommentById(1L, 1L, req);

        // then
        assertThat(saved.getContent()).isEqualTo(text);
    }

    @Test
    void 글쓴이가_아니라면_댓글을_수정하지_못한다() {
        // given
        Comment saved = commentRepository.save(댓글_생성());
        String text = "edit";
        CommentPatchRequest req = new CommentPatchRequest(text);

        // when & then
        assertThatThrownBy(() -> commentService.patchCommentById(2L, 1L, req))
                .isInstanceOf(CommentWriterNotEqualsException.class);
    }

    @Test
    void 댓글이_없다면_댓글을_수정하지_못한다() {
        // given
        String text = "edit";
        CommentPatchRequest req = new CommentPatchRequest(text);

        // when & then
        assertThatThrownBy(() -> commentService.patchCommentById(1L, 1L, req))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 댓글을_삭제한다() {
        // given
        Comment saved = commentRepository.save(댓글_생성());

        // when & then
        assertDoesNotThrow(() -> commentService.deleteCommentById(1L, saved.getId()));
    }

    @Test
    void 글쓴이가_아니라면_댓글을_삭제하지_못한다() {
        // given
        Comment saved = commentRepository.save(댓글_생성());

        // when & then
        assertThatThrownBy(() -> commentService.deleteCommentById(2L, 1L))
                .isInstanceOf(CommentWriterNotEqualsException.class);
    }

    @Test
    void 댓글이_없다면_댓글을_삭제하지_못한다() {
        // when & then
        assertThatThrownBy(() -> commentService.deleteCommentById(1L, 1L))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 댓글을_게시글_id로_모두_제거한다() {
        // given
        Comment saved = commentRepository.save(댓글_생성());

        // when
        assertDoesNotThrow(() -> commentService.deleteAllCommentsByBoardId(saved.getBoardId()));

        // then
        List<Comment> result = commentService.findAllCommentsByBoardId(saved.getBoardId());
        assertThat(result).isEmpty();
    }
}
