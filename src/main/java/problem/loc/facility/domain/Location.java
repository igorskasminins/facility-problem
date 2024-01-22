package problem.loc.facility.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Location {
    private Double lat;
    private Double lon;

    public Location(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @JsonIgnore
    private Map<Location, Double> distanceMap = new HashMap<>();

    @JsonIgnore
    private Map<Location, Integer> timeMap = new HashMap<>();

    public Double distanceTo(Location location) {
        if (!this.getDistanceMap().isEmpty()) {
            return this.distanceMap.get(location);
        } else {
            return Math.sqrt(Math.pow(this.lat - location.lat, 2)
                    + Math.pow(this.lon - location.lon,2));
        }
    }
}
