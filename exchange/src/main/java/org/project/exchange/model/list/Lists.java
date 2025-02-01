package org.project.exchange.model.list;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.exchange.model.product.Product;
import org.project.exchange.model.user.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "lists") // 리스트 테이블
public class Lists {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id", nullable = false) // 리스트 ID
    private Long listId;

    @Column(name = "name", nullable = false, length = 100) // 리스트 이름
    private String name;

    @OneToMany(mappedBy = "lists", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products; // 해당 리스트의 상품들

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // 사용자 ID
    private User user;

    @Builder
    public Lists(String name, User user) {
        this.name = name;
        this.products = new ArrayList<>();
        this.user = user;
    }
}
