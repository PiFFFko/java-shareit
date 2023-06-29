package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user1 = new User("user", "test@test.ru");
        em.persist(user1);
        item = new Item();
        item.setName("Дрель");
        item.setDescription("Электро дрель");
        item.setOwner(user1);
        itemRequest = new ItemRequest("Нужна дрель");
        em.persist(itemRequest);
    }

    @Test
    public void getAllUserItems() {
        em.persist(user1);
        itemRepository.save(item);
        List<Item> itemToGet = itemRepository.getAllUserItems(user1.getId());
        assertEquals(1, itemToGet.size());
        assertEquals(item.getName(), itemToGet.get(0).getName());
    }

    @Test
    public void getAllUserItemsWhenNoItems() {
        em.persist(user1);
        List<Item> itemToGet = itemRepository.getAllUserItems(user1.getId());
        assertEquals(0, itemToGet.size());
    }

    @Test
    public void successSearch() {
        item.setAvailable(true);
        em.persist(item);
        List<Item> items = itemRepository.findAllByNameContainingOrDescriptionContainingIgnoreCase(item.getName());
        assertEquals(1, items.size());
        assertEquals(items.get(0).getAvailable(), true);
        assertEquals(item.getName().toLowerCase(), items.get(0).getName().toLowerCase());
    }

    @Test
    public void successSearchWithUpperCaseArgument() {
        item.setAvailable(true);
        em.persist(item);
        List<Item> items = itemRepository.findAllByNameContainingOrDescriptionContainingIgnoreCase(item.getName().toUpperCase());
        assertEquals(1, items.size());
        assertEquals(items.get(0).getAvailable(), true);
        assertEquals(item.getName().toLowerCase(), items.get(0).getName().toLowerCase());
    }

    @Test
    public void successSearchWithDifferCaseArgument() {
        item.setAvailable(true);
        em.persist(item);
        List<Item> items = itemRepository.findAllByNameContainingOrDescriptionContainingIgnoreCase("дРеЛь");
        assertEquals(1, items.size());
        assertEquals(items.get(0).getAvailable(), true);
        assertEquals(item.getName().toLowerCase(), items.get(0).getName().toLowerCase());
    }

    @Test
    public void failSearch() {
        item.setAvailable(true);
        em.persist(item);
        List<Item> items = itemRepository.findAllByNameContainingOrDescriptionContainingIgnoreCase("Шуруповерт");
        assertEquals(0, items.size());
    }

    @Test
    public void findItemsByRequestSuccess() {
        item.setRequest(itemRequest);
        em.persist(item);
        List<Item> items = itemRepository.findItemsByRequest(itemRequest.getId());
        assertEquals(1, items.size());
        assertEquals(item.getName().toLowerCase(), items.get(0).getName().toLowerCase());
    }

    @Test
    public void findItemsByRequestFailForAnotherRequest() {
        item.setRequest(itemRequest);
        em.persist(item);
        List<Item> items = itemRepository.findItemsByRequest(itemRequest.getId() + 1);
        assertEquals(0, items.size());
    }
}