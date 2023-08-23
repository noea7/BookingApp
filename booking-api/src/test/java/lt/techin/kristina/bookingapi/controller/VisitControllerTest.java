package lt.techin.kristina.bookingapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lt.techin.kristina.bookingapi.model.Specialist;
import lt.techin.kristina.bookingapi.model.Visit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VisitControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${upcoming.visit.number}")
    private Integer upcomingVisitNumber;

    @Test
    @WithMockUser
    void getVisitsDisplaysCorrectNumberOfVisits() throws Exception {
        String message = "Total number of visits displayed should not exceed the number of specialists (one active visit " +
                "per specialist) plus predefined number";
        MvcResult specialistsResponse = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/specialists/")).andReturn();
        int numberOfSpecialists = objectMapper.readValue(specialistsResponse.getResponse().getContentAsString(),
                new TypeReference<List<Specialist>>() {}).size();


        MvcResult visitsResponse = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/visits/")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        int numberOfDisplayedVisits = objectMapper.readValue(visitsResponse.getResponse().getContentAsString(),
                new TypeReference<List<Visit>>() {}).size();

        Assertions.assertTrue(numberOfDisplayedVisits <= numberOfSpecialists + upcomingVisitNumber, message);
    }

    @Test
    @WithMockUser
    void getVisitsBySpecialistThrowsExceptionWithInvalidSpecialistId() throws Exception {
        String message = "Invalid specialist id should return bad request status";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/visits/0")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), message);
    }

    @Test
    @WithMockUser
    void createVisitThrowsExceptionWithNullOrEmptyValues() throws Exception {
        String message = "Should return bad request status with null or empty values";
        Assertions.assertEquals(400, performBookingPost(null, "").getResponse()
                .getStatus(), message);
        Assertions.assertEquals(400, performBookingPost(null, null).getResponse()
                .getStatus(), message);
    }

    @Test
    @WithMockUser
    void createVisitThrowsExceptionWithInvalidSpecialistIdValues() throws Exception {
        String message = "Should return bad request status with invalid specialist id values";
        Assertions.assertEquals(400, performBookingPost("0", "test@gmail.com").getResponse()
                .getStatus(), message);
    }


    @Test
    @WithMockUser
    void shouldCreateAndCancelVisitsWithCorrectTestData() throws Exception {
        String testEmail = "test@gmail.com";
        String testSpecialistId = "1";
        MvcResult createResult = performBookingPost(testSpecialistId, testEmail);
        Visit visit = objectMapper.readValue(createResult.getResponse().getContentAsString(),
                new TypeReference<Visit>() {});
        String reservationCode = visit.getReservationCode();

        String createMessage = "Should create visit with valid values";
        Assertions.assertEquals(200, createResult.getResponse().getStatus(), createMessage);

        String cancelMessage = "Should cancel visit with valid values";
        MvcResult cancelResult = performBookingPatch(reservationCode, testEmail);
        Assertions.assertEquals(200, cancelResult.getResponse().getStatus(), createMessage);
    }

    @Test
    @WithMockUser
    void shouldStartAndEndVisitWithCorrectTestData() throws Exception {
        String testEmail = "test2@gmail.com";
        String testSpecialistId = "2";
        MvcResult createResult = performBookingPost(testSpecialistId, testEmail);
        Visit visit = objectMapper.readValue(createResult.getResponse().getContentAsString(),
                new TypeReference<Visit>() {});
        Long visitId = visit.getId();
        Assertions.assertEquals(200, performVisitStart(testSpecialistId, visitId).getResponse().getStatus(),
                "Should be able to start visit with correct data");
        Assertions.assertEquals(200, performVisitEnd(testSpecialistId, visitId).getResponse().getStatus(),
                "Should be able to end visit with correct data");
    }

    @Test
    @WithMockUser
    void shouldNotStartCancelledVisit() throws Exception {
        String testEmail = "test@gmail.com";
        String testSpecialistId = "1";
        MvcResult createResult = performBookingPost(testSpecialistId, testEmail);
        Visit visit = objectMapper.readValue(createResult.getResponse().getContentAsString(),
                new TypeReference<Visit>() {});
        String reservationCode = visit.getReservationCode();
        Long visitId = visit.getId();
        performBookingPatch(reservationCode, testEmail);

        Assertions.assertEquals(400, performVisitStart(testSpecialistId, visitId).getResponse().getStatus(),
                "Should not be able to start cancelled visit");
    }

    @Test
    void shouldNotBeAbleToCancelVisitWithIncorrectEmail() throws Exception {
        String testEmail = "test@gmail.com";
        String testSpecialistId = "1";
        MvcResult createResult = performBookingPost(testSpecialistId, testEmail);
        Visit visit = objectMapper.readValue(createResult.getResponse().getContentAsString(),
                new TypeReference<Visit>() {});
        String reservationCode = visit.getReservationCode();

        Assertions.assertEquals(400, performBookingPatch(reservationCode, "random@gmail.com")
                        .getResponse().getStatus(),"Should not be able to cancel visit with incorrect email");
        performBookingPatch(reservationCode, testEmail);
    }

    public MvcResult performBookingPost(String specialistId, String email) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/visits/create/" + specialistId + "?email=" + email)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    public MvcResult performBookingPatch(String reservationCode, String email) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/visits/cancel/" + reservationCode + "?email=" + email)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    public MvcResult performVisitStart(String specialistId, Long visitId) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/visits/" + specialistId + "/start/" + visitId)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    public MvcResult performVisitEnd(String specialistId, Long visitId) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/visits/" + specialistId + "/end/" + visitId)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

}