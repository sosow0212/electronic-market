package com.market.board.infrastructure.board;

import com.market.board.domain.board.Board;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardJpaRepository extends JpaRepository<Board, Long> {

    Board save(final Board board);

    Optional<Board> findById(final Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Board b where b.id = :id")
    Optional<Board> findByIdUsingPessimistic(@Param("id") final Long id);

    @Modifying
    @Query("update Board b set b.likeCount.likeCount = b.likeCount.likeCount + 1 where b.id = :id")
    void updateLikeCountUsingQueries(@Param("id") final Long id);

    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.images WHERE b.id = :boardId")
    Optional<Board> findBoardWithImages(@Param("boardId") Long boardId);

    void deleteById(final Long id);
}
