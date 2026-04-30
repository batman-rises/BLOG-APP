package JavaFullStack.BlogApp.Controller;

import JavaFullStack.BlogApp.DTO.Request.PostRequest;
import JavaFullStack.BlogApp.DTO.Response.PostResponse;
import JavaFullStack.BlogApp.Entity.User;
import JavaFullStack.BlogApp.Repository.UserRepository;
import JavaFullStack.BlogApp.Service.PostService;
import JavaFullStack.BlogApp.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;

    // Public — anyone can get all published posts
    // optional ?tag=java filter
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAll(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size,
            @RequestParam(required = false)String tag
    ){
        return ResponseEntity.ok(postService.getAllPublished(page,size,tag));
    }

    // Public — get single post by slug
    @GetMapping("/{slug}")
    public ResponseEntity<PostResponse> getBySlug(@PathVariable String slug){
        return ResponseEntity.ok(postService.getBySlug(slug));
    }
    // For now we pass userId in header manually — security will replace this later
    @PutMapping
    public ResponseEntity<PostResponse>  update(
            @PathVariable Long id,
            @RequestBody PostRequest request,
            @RequestHeader("X-User-Id") Long userId
            ){
        User currentUser=userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));

        return ResponseEntity.ok(postService.update(id,request,currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestBody PostRequest request,
            @RequestHeader("X-User-Id") Long userId
    ){
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        postService.delete(id, currentUser);
        return ResponseEntity.noContent().build(); // ye waala note kar
    }
    // Get logged in user's own posts
    @GetMapping("/my")
    public ResponseEntity<Page<PostResponse>> myPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-User-Id") Long userId) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(postService.getMyPosts(author, page, size));
    }

}
