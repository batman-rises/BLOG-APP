package JavaFullStack.BlogApp.Controller;

import JavaFullStack.BlogApp.DTO.Request.PostRequest;
import JavaFullStack.BlogApp.DTO.Response.PostResponse;
import JavaFullStack.BlogApp.Entity.User;
import JavaFullStack.BlogApp.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    // UserRepository removed — not needed anymore
    // @AuthenticationPrincipal directly gives us the User object from SecurityContext

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tag) {
        return ResponseEntity.ok(postService.getAllPublished(page, size, tag));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(postService.getBySlug(slug));
    }

    // @AuthenticationPrincipal User currentUser
    // Spring reads the User object directly from SecurityContext
    // No need to fetch from DB manually — JwtAuthFilter already did that
    @PostMapping
    public ResponseEntity<PostResponse> create(
            @RequestBody PostRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.create(request, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(
            @PathVariable Long id,
            @RequestBody PostRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.update(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        postService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<Page<PostResponse>> myPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(postService.getMyPosts(currentUser, page, size));
    }
}