package com.likelion.friendpass.domain.chat;

import com.likelion.friendpass.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "chat_room_member",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_member_room_user", columnNames = {"chat_room_id", "user_id"})
        },
        indexes = {
                @Index(name = "ix_member_team", columnList = "team_id"),
                @Index(name = "ix_member_user", columnList = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_member_room"))
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_member_user"))
    private User user;

    @Column(name = "is_current")
    private Boolean isCurrent;   // 현재 매칭 여부 (NULL 허용)

    @Column(name = "team_id", nullable = false)
    private Long teamId;
}
