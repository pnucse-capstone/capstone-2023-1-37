package com.example.p2k.course;

import com.example.p2k._core.util.PageData;
import com.example.p2k.user.User;
import com.example.p2k.vm.Vm;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public class CourseResponse {

    @Getter
    public static class VmCoursesDTO {

        private final List<CourseDTO> courses;

        public VmCoursesDTO(List<Course> courses) {
            this.courses = courses.stream().map(CourseDTO::new).toList();
        }

        @Getter
        public static class CourseDTO {
            private final Long id;
            private final String name;

            public CourseDTO(Course course) {
                this.id = course.getId();
                this.name = course.getName();
            }
        }
    }

    @Getter
    public static class FindCoursesDTO {

        private final PageData pageData;
        private final List<CourseDTO> courses;

        public FindCoursesDTO(Page<Course> courses, int size) {
            this.pageData = new PageData(
                    courses.hasPrevious(),
                    courses.hasNext(),
                    courses.isEmpty(),
                    courses.getNumber(),
                    courses.getTotalPages(),
                    size
            );
            this.courses = courses.getContent().stream().map(CourseDTO::new).toList();
        }

        @Getter
        public static class CourseDTO{

            private final Long id;
            private final String name;
            private final String description;

            public CourseDTO(Course course) {
                this.id = course.getId();
                this.name = course.getName();
                this.description = course.getDescription();
            }
        }
    }

    @Getter
    public static class FindById{

        private final Long id;
        private final String name;
        private final Long instructorId;

        public FindById(Course course) {
            this.id = course.getId();
            this.name = course.getName();
            this.instructorId = course.getInstructorId();
        }
    }

    @Getter
    public static class FindStudentsDTO{

        private final List<FindStudentsDTO.UserDTO> students;

        public FindStudentsDTO(List<User> students, Map<Long, List<Vm>> vmMap) {
            this.students = students.stream().map(student -> new UserDTO(student, vmMap.get(student.getId()))).toList();
        }

        @Getter
        public static class UserDTO{
            private final Long id;
            private final String name;
            private final List<VmDTO> vms;

            public UserDTO(User user, List<Vm> vms) {
                this.id = user.getId();
                this.name = user.getName();
                this.vms = vms.stream().map(VmDTO::new).toList();
            }

            @Getter
            public static class VmDTO{
                private final String name;
                private final int nodePort;
                private final String externalIp;

                public VmDTO(Vm vm){
                    this.name = vm.getVmname();
                    this.nodePort = vm.getNodePort();
                    this.externalIp = vm.getExternalNodeIp();
                }
            }
        }
    }

    @Getter
    public static class FindUsersDTO{
        private final List<UserDTO> users;

        public FindUsersDTO(List<User> users) {
            this.users = users.stream().map(UserDTO::new).toList();
        }

        @Getter
        public static class UserDTO{
            private final Long id;
            private final String name;

            public UserDTO(User user) {
                this.id = user.getId();
                this.name = user.getName();
            }
        }
    }

    @Getter
    public static class FindUnacceptedUserDTO {

        private final List<UnAcceptedUserDTO> students;

        public FindUnacceptedUserDTO(List<User> students) {
            this.students = students.stream().map(UnAcceptedUserDTO::new).toList();
        }

        @Getter
        public static class UnAcceptedUserDTO{

            private final Long id;
            private final String name;

            public UnAcceptedUserDTO(User user) {
                this.id = user.getId();
                this.name = user.getName();
            }
        }
    }
}
