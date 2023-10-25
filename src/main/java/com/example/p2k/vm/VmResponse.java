package com.example.p2k.vm;

import lombok.Getter;

import java.util.List;

public class VmResponse {

    @Getter
    public static class FindByIdDTO {
        private final Long id;
        private final String name;
//        private final int port;
//        private final String state;
//        private final Boolean scope;
//        private final Boolean control;
//        private final String imageId;
//        private final String key;

        public FindByIdDTO(Vm vm) {
            this.id = vm.getId();
            this.name = vm.getVmname();
//            this.port = vm.getPort();
//            this.state = vm.getState();
//            this.scope = vm.getScope();
//            this.control = vm.getControl();
//            this.imageId = vm.getImageId();
//            this.key = vm.getVmKey();
        }
    }

    @Getter
    public static class FindAllDTO {
        private final List<VmDTO> vms;

        public FindAllDTO(List<Vm> vms) {
            this.vms = vms.stream().map(VmDTO::new).toList();
        }

        @Getter
        public class VmDTO{

            private final Long id;
            private final String name;
            private final int port;
            private final int nodePort;
            private final String externalIp;
            private final String state;
            private final Boolean scope;
            private final Boolean control;
            private final String imageId;
            private final String key;
            private final String courseName;
            private final String creator;
            private final String description;

            public VmDTO(Vm vm) {
                this.id = vm.getId();
                this.name = vm.getVmname();
                this.port = vm.getPort();
                this.nodePort = vm.getNodePort();
                this.externalIp = vm.getExternalNodeIp();
                this.state = vm.getState();
                this.scope = vm.getScope();
                this.control = vm.getControl();
                this.imageId = vm.getImageId();
                this.key = vm.getVmKey();
                this.courseName = vm.getCourse() != null ? vm.getCourse().getName() : null;
                this.creator = vm.getUser().getName();
                this.description = vm.getDescription();
            }
        }
    }

    @Getter
    public static class CreateDTO {

        private final String errorMsg;

        public CreateDTO(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    @Getter
    public static class UpdateDTO {

        private Long id;
        private String name;
        private String description;
        private String password;
        private Boolean scope;
        private Boolean control;
        private Long courseId;

        public UpdateDTO(Vm vm) {
            this.id = vm.getId();
            this.name = vm.getVmname();
            this.description = vm.getDescription();
            this.password = vm.getPassword();
            this.scope = vm.getScope();
            this.control = vm.getControl();
            this.courseId = vm.getCourse().getId();
        }
    }

    @Getter
    public static class LoadDTO {

        private final String errorMsg;

        public LoadDTO(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }
}
