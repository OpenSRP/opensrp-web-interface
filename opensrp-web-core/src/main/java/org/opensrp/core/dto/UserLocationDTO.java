package org.opensrp.core.dto;

import java.util.Arrays;

public class UserLocationDTO {

    private int[] locations;
    private int userId;

    public int[] getLocations() {
        return locations;
    }

    public void setLocations(int[] locations) {
        this.locations = locations;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserLocationDTO{" +
                "locations=" + Arrays.toString(locations) +
                ", userId=" + userId +
                '}';
    }
}
