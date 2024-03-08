package com.aldimbilet.website.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import com.aldimbilet.pojos.ActivityPojo;
import com.aldimbilet.util.Constants;

// Must give a url to NOT use load balancer (we have a gateway that routes to eureka with lb:// links)
// Otherwise it will throw "did you forget load balancer?" error
// gateway adress is in the properties (4441)
@FeignClient(url = "${gateway.adress.activityservice}", name = "ActivityClient")
public interface ActivityClient
{
	// The hello endpoint in the activityservice returns ResponseEntity
	// Your feign client methods must have the same return type and parameters and the http path
	// Just like invoking a method in java
	// path = localhost:4441/act/hello
	@GetMapping(path = "hello")
	ResponseEntity<String> sayHello(@RequestHeader(value = Constants.HEADER_STRING) String token);

	// The getActivities endpoint in the activityservice returns ResponseEntity
	// Your feign client methods must have the same return type and parameters and the http path
	// Just like invoking a method in java
	// path = localhost:4441/act/getActivities
	@GetMapping(path = "getActivities")
	ResponseEntity<List<ActivityPojo>> getActivities();

	@GetMapping(path = "getActivityInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ActivityPojo> getActivityInfo(@RequestParam Long actId);

	@GetMapping(path = "checkActivitySeatAvailable", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Boolean> checkActivitySeatAvailable(@RequestHeader(value = Constants.HEADER_STRING) String token, @RequestParam Long actId);

	@PostMapping(path = "sellSeat", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> sellSeat(@RequestHeader(value = Constants.HEADER_STRING) String token, @RequestBody Long actId);
}
