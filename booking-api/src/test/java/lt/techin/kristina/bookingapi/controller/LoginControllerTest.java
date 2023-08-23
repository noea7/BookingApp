package lt.techin.kristina.bookingapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.techin.kristina.bookingapi.model.dto.UserCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void loginThrowsExceptionWithNullOrEmptyValues() throws Exception {
        UserCredentials testUserCredentials1 = new UserCredentials("test", "");
        UserCredentials testUserCredentials2 = new UserCredentials("test", null);
        UserCredentials testUserCredentials3 = new UserCredentials("", "test");
        UserCredentials testUserCredentials4 = new UserCredentials(null, "test");

        String message = "Null or empty values should return bad request status";
        assertEquals(400, performLogin(testUserCredentials1).getResponse().getStatus(), message);
        assertEquals(400, performLogin(testUserCredentials2).getResponse().getStatus(), message);
        assertEquals(400, performLogin(testUserCredentials3).getResponse().getStatus(), message);
        assertEquals(400, performLogin(testUserCredentials4).getResponse().getStatus(), message);
    }

    @Test
    void loginThrowsExceptionWithMismatchedPassword() throws Exception {
        UserCredentials testUserCredentials1 = new UserCredentials("ghouse", "ghouse");

        String message = "Null or empty values should return bad request status";
        assertEquals(400, performLogin(testUserCredentials1).getResponse().getStatus(), message);
    }

    @Test
    void loginIsSuccessfulWIthCorrectCredentials() throws Exception {
        UserCredentials testUserCredentials1 = new UserCredentials("ghouse", "house");

        String message = "Should be able to login with correct credentials";
        assertEquals(200, performLogin(testUserCredentials1).getResponse().getStatus(), message);
    }

    public MvcResult performLogin(UserCredentials userCredentials) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCredentials))).andReturn();
    }
}