package JavaFullStack.BlogApp.Entity;

import JavaFullStack.BlogApp.Enums.PostStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="posts")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(unique=true,nullable=false)
    private String slug;

    @Column(columnDefinition = "TEXT",nullable=false)
    private String content;

    @Enumerated(EnumType.STRING)
    private PostStatus status=PostStatus.DRAFT;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="authorId")
    private User author;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments=new ArrayList<>();

    // Many posts ↔ Many tags (creates a post_tags join table automatically)
    @ManyToMany
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Fires automatically before every update query
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
