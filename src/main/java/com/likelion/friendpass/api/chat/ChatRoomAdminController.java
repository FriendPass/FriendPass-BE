package com.likelion.friendpass.api.chat;

import com.likelion.friendpass.api.chat.dto.RenameRoomReq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomAdminController {

    private final ChatRoomCommandService commandService;

    @PatchMapping("/rooms/{roomId}/name")
    public void rename(
            @PathVariable Long roomId,
            @AuthenticationPrincipal Long userId,
            @RequestBody RenameRoomReq req
    ) {
        commandService.rename(roomId, userId, req.getRoomName());
    }
}
