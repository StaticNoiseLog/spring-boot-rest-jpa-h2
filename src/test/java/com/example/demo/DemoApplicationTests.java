package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@AutoConfigureMockMvc(addFilters = false) // Disable security filters
@WithMockUser(username = "testuser", password = "testpassword")
public class DemoApplicationTests {

    @Autowired
    private MockMvc mvc; // Inject a Spring MVC test MockMvc client with which we make calls to the REST endpoints.

    @Autowired
    private CatRepository catRepository;

    @BeforeEach
    public void beforeClass() {
        catRepository.deleteAll();
        Stream.of("Felix", "Garfieldx", "Whiskers")
                .forEach(n -> catRepository.save(new Cat(n)));
    }

    @Test
    public void catsFromRepo() {
        assertEquals("Number of cats in the repository not as expected.", 3, catRepository.findAll().size());
    }

    @Test
    public void catsReflectedInRead() throws Exception {
        final MediaType halJson = MediaType.parseMediaType("application/hal+json");
        this.mvc.perform(get("/cats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(halJson))
                .andExpect(
                        mvcResult -> {
                            String contentAsString = mvcResult.getResponse().getContentAsString();
                            assertEquals("Expected 3 cats", "3", contentAsString
                                    .split("totalElements")[1]
                                    .split(":")[1].trim()
                                    .split(",")[0]);
                        });
    }
}
