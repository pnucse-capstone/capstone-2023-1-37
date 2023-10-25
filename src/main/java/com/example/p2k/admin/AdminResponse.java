package com.example.p2k.admin;

import com.example.p2k._core.util.PageData;
import com.example.p2k.course.Course;
import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import com.example.p2k.vm.Vm;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AdminResponse {

    @Getter
    public static class UsersDTO {

        private final PageData pageData;
        private final List<UserDTO> users;

        public UsersDTO(Page<User> users, int size,
                        List<Integer> vmNums, List<Integer> courseNums, List<Integer> postNums, List<Integer> replyNums) {
            Map<Long, Integer> vmNumsMap = mapByUserId(users, vmNums);
            Map<Long, Integer> courseNumsMap = mapByUserId(users, courseNums);
            Map<Long, Integer> postNumsMap = mapByUserId(users, postNums);
            Map<Long, Integer> replyNumsMap = mapByUserId(users, replyNums);

            this.pageData = new PageData(
                    users.hasPrevious(),
                    users.hasNext(),
                    users.isEmpty(),
                    users.getNumber(),
                    users.getTotalPages(),
                    size
            );
            this.users = users.stream().map(user ->
                new UserDTO(user, vmNumsMap.get(user.getId()), courseNumsMap.get(user.getId()), postNumsMap.get(user.getId()), replyNumsMap.get(user.getId()))
            ).toList();
        }

        private Map<Long, Integer> mapByUserId(Page<User> users, List<Integer> nums) {
            return IntStream.range(0, nums.size())
                    .boxed()
                    .collect(Collectors.toMap(
                            userIdx -> users.getContent().get(userIdx).getId(),
                            nums::get
                    ));
        }

        @Getter
        public class UserDTO {
            private final Long id;
            private final String name;
            private final String email;
            private final Role role;
            private final Boolean pending;
            private final int vmNum;
            private final int courseNum;
            private final int postNum;
            private final int replyNum;
            private final LocalDate createdDate;

            public UserDTO(User user, int vmNum, int courseNum, int postNum, int replyNum) {
                this.id = user.getId();
                this.name = user.getName();
                this.email = user.getEmail();
                this.role = user.getRole();
                this.pending = user.getPending();
                this.vmNum = vmNum;
                this.courseNum = courseNum;
                this.postNum = postNum;
                this.replyNum = replyNum;
                this.createdDate = user.getCreatedDate() != null ? user.getCreatedDate().toLocalDate() : null;
            }
        }
    }

    @Getter
    public static class VmsDTO {

        private final PageData pageData;
        private final List<VmDTO> vms;

        public VmsDTO(Page<Vm> vms, int size) {
            this.pageData = new PageData(
                    vms.hasPrevious(),
                    vms.hasNext(),
                    vms.isEmpty(),
                    vms.getNumber(),
                    vms.getTotalPages(),
                    size
            );
            this.vms = vms.stream().map(VmDTO::new).toList();
        }

        @Getter
        public class VmDTO {
            private final Long id;
            private final String name;
            private final String createdBy;
            private final int nodePort;
            private final String externalIp;
            private final String course;
            private final Boolean scope;
            private final Boolean control;
            private final String state;
            private final LocalDate createdDate;

            public VmDTO(Vm vm) {
                this.id = vm.getId();
                this.name = vm.getVmname();
                this.createdBy = vm.getUser().getName();
                this.nodePort = vm.getNodePort();
                this.externalIp = vm.getExternalNodeIp();
                this.course = vm.getCourse() != null ? vm.getCourse().getName() : null;
                this.scope = vm.getScope();
                this.control = vm.getControl();
                this.state = vm.getState();
                this.createdDate = vm.getCreatedDate() != null ? vm.getCreatedDate().toLocalDate() : null;
            }
        }
    }

    @Getter
    public static class CoursesDTO {

        private final PageData pageData;
        private final List<CourseDTO> courses;

        public CoursesDTO(Page<Course> courses, int size, List<Integer> studentNums, List<String> instructorNames) {
            this.pageData = new PageData(
                    courses.hasPrevious(),
                    courses.hasNext(),
                    courses.isEmpty(),
                    courses.getNumber(),
                    courses.getTotalPages(),
                    size
            );
            this.courses = courses.stream().map(course -> {
                int index = courses.toList().indexOf(course);
                return new CourseDTO(course, instructorNames.get(index), studentNums.get(index));
            }).toList();
        }

        @Getter
        public class CourseDTO {
            private final Long id;
            private final String name;
            private final String instructor;
            private final int studentsNum;

            private final LocalDate createdDate;

            public CourseDTO(Course course, String instructorName, int studentsNum) {
                this.id = course.getId();
                this.name = course.getName();
                this.instructor = instructorName;
                this.studentsNum = studentsNum;
                this.createdDate = course.getCreatedDate() != null ? course.getCreatedDate().toLocalDate() : null;
            }
        }
    }

    @Getter
    public static class SettingDTO{
        private final int vmMaxNum;
        private final int courseCreateMaxNum;
        private final int courseApplyMaxNum;

        public SettingDTO(int vmMaxNum, int courseCreateMaxNum, int courseApplyMaxNum) {
            this.vmMaxNum = vmMaxNum;
            this.courseCreateMaxNum = courseCreateMaxNum;
            this.courseApplyMaxNum = courseApplyMaxNum;
        }
    }
}