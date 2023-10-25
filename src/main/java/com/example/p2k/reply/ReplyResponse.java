package com.example.p2k.reply;

import com.example.p2k._core.util.PageData;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReplyResponse {

    @Getter
    public static class FindRepliesDTO {

        private final PageData pageData;
        private final List<ReplyDTO> replies;

        public FindRepliesDTO(Page<Reply> replies, int size) {
            this.pageData = new PageData(
                    replies.hasPrevious(),
                    replies.hasNext(),
                    replies.isEmpty(),
                    replies.getNumber(),
                    replies.getTotalPages(),
                    size
            );
            this.replies = replies.getContent().stream().map(ReplyDTO::new).toList();
        }

        @Getter
        public class ReplyDTO {
            private final Long id;
            private final String content;
            private final String author;
            private final String createdDate;
            private final Long step;
            private final Long userId;

            public ReplyDTO(Reply reply) {
                this.id = reply.getId();
                this.content = reply.getContent();
                this.author = reply.getAuthor();
                this.createdDate = reply.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                this.step = reply.getStep();
                this.userId = reply.getUser() == null? null : reply.getUser().getId();
            }
        }
    }
}
