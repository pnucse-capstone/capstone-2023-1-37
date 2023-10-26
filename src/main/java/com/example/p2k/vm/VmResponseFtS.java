package com.example.p2k.vm;

import lombok.Getter;

public class VmResponseFtS {

    @Getter
    public static class createDTO {
        private int port;
        private String containerId;
        private String imageId;
    }

    @Getter
    public static class loadDTO {
        private String port;
    }

    @Getter
    public static class startDTO {

        private int port;
        private String containerId;
        private String externalNodeIp;
    }

    @Getter
    public static class stopDTO {

        private int port;
        private String containerId;
    }

    @Getter
    public static class saveDTO {
        private String containerId;
        private String imageId;
        private String loadKey;
    }

    @Getter
    public static class deleteDTO {

        private int port;
        private String containerId;
    }
}
