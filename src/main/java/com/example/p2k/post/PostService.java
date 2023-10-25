package com.example.p2k.post;

import com.example.p2k._core.exception.Exception403;
import com.example.p2k._core.exception.Exception404;
import com.example.p2k.course.Course;
import com.example.p2k.course.CourseRepository;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import com.example.p2k.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostService {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGINATION_SIZE = 5;

    private final PostRepository postRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    //강좌 카테고리별로 게시글 찾기
    public PostResponse.FindPostsDTO findPostsByCategory(Long courseId, Category category, Long userId, int page){
        List<Sort.Order> sorts = Collections.singletonList(Sort.Order.desc("createdDate"));
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(sorts));
        Page<Post> posts = postRepository.findByCourseIdAndCategoryAndUserIdOrScope(pageable, courseId, category, userId, Scope.PUBLIC);
        return new PostResponse.FindPostsDTO(posts, DEFAULT_PAGINATION_SIZE);
    }

    //게시글 아이디로 게시글 찾기
    public PostResponse.FindPostByIdDTO findPostById(Long postId){
        Post post = getPost(postId);
        return new PostResponse.FindPostByIdDTO(post);
    }

    //게시글 작성하기
    @Transactional
    public void savePost(PostRequest.SaveDTO saveDTO, Category category, Long userId, Long courseId){
        Course course = getCourse(courseId);
        User user = getUser(userId);

        validateNoticePostCreate(category, user);

        Scope scope = (category == Category.NOTICE) ? Scope.PUBLIC : saveDTO.getScope();
        Post post = Post.builder()
                .title(saveDTO.getTitle())
                .content(saveDTO.getContent())
                .scope(scope)
                .author(user.getName())
                .category(category)
                .course(course)
                .user(user)
                .build();

        postRepository.save(post);
    }

    //게시글 수정하기
    @Transactional
    public void updatePost(PostRequest.UpdateDTO updateDTO, Category category, Long postId, User user){
        Post post = getPost(postId);
        validatePostUpdate(user, post);
        Scope scope = (category == Category.NOTICE) ? Scope.PUBLIC : updateDTO.getScope();
        post.updatePost(updateDTO.getTitle(), updateDTO.getContent(), scope);
    }

    //게시글 삭제하기
    @Transactional
    public void deletePost(Long postId, User user){
        Post post = getPost(postId);
        validatePostUpdate(user, post);
        postRepository.deleteById(postId);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new Exception404("해당 게시글을 찾을 수 없습니다.")
        );
    }

    private Course getCourse(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow(
                () -> new Exception404("해당 강좌를 찾을 수 없습니다.")
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );
    }

    private static void validateNoticePostCreate(Category category, User user) {
        if(category == Category.NOTICE && isNoticePostForbiddenUser(user)){
            throw new Exception403("공지사항 게시글을 작성할 권한이 없습니다.");
        }
    }

    private static boolean isNoticePostForbiddenUser(User user) {
        return !(user.getRole() == Role.ROLE_INSTRUCTOR || user.getRole() == Role.ROLE_ADMIN);
    }

    private static void validatePostUpdate(User user, Post post) {
        if(post.getUser().getId() != user.getId()){
            throw new Exception403("게시글을 수정할 권한이 없습니다.");
        }
    }
}