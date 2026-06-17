package com.example.PCOnlineShop.model.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table (name = "category")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Category {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column (name = "category_id")
    private int categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category parent;

    @Column (name = "category_name")
    private String categoryName;

    @Column (name = "description")
    private String description;

    @Column (name = "display_order")
    private int displayOrder;

    @Column (name = "created_at")
    private Date createdAt;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Product> products;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Category> children;
}
