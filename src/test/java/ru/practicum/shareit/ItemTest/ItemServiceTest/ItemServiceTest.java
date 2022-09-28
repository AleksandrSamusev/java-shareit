package ru.practicum.shareit.ItemTest.ItemServiceTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestDto;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class ItemServiceTest<T extends ItemService> {

    private final EntityManager em;

    private final RequestService requestService;
    private final UserService userService;
    private final ItemService itemService;
    private final CommentRepository commentRepository;

    @Test
    public void createItemWithoutRequestTest() {

        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Item item = new Item();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, null, ItemMapper.toItemDto(item));

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.owner.name = :name", Item.class);

        Item itemFromDb = query.setParameter("name", user.getName()).getSingleResult();

        assertThat(itemFromDb.getId(), notNullValue());
        assertThat(itemFromDb.getName(), equalTo(item.getName()));
        assertThat(itemFromDb.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    public void createItemWithoutRequestWithBadItemNameTest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Item item = new Item();
        item.setIsAvailable(true);
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");

        assertThrows(InvalidParameterException.class,
                () -> itemService.createItem(1L, null, ItemMapper.toItemDto(item)));

    }

    @Test
    public void createItemWithoutRequestWithBadItemDescriptionTest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Item item = new Item();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);

        assertThrows(InvalidParameterException.class,
                () -> itemService.createItem(1L, null, ItemMapper.toItemDto(item)));

    }

    @Test
    public void createItemWithoutRequestWithBadItemIsAvailableParameterTest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Item item = new Item();
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");

        assertThrows(InvalidParameterException.class,
                () -> itemService.createItem(1L, null, ItemMapper.toItemDto(item)));

    }

    @Test
    public void createItemWithRequestTest() {

        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        Request request = new Request();
        request.setRequestor(user);
        request.setDescription("Screwdriver needed");
        request.setCreated(LocalDateTime.now());
        requestService.createRequest(1L, RequestMapper.toRequestDto(request));

        Item item = new Item();
        item.setRequestId(1L);
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, 1L, ItemMapper.toItemDto(item));

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.owner.name = :name", Item.class);
        Item itemFromDb = query.setParameter("name", user.getName()).getSingleResult();

        assertThat(itemFromDb.getId(), notNullValue());
        assertThat(itemFromDb.getName(), equalTo(item.getName()));
        assertThat(itemFromDb.getDescription(), equalTo(item.getDescription()));
        assertThrows(RequestNotFoundException.class,
                () -> itemService.createItem(1L, 999L, ItemMapper.toItemDto(item)));
    }

    @Test
    public void updateItemTest() {
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(user);

        ItemDto item = new ItemDto();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, null, item);

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(1L);
        updatedItem.setIsAvailable(true);
        updatedItem.setName("Screwdriver with changed name");
        updatedItem.setOwner(user);
        updatedItem.setDescription("This is the best screwdriver in the world!");
        itemService.updateItem(updatedItem);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.owner.name = :name", Item.class);

        Item itemFromDb = query.setParameter("name", user.getName()).getSingleResult();

        assertThat(itemFromDb.getId(), notNullValue());
        assertThat(itemFromDb.getName(), equalTo(updatedItem.getName()));
        assertThat(itemFromDb.getRequestId(), equalTo(null));
    }

    @Test
    public void findItemByIdTest() {
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(user);

        UserDto user2 = new UserDto();
        user2.setName("user2");
        user2.setEmail("user2@user.ru");
        userService.createUser(user2);

        ItemDto item = new ItemDto();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        item.setComments(new HashSet<>());
        itemService.createItem(1L, null, item);

        ItemDto itemResponce = itemService.findItemById(1L, 1L);

        assertThat(itemResponce.getId(), notNullValue());
        assertThat(itemResponce.getName(), equalTo("Screwdriver"));
        assertThat(itemResponce.getOwner().getName(), equalTo(user.getName()));
        var ex = assertThrows(ItemNotFoundException.class,
                () -> itemService.findItemById(1L, 99L));
        assertThat(ex.getMessage(), equalTo("Item not found"));
        assertThat(commentRepository.findAllItemComments(1L), equalTo(new HashSet<>()));

    }

    @Test
    public void findAllItemsByOwnerTest() {
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(user);

        ItemDto item = new ItemDto();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, null, item);

        ItemDto item2 = new ItemDto();
        item2.setIsAvailable(true);
        item2.setName("Wrench");
        item2.setOwner(user);
        item2.setDescription("Super wrench!");
        itemService.createItem(1L, null, item2);

        List<ItemDto> listOfItems = itemService.findAllItemsByOwner(1L);

        assertThat(listOfItems.size(), equalTo(2));
        assertThat(listOfItems.get(0).getId(), equalTo(1L));
        assertThat(listOfItems.get(1).getId(), equalTo(2L));


    }

    @Test
    public void getAllItemsByStringTest() {
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(user);

        ItemDto item = new ItemDto();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, null, item);

        ItemDto item2 = new ItemDto();
        item2.setIsAvailable(true);
        item2.setName("Wrench");
        item2.setOwner(user);
        item2.setDescription("Super wrench!");
        itemService.createItem(1L, null, item2);

        List<ItemDto> listOfItemsByDescription = itemService.getAllItemsByString("best");
        assertThat(listOfItemsByDescription.size(), equalTo(1));
        assertThat(listOfItemsByDescription.get(0).getName(), equalTo("Screwdriver"));

        List<ItemDto> listOfItemsByName = itemService.getAllItemsByString("Wrench");
        assertThat(listOfItemsByName.size(), equalTo(1));
        assertThat(listOfItemsByName.get(0).getName(), equalTo("Wrench"));
    }

    @Test
    public void patchItemTest() {
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(user);

        ItemDto item = new ItemDto();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");
        itemService.createItem(1L, null, item);


        ItemDto patchedItem = new ItemDto();
        patchedItem.setId(1L);
        patchedItem.setIsAvailable(true);
        patchedItem.setName("Bad screwdriver");
        patchedItem.setOwner(user);
        patchedItem.setDescription("Baaad!");

        ItemDto testItem = itemService.patchItem(patchedItem, 1L, 1L);
        assertThat(patchedItem.getName(), equalTo(testItem.getName()));
        assertThat(patchedItem.getDescription(), equalTo(testItem.getDescription()));

        assertThrows(ItemNotFoundException.class,
                () -> itemService.patchItem(patchedItem, 1L, 2L));
    }

    @Test
    public void createCommentTest() {

        User user = new User();
        user.setName("user");
        user.setEmail("user@user.ru");
        userService.createUser(UserMapper.toUserDto(user));

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@user.ru");
        userService.createUser(UserMapper.toUserDto(user2));

        Item item = new Item();
        item.setIsAvailable(true);
        item.setName("Screwdriver");
        item.setOwner(user);
        item.setDescription("This is the best screwdriver in the world!");

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemService.createItem(1L, null, itemDto);

        Comment comment = new Comment();

        comment.setText("super");
        comment.setAuthor(user2);
        comment.setItem(ItemMapper.toItem(itemDto));
        comment.setCreated(LocalDateTime.now());
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        itemService.postComment(2L, 1L, commentDto);
        assertThat(itemService.findItemById(1L, 1L).getComments().size(), equalTo(0));

        assertThrows(UserNotFoundException.class,
                () -> itemService.postComment(999L, 1L, commentDto));

    }

    @Test
    public void testMappingToComment() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.ru");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@user2.ru");

        RequestDto requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setRequestor(user2);
        requestDto.setDescription("need something");
        requestDto.setCreated(LocalDateTime.now());

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setOwner(user);
        itemDto.setName("screwdriver");
        itemDto.setDescription("best item ever");
        itemDto.setIsAvailable(true);
        itemDto.setRequestId(1L);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("good");
        commentDto.setItem(itemDto);
        commentDto.setAuthor(user2);
        commentDto.setAuthorName("user2");

        Comment comment = CommentMapper.toComment(commentDto);

        assertThat(CommentMapper.toComment(commentDto).getClass(), equalTo(Comment.class));
        assertThat(CommentMapper.toComment(commentDto).getAuthor().getName(), equalTo(user2.getName()));
        assertThat(CommentMapper.toComment(commentDto).getItem().getRequestId(), equalTo(1L));
    }


    @Test
    public void testMappingToCommentDto() {
        User user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@user2.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("screwdriver");
        item.setDescription("best item ever");
        item.setIsAvailable(true);
        item.setRequestId(1L);

        Request request = new Request();
        request.setId(1L);
        request.setRequestor(user2);
        request.setDescription("need something");
        request.setCreated(LocalDateTime.now());


        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("good");
        comment.setItem(item);
        comment.setAuthor(user2);

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertThat(commentDto.getClass(), equalTo(CommentDto.class));
        assertThat(commentDto.getText(), equalTo(comment.getText()));
        assertThat(commentDto.getItem().getRequestId(), equalTo(1L));
    }


    @Test
    public void testToCommentDtos() {

        User user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@user2.ru");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setName("screwdriver");
        item.setDescription("best item ever");
        item.setIsAvailable(true);
        item.setRequestId(2L);


        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(user2);
        item2.setName("screwdriver2");
        item2.setDescription("best item ever 2");
        item2.setIsAvailable(true);
        item2.setRequestId(1L);

        Request request = new Request();
        request.setId(1L);
        request.setRequestor(user);
        request.setDescription("need something");
        request.setCreated(LocalDateTime.now());

        Request request2 = new Request();
        request2.setId(2L);
        request2.setRequestor(user2);
        request2.setDescription("need something too");
        request2.setCreated(LocalDateTime.now());

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("good");
        comment.setItem(item);
        comment.setAuthor(user2);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("very good");
        comment2.setItem(item2);
        comment2.setAuthor(user);

        Set<Comment> comments = new HashSet<>();
        comments.add(comment);
        comments.add(comment2);

        Set<CommentDto> commentDtos = CommentMapper.toCommentDtos(comments);

        assertThat(commentDtos.size(), equalTo(2));
    }
}

