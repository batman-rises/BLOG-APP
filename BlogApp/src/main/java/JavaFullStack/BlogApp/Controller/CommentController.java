package JavaFullStack.BlogApp.Controller;

import JavaFullStack.BlogApp.DTO.Request.CommentRequest;
import JavaFullStack.BlogApp.DTO.Response.CommentResponse;
import JavaFullStack.BlogApp.Entity.User;
import JavaFullStack.BlogApp.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    // UserRepository removed — not needed anymore

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> add(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(commentService.addComment(postId, request, currentUser));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        commentService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}