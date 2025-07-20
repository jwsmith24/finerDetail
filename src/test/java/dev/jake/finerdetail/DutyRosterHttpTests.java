package dev.jake.finerdetail;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.util.constants.DetailType;
import java.net.URI;
import javax.swing.text.Document;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DutyRosterHttpTests {


    private int ROSTER_COUNT = 4;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllRostersShouldReturnCorrectNumberOfItems() {

        ResponseEntity<String> response = restTemplate.getForEntity("/rosters", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext context = JsonPath.parse(response.getBody());
        int rosterCount = context.read("$.length()");
        assertThat(rosterCount).isEqualTo(ROSTER_COUNT);

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
    void shouldAddNewRoster() {
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

}
