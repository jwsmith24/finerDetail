package dev.jake.finerdetail;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import dev.jake.finerdetail.entities.DutyAssignment;
import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.repos.DutyRosterRepository;
import dev.jake.finerdetail.util.constants.DetailType;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DutyRosterHttpTests {

    private final static Logger log = LoggerFactory.getLogger(DutyRosterHttpTests.class);

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private DutyRosterRepository dutyRosterRepository;


    @BeforeEach
    void resetDatabase() {
        dutyRosterRepository.deleteAll();
        dutyRosterRepository.save(new DutyRoster(DetailType.CQ_NCO, "CQ NCO in Building 12345"));
        dutyRosterRepository.save(new DutyRoster(DetailType.CQ_RUNNER, "CQ Runner in Building 12345"));
        dutyRosterRepository.save(new DutyRoster(DetailType.SD_NCO, "SD NCO in Building 67890"));
        dutyRosterRepository.save(new DutyRoster(DetailType.SD_RUNNER, "SD Runner in Building 67890"));
    }

    private Long getIdOfFirstRoster() {
        List<DutyRoster> rosters = dutyRosterRepository.findAll();
        Long firstId = rosters.getFirst().getId();
        log.info("First roster id is {}", firstId);

        return firstId;
    }

    @Test
    void getAllRostersShouldReturnCorrectNumberOfItems() {

        ResponseEntity<String> response = restTemplate.getForEntity("/rosters", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        int rosterCount = context.read("$.length()");

        assertThat(rosterCount).isEqualTo(4);
    }

    @Test
    void getRosterByIdShouldReturnRosterThatExists() {

        Long id = getIdOfFirstRoster();
        String path = String.format("/rosters/%d", id);

        ResponseEntity<DutyRoster> response = restTemplate.getForEntity(path, DutyRoster.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DutyRoster roster = response.getBody();
        assertThat(roster).isNotNull();
        assertThat(roster.getId()).isEqualTo(id);
    }

    @Test
    void shouldNotReturnRosterThatDoesNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity("/rosters/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateNewRoster() {
        DutyRoster newRoster = new DutyRoster(DetailType.ROAD_GUARD, "Road guard - report to SD " + "desk at Building 67890");

        ResponseEntity<String> response = restTemplate.postForEntity("/rosters", newRoster, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // make sure we can access the new roster via the location header
        URI location = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldUpdateExistingRoster() {

        // save an existing roster
        DutyRoster savedRoster = dutyRosterRepository.save(new DutyRoster(DetailType.CQ_NCO, "old" + " description"));
        Long id = savedRoster.getId();

        // create updated duty roster
        DutyRoster updatedRoster = new DutyRoster(DetailType.ROAD_GUARD, "This is an updated " + "description");
        // simulate put request
        HttpEntity<DutyRoster> entity = new HttpEntity<>(updatedRoster);
        ResponseEntity<DutyRoster> updatedResponse = restTemplate.exchange("/rosters/" + id, HttpMethod.PUT, entity, DutyRoster.class);

        // validate updates
        assertThat(updatedResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        DutyRoster validated = restTemplate.getForEntity("/rosters/" + id, DutyRoster.class).getBody();
        assertThat(validated).isNotNull();
        assertThat(validated.getDescription()).isEqualTo("This is an updated description");
    }

    @Test
    void updateShouldFailIfTargetDoesNotExist() {

        // make a roster that doesn't exist
        long fakeId = 99L;
        DutyRoster newRoster = new DutyRoster(DetailType.ROAD_GUARD, "new description");

        // simulate put request
        HttpEntity<DutyRoster> entity = new HttpEntity<>(newRoster);
        ResponseEntity<DutyRoster> response = restTemplate.exchange("/rosters/" + fakeId, HttpMethod.PUT, entity, DutyRoster.class);

        // make sure 404 was returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }


    @Test
    void shouldCreateANewAssignment() {

        // get a saved roster
        Long firstId = getIdOfFirstRoster();
        String assignmentPath = String.format("/rosters/%d/assignments", firstId);

        // create a new assignment
        DutyAssignment newAssignment = new DutyAssignment(LocalDate.of(2025, 7, 25), DetailType.CQ_NCO);

        // add assignment to existing roster
        ResponseEntity<DutyRoster> response = restTemplate.postForEntity(assignmentPath, newAssignment, DutyRoster.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        // check if roster has the new assignment
        String rosterPath = String.format("/rosters/%d", firstId);
        DutyRoster updatedRoster = restTemplate.getForObject(rosterPath, DutyRoster.class);

        List<DutyAssignment> assignments = updatedRoster.getDutyAssignments();
        assertThat(assignments).isNotNull().hasSize(1);
        assertThat(assignments.getFirst().getDetailType()).isEqualTo(DetailType.CQ_NCO);
    }

    @Test
    void shouldGetAllAssignments() {
        // get a saved roster
        Long firstId = getIdOfFirstRoster();
        String assignmentPath = String.format("/rosters/%d/assignments", firstId);

        // add new assignment to saved roster
        restTemplate.postForEntity(assignmentPath, new DutyAssignment(LocalDate.of(2025, 7, 25), DetailType.ROAD_GUARD), DutyAssignment.class);

        // validate roster has new assignment
        String rosterPath = String.format("/rosters/%d", firstId);
        DutyRoster updatedRoster = restTemplate.getForObject(rosterPath, DutyRoster.class);

        assertThat(updatedRoster).isNotNull();
        assertThat(updatedRoster.getDutyAssignments()).hasSize(1);

    }

    // todo: update assignment (with put), delete all for rosters, assignments

    @Test
    void shouldDeleteOneRoster() {
        Long firstId = getIdOfFirstRoster();
        String path = String.format("/rosters/%d", firstId);

        // delete roster and verify 204 is returned
        ResponseEntity<Void> response = restTemplate.exchange(path, HttpMethod.DELETE, null,
                Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // verify roster does not exist and 404 is returned
        ResponseEntity<DutyRoster> rosterResponse = restTemplate.getForEntity(path,
                DutyRoster.class);
        assertThat(rosterResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteAllRosters() {

    }

}
