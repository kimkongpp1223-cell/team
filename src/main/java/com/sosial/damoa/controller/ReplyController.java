package com.sosial.damoa.controller;

import com.sosial.damoa.entity.Inquiry;
import com.sosial.damoa.entity.Reply;
import com.sosial.damoa.repository.InquiryRepository;
import com.sosial.damoa.repository.ReplyRepository;
import com.sosial.damoa.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/replies")
@RequiredArgsConstructor
@CrossOrigin
public class ReplyController {

    private final ReplyRepository replyRepository;
    private final InquiryRepository inquiryRepository;
    private final EmailService emailService;

    @PostMapping("/{inquiryId}")
    public Reply createReply(@PathVariable Long inquiryId, @RequestBody Reply reply) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("문의 없음"));

        reply.setInquiryId(inquiryId);
        Reply savedReply = replyRepository.save(reply);

        try {
            emailService.sendReplyToUser(
                    inquiry.getEmail(),
                    inquiry.getTitle(),
                    reply.getContent()
            );
        } catch (Exception e) {
            System.out.println("메일 발송 실패 -> " + e.getMessage());
        }

        return savedReply;
    }

    @GetMapping("/{inquiryId}")
    public List<Reply> getReplies(@PathVariable Long inquiryId) {
        return replyRepository.findByInquiryId(inquiryId);
    }

    @PutMapping("/edit/{replyId}")
    public Reply updateReply(@PathVariable Long replyId, @RequestBody Reply req) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("답변 없음"));

        reply.setContent(req.getContent());
        return replyRepository.save(reply);
    }

    @DeleteMapping("/{replyId}")
    public Map<String, String> deleteReply(@PathVariable Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("답변 없음"));

        replyRepository.delete(reply);

        return Map.of("result", "ok");
    }
}