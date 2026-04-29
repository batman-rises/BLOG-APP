package JavaFullStack.BlogApp.Service;

import JavaFullStack.BlogApp.DTO.Request.PostRequest;
import JavaFullStack.BlogApp.DTO.Response.PostResponse;
import JavaFullStack.BlogApp.Entity.Post;
import JavaFullStack.BlogApp.Entity.Tag;
import JavaFullStack.BlogApp.Entity.User;
import JavaFullStack.BlogApp.Enums.PostStatus;
import JavaFullStack.BlogApp.Repository.PostRepository;
import JavaFullStack.BlogApp.Repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    private final TagRepository tagRepository;

    // -------------------------------------------------------
    // HELPER 1 — converts "My First Post" → "my-first-post"
    // If that slug already exists, tries "my-first-post-1" etc.
    // -------------------------------------------------------
    private String generateSlug(String title) {
        String base = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
        String slug = base;
        int count = 1;
        while (postRepository.existsBySlug(slug)) {
            slug = base + "-" + count++;
        }
        return slug;
    }

    // HELPER 2 — takes Set<String> like ["java", "spring"]
    // finds existing tags in DB, creates new ones if not found

    private Set<Tag> resolveTags (Set<String> tagNames){
        if(tagNames == null)
            return new HashSet<>();

        return tagNames.stream().map(name -> tagRepository.findByName(name)
                .orElseGet(()->tagRepository.save(
                        Tag.builder().name(name).build()
                ))
        ).collect(Collectors.toSet());
    }

    // HELPER 3 — converts Post entity → PostResponse DTO ; jo neeche use hoga
    private PostResponse toResponse(Post post){
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .status(post.getStatus().name())
                .authorUsername(post.getAuthor().getUsername())
                .tags(post.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toSet()))
                .commentCount(post.getComments().size())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    // CREATE
    public PostResponse create(PostRequest request, User author) {
        Post post = Post.builder()
                .title(request.getTitle())
                .slug(generateSlug(request.getTitle()))
                .content(request.getContent())
                .status(request.getStatus() != null
                        ? PostStatus.valueOf(request.getStatus())
                        : PostStatus.DRAFT)
                .author(author)
                .tags(resolveTags(request.getTags()))
                .build();

        return toResponse(postRepository.save(post));
    }

    // GET ALL PUBLISHED — with pagination + optional tag filter
    public Page<PostResponse> getAllPublished(int page, int size, String tag) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (tag != null && !tag.isBlank()) {
            return postRepository
                    .findByTagsNameAndStatus(tag, PostStatus.PUBLISHED, pageable)
                    .map(this::toResponse);
        }

        return postRepository
                .findByStatus(PostStatus.PUBLISHED, pageable)
                .map(this::toResponse);
    }

    // GET SINGLE POST BY SLUG
    public PostResponse getBySlug(String slug){
        Post post=postRepository.findBySlug(slug)
                .orElseThrow(()-> new RuntimeException("Post not found"));

        return toResponse(post);
    }

    // UPDATE — only the author can update their own post
    public PostResponse update(Long id, PostRequest request, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Ownership check — is this post written by the logged in user?
        if (!post.getAuthor().getId().equals(currentUser.getId()))
            throw new RuntimeException("You can only edit your own posts");

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        if (request.getStatus() != null)
            post.setStatus(PostStatus.valueOf(request.getStatus()));
        post.setTags(resolveTags(request.getTags()));

        return toResponse(postRepository.save(post));
    }

    // DELETE — only the author can delete their own post
    public void delete(Long id,User currentUser){
        Post post=postRepository.findById(id)
                .orElseThrow(()->new RuntimeException("post not found"));

        if(!post.getAuthor().getId().equals(currentUser.getId()))
            throw new RuntimeException("you can only delete your own posts");

        postRepository.delete(post);

    }

    // GET MY POSTS — returns all posts by the logged in user
    // -------------------------------------------------------
    public Page<PostResponse> getMyPosts(User author, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByAuthor(author, pageable)
                .map(this::toResponse);
    }
}
