package com.mm.v1.device;

public class DeviceObject {
    
    private String id;
    private boolean is_active;
    private boolean is_private_session;
    private boolean is_restricted;
    private String name;
    private String type;
    private int volume_percent;
    private boolean supports_volume;

    public String getName() {
        return this.name;
    }

}
