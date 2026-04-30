package JavaFullStack.BlogApp.Controller;

import JavaFullStack.BlogApp.DTO.Request.CommentRequest;
import JavaFullStack.BlogApp.DTO.Response.CommentResponse;
import JavaFullStack.BlogApp.Entity.User;
import JavaFullStack.BlogApp.Repository.UserRepository;
import JavaFullStack.BlogApp.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final UserRepository userRepository;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> add(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            @RequestHeader("X-User-Id") Long userId
    ){
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(commentService.addComment(postId, request, author));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        commentService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
