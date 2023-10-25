package com.example.p2k.post;

import com.example.p2k._core.util.PageData;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public class PostResponse {

    @Getter
    public static class FindPostsDTO {

        private final PageData pageData;
        private final List<PostDTO> posts;

        public FindPostsDTO(Page<Post> posts, int size) {
            this.pageData = new PageData(
                    posts.hasPrevious(),
                    posts.hasNext(),
                    posts.isEmpty(),
                    posts.getNumber(),
                    posts.getTotalPages(),
                    size
            );
            this.posts = posts.getContent().stream().map(PostDTO::new).toList();
        }

        @Getter
        public class PostDTO{
            private final Long id;
            private final String title;
            private final String author;
            private final String content;
            private final LocalDate createdDate;
            private final Scope scope;

            public PostDTO(Post post) {
                this.id = post.getId();
                this.title = post.getTitle();
                this.author = post.getAuthor();
                this.content = post.getContent();
                this.createdDate = post.getCreatedDate() != null ? post.getCreatedDate().toLocalDate() : null;
                this.scope = post.getScope();
            }
        }
    }

    @Getter
    public static class FindPostByIdDTO{

        private final Long id;
        private final String title;
        private final String author;
        private final String content;
        private final LocalDate createdDate;
        private final Long userId;
        private final Scope scope;

        public FindPostByIdDTO(Post post) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.author = post.getAuthor();
            this.content = post.getContent();
            this.createdDate = post.getCreatedDate() != null ? post.getCreatedDate().toLocalDate() : null;
            this.userId = post.getUser().getId();
            this.scope = post.getScope();
        }
    }
}
