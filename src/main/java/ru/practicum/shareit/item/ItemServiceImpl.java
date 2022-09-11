package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.InvalidParameterException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.*;


@Service

public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserServiceImpl userService,
                           BookingRepository bookingRepository,
                           UserRepository userRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public ItemDto createItem(Long id, ItemDto itemDto) {
        if (itemDto.getIsAvailable() == null) {
            throw new InvalidParameterException("Item isAvailable is empty");
        } else if (itemDto.getName() == null || itemDto.getName().equals("")) {
            throw new InvalidParameterException("Item name is empty");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().equals("")) {
            throw new InvalidParameterException("Item description is empty");
        }
        itemDto.setOwner(userService.findUserById(id));
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto)));
    }

    public ItemDto updateItem(ItemDto itemDto) {
        Item temp = itemRepository.getReferenceById(itemDto.getId());
        if (itemDto.getName() != null && !itemDto.getName().equals("")) {
            temp.setName(itemDto.getName());
        }
        if (itemDto.getIsAvailable() != null) {
            temp.setIsAvailable(itemDto.getIsAvailable());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals("")) {
            temp.setDescription(itemDto.getDescription());
        }
        if (itemDto.getOwner().getId() != null && itemDto.getOwner().getId() != 0) {
            temp.setOwner(UserMapper.toUser(itemDto.getOwner()));
        }
        if (itemDto.getRequestId() != null && itemDto.getRequestId() != 0) {
            temp.setRequestId(itemDto.getRequestId());
        }
        return ItemMapper.toItemDto(itemRepository.save(temp));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public ItemDto findItemById(Long userId, Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException("Item not found");
        }
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.getReferenceById(itemId));
        Set<CommentDto> comments = CommentMapper.toCommentDtos(commentRepository.findAllItemComments(itemId));
        for (CommentDto commentDto : comments) {
            itemDto.getComments().add(commentDto);
           for (CommentDto commentDtoName : itemDto.getComments()) {
               commentDtoName.setAuthorName(commentDtoName.getAuthor().getName());
           }
        }

        if (Objects.equals(itemRepository.getReferenceById(itemId).getOwner().getId(), userId)) {
            List<Booking> bookingPast = bookingRepository.findAllItemBookingsPast(itemId);
            if (bookingPast.size() != 0) {
                bookingPast.sort(Comparator.comparing(Booking::getStart).reversed());
                BookingDto bookingDtoPast = BookingMapper.toBookingDto(bookingPast.get(0));
                bookingDtoPast.setBookerId(bookingDtoPast.getBooker().getId());
                bookingDtoPast.setBooker(null);
                itemDto.setLastBooking(bookingDtoPast);
            }
            List<Booking> bookingFuture = bookingRepository.findAllItemBookingsFuture(itemId);
            if (bookingFuture.size() != 0) {
                bookingFuture.sort(Comparator.comparing(Booking::getStart));
                BookingDto bookingDtoFuture = BookingMapper.toBookingDto(bookingFuture.get(0));
                bookingDtoFuture.setBookerId(bookingDtoFuture.getBooker().getId());
                bookingDtoFuture.setBooker(null);
                itemDto.setNextBooking(bookingDtoFuture);
            }
        }
        return itemDto;
    }

    public void deleteItemById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> findAllItemsByOwner(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        List<ItemDto> itemDtoList = ItemMapper.toItemDtos(itemRepository.findAllItemsByOwner(id));
        for (ItemDto itemDto : itemDtoList) {
            List<Booking> bookingPast = bookingRepository.findAllItemBookingsPast(itemDto.getId());
            if (bookingPast.size() != 0) {
                bookingPast.sort(Comparator.comparing(Booking::getStart).reversed());
                BookingDto bookingDtoPast = BookingMapper.toBookingDto(bookingPast.get(0));
                bookingDtoPast.setBookerId(bookingDtoPast.getBooker().getId());
                bookingDtoPast.setBooker(null);
                itemDto.setLastBooking(bookingDtoPast);
            }
            List<Booking> bookingFuture = bookingRepository.findAllItemBookingsFuture(itemDto.getId());
            if (bookingFuture.size() != 0) {
                bookingFuture.sort(Comparator.comparing(Booking::getStart));
                BookingDto bookingDtoFuture = BookingMapper.toBookingDto(bookingFuture.get(0));
                bookingDtoFuture.setBookerId(bookingDtoFuture.getBooker().getId());
                bookingDtoFuture.setBooker(null);
                itemDto.setNextBooking(bookingDtoFuture);
            }
        }
        itemDtoList.sort(Comparator.comparing(ItemDto::getId));
        return itemDtoList;
    }

    @Override
    public List<ItemDto> getAllItemsByString(String someText) {
        List<Item> availableItems = new ArrayList<>();
        if (someText.length() > 0 && !someText.trim().equals("")) {
            for (Item itemFromStorage : itemRepository.findAll()) {
                if (itemFromStorage.getIsAvailable()
                        && (itemFromStorage.getDescription().toLowerCase().contains(someText.toLowerCase())
                        || itemFromStorage.getName().toLowerCase().contains(someText.toLowerCase()))) {
                    availableItems.add(itemFromStorage);
                }
            }
        }
        return ItemMapper.toItemDtos(availableItems);
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long itemId, Long id) {
        if (findItemById(id, itemId) != null) {
            if (!Objects.equals(findItemById(id, itemId).getOwner().getId(), id)) {
                throw new ItemNotFoundException("Вещь не принадлежит юзеру");
            }
        }
        itemDto.setId(itemId);
        if (findItemById(id, itemDto.getId()) != null) {
            ItemDto patchedItem = findItemById(id, itemDto.getId());
            if (itemDto.getName() != null) {
                patchedItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                patchedItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getIsAvailable() != null) {
                patchedItem.setIsAvailable(itemDto.getIsAvailable());
            }
            return patchedItem;
        } else {
            throw new ItemNotFoundException("Item not found");
        }
    }

    public CommentDto postComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new InvalidParameterException("Text field is empty");
        }
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException("Item not found");
        }

        List<Booking> bookings = bookingRepository.findAllItemBookings(itemId);
        for (Booking booking : bookings) {
            if (booking.getBooker().getId().equals(userId) && booking.getEnd().isBefore(LocalDateTime.now())) {
                commentDto.setItem(ItemMapper.toItemDto(itemRepository.getReferenceById(itemId)));
                commentDto.setAuthor(UserMapper.toUserDto(userRepository.getReferenceById(userId)));
                commentDto.setCreated(LocalDateTime.now());
                Comment commentTemp = commentRepository.save(CommentMapper.toComment(commentDto));
                CommentDto commentTempDto = CommentMapper.toCommentDto(commentTemp);
                commentTempDto.setAuthorName(userRepository.getReferenceById(userId).getName());
                commentTempDto.setAuthor(null);
                commentTempDto.setItem(null);
                return commentTempDto;
            } else {
                throw new InvalidParameterException("Invalid parameter");
            }
        }
        return null;
    }
}
