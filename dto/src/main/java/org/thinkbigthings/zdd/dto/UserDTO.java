package org.thinkbigthings.zdd.dto;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {
    public String username = "";
    public String email = "";
    public String displayName = "";
    public String phoneNumber = "";
    public String registrationTime = "";
    public int heightCm = 0;
    public Set<AddressDTO> addresses = new HashSet<>();
}
