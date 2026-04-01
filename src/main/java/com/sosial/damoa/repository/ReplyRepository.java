package com.sosial.damoa.repository;

import com.sosial.damoa.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByInquiryId(Long inquiryId);

    @Modifying
    @Transactional
    void deleteByInquiryId(Long inquiryId);
}