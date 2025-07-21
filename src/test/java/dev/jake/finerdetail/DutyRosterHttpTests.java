package dev.jake.finerdetail;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.repos.DutyRosterRepository;
import dev.jake.finerdetail.util.constants.DetailType;
import java.net.URI;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DutyRosterHttpTests {


    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private DutyRosterRepository dutyRosterRepository;

    @Test
    void getAllRostersShouldReturnCorrectNumberOfItems() {

        ResponseEntity<String> response = restTemplate.getForEntity("/rosters", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        int rosterCount = context.read("$.length()");

        assertThat(rosterCount).isEqualTo(4);

        JSONArray rosterIds = context.read("$..id");
        assertThat(rosterIds).containsExactlyInAnyOrder(1,2,3,4);

    }

    @Test
    void getRosterByIdShouldReturnRosterThatExists() {
        ResponseEntity<String> response = restTemplate.getForEntity("/rosters/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        Number id = context.read("$.id");
        String type = context.read("$.detailType");

        assertThat(id).isEqualTo(1);
        assertThat(type).isEqualTo("CQ_NCO");
    }

    @Test
    void shouldNotReturnRosterThatDoesNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity("/rosters/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateNewRoster() {
        DutyRoster newRoster = new DutyRoster(DetailType.ROAD_GUARD, "Road guard - report to SD " +
                "desk at Building 67890");

        ResponseEntity<String> response = restTemplate.postForEntity("/rosters", newRoster,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // make sure we can access the new roster via the location header
        URI location = response.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.getForEntity(location, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldUpdateExistingRoster() {

        // save an existing roster
        DutyRoster savedRoster = dutyRosterRepository.save(new DutyRoster(DetailType.CQ_NCO, "old" +
                " description"));
        Long id = savedRoster.getId();

        // create updated duty roster
        DutyRoster updatedRoster = new DutyRoster(DetailType.ROAD_GUARD, "This is an updated " +
                "description");
        // simulate put request
        HttpEntity<DutyRoster> entity = new HttpEntity<>(updatedRoster);
        ResponseEntity<DutyRoster> updatedResponse = restTemplate
                .exchange("/rosters/" + id, HttpMethod.PUT, entity, DutyRoster.class);

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
        ResponseEntity<DutyRoster> response = restTemplate
                .exchange("/rosters/" + fakeId, HttpMethod.PUT, entity, DutyRoster.class);

        // make sure 404 was returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    //todo: tests for getting an assignment, getting all assignments, adding an assignment,
    // removing an assignment, removing all assignments

}
