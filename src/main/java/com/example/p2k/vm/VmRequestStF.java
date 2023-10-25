package com.example.p2k.vm;

import lombok.Getter;
import lombok.Setter;

public class VmRequestStF {

    @Getter @Setter
    public static class createDTO {

        private Long id;
        private int port;
        private String password;
        private Boolean scope;
        private Boolean control;
        private int nodePort;
        private String imagePath;
    }

    @Getter @Setter
    public static class loadDTO {

        private Long id;
        private int port;
        private String password;
        private String key;
        private int nodePort;
        private Boolean scope;
        private Boolean control;
    }

    @Getter @Setter
    public static class startDTO {
        private int port;
        private String containerId;
        private String password;
        private Boolean scope;
        private Boolean control;
        private int nodePort;
    }

    @Getter @Setter
    public static class stopDTO {
        private int port;
        private String containerId;
        private int nodePort;
    }

    @Getter @Setter
    public static class saveDTO {
        private Long id;
        private int port;
        private String pwd;
        private String containerId;
        private String imageId;
        private int nodePort;
    }

    @Getter @Setter
    public static class deleteDTO {
        private Long id;
        private int port;
        private String containerId;
        private String imageId;
        private int nodePort;
    }
}
