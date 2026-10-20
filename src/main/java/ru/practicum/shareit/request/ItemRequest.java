package ru.practicum.shareit.request;

import jakarta.persistence.*;
import ru.practicum.shareit.user.model.User;

@Entity
@Table(name = "items_request")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor")
    private User userId;
}
