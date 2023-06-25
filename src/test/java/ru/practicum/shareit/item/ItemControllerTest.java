package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    private ItemDto itemDto;
    private EasyRandom generator = new EasyRandom();

    @BeforeEach
    void beforeEach() {
        itemDto = generator.nextObject(ItemDto.class);
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenAnswer(invocationOnMock -> {
                    itemDto.setId(generator.nextLong());
                    return itemDto;
                });
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void createItemWithoutHeaderShouldFail() throws Exception {
        when(itemService.createItem(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenAnswer(invocationOnMock -> {
                    itemDto.setId(generator.nextLong());
                    return itemDto;
                });
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createItemWithoutNameShouldFail() throws Exception {
        itemDto.setName(null);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createItemWithBlankNameShouldFail() throws Exception {
        itemDto.setName("");
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createItemWithoutDescriptionShouldFail() throws Exception {
        itemDto.setDescription(null);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createItemWithBlankDescriptionShouldFail() throws Exception {
        itemDto.setDescription("");
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createItemWithNoAvailable() throws Exception {
        itemDto.setAvailable(null);
        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getAllUserItems() throws Exception {
        ItemWithBookingsDto itemWithBookingsDto = generator.nextObject(ItemWithBookingsDto.class);
        when(itemService.getAllUserItems(Mockito.anyLong()))
                .thenReturn(List.of(itemWithBookingsDto));
        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.[*].id").exists());

    }


    @Test
    void getItem() throws Exception {
        ItemWithBookingsDto itemWithBookingsDto = generator.nextObject(ItemWithBookingsDto.class);
        when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemWithBookingsDto);
        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.comments").isArray());
    }


    @Test
    void searchItem() throws Exception {
        Item item = generator.nextObject(Item.class);
        when(itemService.searchItems(Mockito.anyString()))
                .thenReturn(List.of(item));
        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "parameter")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].id").exists())
                .andExpect(jsonPath("$.[*].name").exists());
    }

    @Test
    void searchItemBlankForBlankText() throws Exception {
        Item item = generator.nextObject(Item.class);
        when(itemService.searchItems(Mockito.anyString()))
                .thenReturn(List.of(item));
        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());
    }

    @Test
    void updateItemSuccess() throws Exception {
        when(itemService.updateItem(Mockito.anyLong(), Mockito.any(ItemDto.class), Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    itemDto.setId(1L);
                    return itemDto;
                });
        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

}