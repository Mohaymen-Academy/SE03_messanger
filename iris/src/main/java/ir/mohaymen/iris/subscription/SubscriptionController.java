package ir.mohaymen.iris.subscription;

import ir.mohaymen.iris.chat.Chat;
import ir.mohaymen.iris.chat.ChatService;
import ir.mohaymen.iris.chat.ChatType;
import ir.mohaymen.iris.chat.GetChatDto;
import ir.mohaymen.iris.contact.ContactService;
import ir.mohaymen.iris.permission.Permission;
import ir.mohaymen.iris.permission.PermissionService;
import ir.mohaymen.iris.profile.UserProfile;
import ir.mohaymen.iris.user.UserService;
import ir.mohaymen.iris.utility.BaseController;
import ir.mohaymen.iris.utility.Nameable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/subs")
@RequiredArgsConstructor
public class SubscriptionController extends BaseController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final ChatService chatService;
    private final ModelMapper modelMapper;
    private final ContactService contactService;
    private final PermissionService permissionService;

    @PostMapping("/add-user-to-chat")
    public ResponseEntity<GetChatDto> addToChat(@RequestBody @Valid AddSubDto addSubDto) {
        var user = getUserByToken();
        if (!permissionService.hasAccess(user.getUserId(), addSubDto.getChatId(), Permission.ADD_USER))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        Chat chat = chatService.getById(addSubDto.getChatId());

        if (addSubDto.getUserIds().size() != 1 && chat.getChatType() == ChatType.PV)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        for (Long id : addSubDto.getUserIds()) {
            if (chat.getMessages() == null || chat.getMessages().size() == 0)
                subscriptionService.createOrUpdate(new Subscription(null, userService.getById(id),
                        chat, 0L, Permission.getDefaultPermissions(chat.getChatType())));
            else
                subscriptionService.createOrUpdate(new Subscription(null, userService.getById(id),
                        chat, chat.getMessages().get(chat.getMessages().size() - 1).getMessageId(),
                        Permission.getDefaultPermissions(chat.getChatType())));
        }

        GetChatDto getChatDto = modelMapper.map(chat, GetChatDto.class);
        getChatDto.setSubCount(chat.getSubs().size());
        return new ResponseEntity<>(getChatDto, HttpStatus.OK);
    }

    @PutMapping("/set-last-seen/{chatId}/{messageId}")
    public ResponseEntity<?> setLastSeen(@PathVariable Long chatId, @PathVariable Long messageId) {
        Chat chat = chatService.getById(chatId);
        Subscription subscription = null;
        for (Subscription sub : chat.getSubs()) {
            if (Objects.equals(sub.getUser().getUserId(), getUserByToken().getUserId())) {
                subscription = sub;
                break;
            }
        }
        if (subscription == null)
            throw new EntityNotFoundException();
        subscription.setLastMessageSeenId(messageId);
        subscriptionService.createOrUpdate(subscription);
        return ResponseEntity.ok("... YoU hAvE SeEn ThE tRuTh ...");
    }

    @GetMapping("/chat-subs/{id}")
    public ResponseEntity<List<SubDto>> subsOfOneChat(@PathVariable Long id) {
        List<SubDto> subDtoList = new ArrayList<>();
        for (Subscription subscription : subscriptionService.getAllSubscriptionByChatId(id)) {
            SubDto subDto = new SubDto();
            // contact
            Nameable nameable = subscriptionService.setName(contactService.getContactByFirstUser(getUserByToken()),
                    subscription.getUser());
            subDto.setFirstName(nameable.getFirstName());
            subDto.setLastName(nameable.getLastName());
            subDto.setUserId(subscription.getUser().getUserId());
            List<UserProfile> profiles = subscription.getUser().getProfiles();
            if (!profiles.isEmpty())
                subDto.setProfile(profiles.get(profiles.size() - 1).getMedia());
            subDtoList.add(subDto);
        }
        return new ResponseEntity<>(subDtoList, HttpStatus.OK);
    }
}
