package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ShortItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private final EasyRandom generator = new EasyRandom();
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    @Test
    void createRequestSuccess() throws Exception {
        String moment = LocalDateTime.now().toString();
        ShortItemRequestDto shortItemRequestDto = generator.nextObject(ShortItemRequestDto.class);
        when(itemRequestService.createItemRequest(Mockito.anyLong(), Mockito.any(ShortItemRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDto itemRequestDto = new ItemRequestDto();
                    itemRequestDto.setId(generator.nextLong());
                    itemRequestDto.setDescription(shortItemRequestDto.getDescription());
                    itemRequestDto.setCreated(moment);
                    return itemRequestDto;
                });
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(shortItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.description").value(shortItemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(moment));
    }

    @Test
    void createRequestFailWithNullDescription() throws Exception {
        ShortItemRequestDto shortItemRequestDto = generator.nextObject(ShortItemRequestDto.class);
        shortItemRequestDto.setDescription(null);
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(shortItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void createRequestFailWithBlankDescription() throws Exception {
        ShortItemRequestDto shortItemRequestDto = generator.nextObject(ShortItemRequestDto.class);
        shortItemRequestDto.setDescription("");
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(shortItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void getRequestsByPagesWithoutRequestParameters() throws Exception {
        when(itemRequestService.getRequestsByPages(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDtoWithItems itemRequestDtoWithItems = generator.nextObject(ItemRequestDtoWithItems.class);
                    return List.of(itemRequestDtoWithItems);
                });
        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    void getRequestsByPagesWithRequestParameters() throws Exception {
        when(itemRequestService.getRequestsByPages(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDtoWithItems itemRequestDtoWithItems = generator.nextObject(ItemRequestDtoWithItems.class);
                    return List.of(itemRequestDtoWithItems);
                });
        mvc.perform(get("/requests/all?from=0&size=5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    void getRequestsByPagesWithIncorrectFromParameter() throws Exception {
        when(itemRequestService.getRequestsByPages(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDtoWithItems itemRequestDtoWithItems = generator.nextObject(ItemRequestDtoWithItems.class);
                    return List.of(itemRequestDtoWithItems);
                });
        mvc.perform(get("/requests/all?from=-2&size=5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getRequestsByPagesWithIncorrectSizeParameter() throws Exception {
        when(itemRequestService.getRequestsByPages(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDtoWithItems itemRequestDtoWithItems = generator.nextObject(ItemRequestDtoWithItems.class);
                    return List.of(itemRequestDtoWithItems);
                });
        mvc.perform(get("/requests/all?from=0&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getRequestsByPagesWithIncorrectParameters() throws Exception {
        when(itemRequestService.getRequestsByPages(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDtoWithItems itemRequestDtoWithItems = generator.nextObject(ItemRequestDtoWithItems.class);
                    return List.of(itemRequestDtoWithItems);
                });
        mvc.perform(get("/requests/all?from=-2&size=0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }


}