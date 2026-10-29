package com.branch_master.ecovolt360.faq.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import  lombok.NoArgsConstructor;
import  java.util.UUID;

@Entity
@Table(name="faqs")
@Getter
@Setter
@NoArgsConstructor
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="title")
    private String title;

    @Column(name="answer")
    private String answer;

    @Column(name="category")
    private String category;

    public Faq(UUID id, String title, String answer, String category) {
        this.id = id;
        this.title = title;
        this.answer = answer;
        this.category = category;
    }

}
