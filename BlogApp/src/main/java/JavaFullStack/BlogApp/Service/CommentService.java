package JavaFullStack.BlogApp.Service;

import JavaFullStack.BlogApp.DTO.Request.CommentRequest;
import JavaFullStack.BlogApp.DTO.Response.CommentResponse;
import JavaFullStack.BlogApp.Entity.Comment;
import JavaFullStack.BlogApp.Entity.Post;
import JavaFullStack.BlogApp.Entity.User;
import JavaFullStack.BlogApp.Repository.CommentRepository;
import JavaFullStack.BlogApp.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // -------------------------------------------------------
    // ADD COMMENT — finds the post, builds comment, saves it
    // -------------------------------------------------------
    public CommentResponse addComment(Long postId, CommentRequest request, User author) {

        // First find the post this comment belongs to
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Build the comment entity
        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .author(author)
                .build();

        // Save and immediately map to response DTO
        Comment saved = commentRepository.save(comment);

        return CommentResponse.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .authorUsername(saved.getAuthor().getUsername())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // -------------------------------------------------------
    // DELETE COMMENT — only the comment author can delete it
    // -------------------------------------------------------
    public void delete(Long commentId, User currentUser) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Ownership check
        if (!comment.getAuthor().getId().equals(currentUser.getId()))
            throw new RuntimeException("You can only delete your own comments");

        commentRepository.delete(comment);
    }
}