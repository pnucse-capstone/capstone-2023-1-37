package com.example.p2k.post;

import com.example.p2k._core.security.CustomUserDetails;
import com.example.p2k.course.CourseResponse;
import com.example.p2k.course.CourseService;
import com.example.p2k.reply.ReplyResponse;
import com.example.p2k.reply.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}")
@Controller
public class PostController {

    private final CourseService courseService;
    private final PostService postService;
    private final ReplyService replyService;

    //공지사항 게시판 페이지
    @GetMapping("/notice-board")
    public String getNoticeBoard(@PathVariable Long courseId, Model model,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("postDTOs", getPostsDTO(courseId, Category.NOTICE, userDetails.getUser().getId(), page));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/noticeBoard";
    }

    //질문 게시판 페이지
    @GetMapping("/question-board")
    public String getQuestionBoard(@PathVariable Long courseId, Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("postDTOs", getPostsDTO(courseId, Category.QUESTION, userDetails.getUser().getId(), page));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/questionBoard";
    }

    //자유 게시판 페이지
    @GetMapping("/free-board")
    public String getFreeBoard(@PathVariable Long courseId, Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("postDTOs", getPostsDTO(courseId, Category.FREE, userDetails.getUser().getId(), page));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/freeBoard";
    }

    //공지사항 글 상세 조회
    @GetMapping("/notice-board/{postId}")
    public String findNoticePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("postDTO", getPostDTO(postId));
        model.addAttribute("repliesDTO", getRepliesDTO(postId, page));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/noticePost";
    }

    //공지사항 글 작성
    @PostMapping("/notice-board/save")
    public String saveNoticePost(@PathVariable Long courseId,
                                 @Valid @ModelAttribute PostRequest.SaveDTO requestDTO, Error errors,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.savePost(requestDTO, Category.NOTICE, userDetails.getUser().getId(), courseId);
        return "redirect:/courses/{courseId}/notice-board";
    }

    //공지사항 글 작성 폼
    @GetMapping("/notice-board/save")
    public String getSaveNoticePost(@PathVariable Long courseId, Model model,
                                    @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("saveDTO", new PostRequest.SaveDTO());
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/saveNoticePost";
    }

    //공지사항 글 수정
    @PostMapping("/notice-board/{postId}/update")
    public String updateNoticePost(@PathVariable Long postId,
                                   @Valid @ModelAttribute PostRequest.UpdateDTO requestDTO, Error errors,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.updatePost(requestDTO, Category.NOTICE, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/notice-board/{postId}";
    }

    //공지사항 글 수정 폼
    @GetMapping("/notice-board/{postId}/update")
    public String getUpdateNoticePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                      @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("updateDTO", getPostDTO(postId));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/updateNoticePost";
    }

    //공지사항 글 삭제
    @PostMapping("/notice-board/{postId}/delete")
    public String deleteNoticePost(@PathVariable Long postId, Model model,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("user", userDetails.getUser());
        postService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/notice-board";
    }

    //질문 글 상세 조회
    @GetMapping("/question-board/{postId}")
    public String findQuestionPost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("postDTO", getPostDTO(postId));
        model.addAttribute("repliesDTO", getRepliesDTO(postId, page));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/questionPost";
    }

    //질문 글 작성
    @PostMapping("/question-board/save")
    public String saveQuestionPost(@PathVariable Long courseId,
                                   @Valid @ModelAttribute PostRequest.SaveDTO requestDTO, Error errors,
                                   @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.savePost(requestDTO, Category.QUESTION, userDetails.getUser().getId(), courseId);
        return "redirect:/courses/{courseId}/question-board";
    }

    //질문 글 작성 폼
    @GetMapping("/question-board/save")
    public String getSaveQuestionPost(@PathVariable Long courseId, Model model,
                                      @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("saveDTO", new PostRequest.SaveDTO());
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/saveQuestionPost";
    }

    //질문 글 수정
    @PostMapping("/question-board/{postId}/update")
    public String updateQuestionPost(@PathVariable Long postId,
                                     @Valid @ModelAttribute PostRequest.UpdateDTO requestDTO, Error errors,
                                     @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.updatePost(requestDTO, Category.QUESTION, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/question-board/{postId}";
    }

    //질문 글 수정 폼
    @GetMapping("/question-board/{postId}/update")
    public String getUpdateQuestionPost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                        @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("updateDTO", getPostDTO(postId));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/updateQuestionPost";
    }

    //질문 글 삭제
    @PostMapping("/question-board/{postId}/delete")
    public String deleteQuestionPost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/question-board";
    }

    //자유 글 상세 조회
    @GetMapping("/free-board/{postId}")
    public String findFreePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("postDTO", getPostDTO(postId));
        model.addAttribute("repliesDTO", getRepliesDTO(postId, page));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/freePost";
    }

    //자유 글 작성
    @PostMapping("/free-board/save")
    public String saveFreePost(@PathVariable Long courseId,
                               @Valid @ModelAttribute PostRequest.SaveDTO requestDTO, Error errors,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.savePost(requestDTO, Category.FREE, userDetails.getUser().getId(), courseId);
        return "redirect:/courses/{courseId}/free-board";
    }

    //자유 글 작성 폼
    @GetMapping("/free-board/save")
    public String getSaveFreePost(@PathVariable Long courseId, Model model,
                                  @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("saveDTO", new PostRequest.SaveDTO());
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/saveFreePost";
    }

    //자유 글 수정
    @PostMapping("/free-board/{postId}/update")
    public String updateFreePost(@PathVariable Long postId,
                                 @Valid @ModelAttribute PostRequest.UpdateDTO requestDTO, Error errors,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.updatePost(requestDTO, Category.FREE, postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/free-board/{postId}";
    }

    //자유 글 수정 폼
    @GetMapping("/free-board/{postId}/update")
    public String getUpdateFreePost(@PathVariable Long courseId, @PathVariable Long postId, Model model,
                                    @AuthenticationPrincipal CustomUserDetails userDetails){
        model.addAttribute("updateDTO", getPostDTO(postId));
        model.addAttribute("courseDTO", getCourseDTO(courseId));
        model.addAttribute("user", userDetails.getUser());
        return "course/updateFreePost";
    }

    //자유 글 삭제
    @PostMapping("/free-board/{postId}/delete")
    public String deleteFreePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.deletePost(postId, userDetails.getUser());
        return "redirect:/courses/{courseId}/free-board";
    }

    @ModelAttribute("scopes")
    public Scope[] scopes() {
        return Scope.values();
    }

    private ReplyResponse.FindRepliesDTO getRepliesDTO(Long postId, int page) {
        return replyService.findByPostId(postId, page);
    }

    private PostResponse.FindPostsDTO getPostsDTO(Long courseId, Category category, Long userId, int page) {
        return postService.findPostsByCategory(courseId, category, userId, page);
    }

    private PostResponse.FindPostByIdDTO getPostDTO(Long postId) {
        return postService.findPostById(postId);
    }

    private CourseResponse.FindById getCourseDTO(Long courseId) {
        return courseService.findCourse(courseId);
    }
}
