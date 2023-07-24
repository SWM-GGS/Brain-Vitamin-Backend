package ggs.brainvitamin.src.post.entity;

import ggs.brainvitamin.config.BaseEntity;
import ggs.brainvitamin.config.CommonCodeDetailEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "TAG_COUNT")
@NoArgsConstructor
public class TagCountEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "popular_tag1")
    private String popularTagName1;

    @Column(nullable = false, name = "popular_tag2")
    private String popularTagName2;

    @Column(nullable = false, name = "popular_tag3")
    private String popularTagName3;

    @Column(nullable = false, name = "popular_tag4")
    private String popularTagName4;

    @Column(nullable = false, name = "popular_tag5")
    private String popularTagName5;

    @Column(nullable = false, name = "popular_tag6")
    private String popularTagName6;

    @Column(nullable = false, name = "popular_tag7")
    private String popularTagName7;

    @Column(nullable = false, name = "popular_tag8")
    private String popularTagName8;

    @Column(nullable = false, name = "popular_tag9")
    private String popularTagName9;

    @Column(nullable = false, name = "popular_tag10")
    private String popularTagName1p;
}
